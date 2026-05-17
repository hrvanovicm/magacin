package api

import (
	"context"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"gorm.io/gorm"

	appinfra "hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/paged"
	"hrvanovicm/magacin/internal/account"
	"hrvanovicm/magacin/internal/ws"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool { return true },
}

const requestTimeout = 5 * time.Second

type Handler struct {
	db  *gorm.DB
	hub *ws.Hub
}

func NewHandler(db *gorm.DB, hub *ws.Hub) *Handler {
	return &Handler{db: db, hub: hub}
}

func (h *Handler) SetupRouter() *gin.Engine {
	r := gin.Default()
	r.Use(cors.New(cors.Config{
		AllowAllOrigins:  true,
		AllowMethods:     []string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Authorization"},
		AllowCredentials: false,
	}))

	r.GET("/health", h.Health)
	r.GET("/ws", h.HandleWS)

	api := r.Group("/api")

	api.POST("/auth/sign-in", h.SignIn)
	api.POST("/auth/register-first-admin", h.RegisterFirstAdmin)

	g := api.Group("/", h.jwtMiddleware())

	g.GET("/accounts", h.ListAccounts)
	g.GET("/accounts/paged", h.ListAccountsPaged)
	g.GET("/accounts/:id", h.GetAccount)
	g.POST("/accounts", h.SaveAccount)
	g.POST("/accounts/change-password", h.ChangeAccountPassword)
	g.DELETE("/accounts/:id", h.DeleteAccount)

	g.GET("/articles", h.ListArticles)
	g.GET("/articles/paged", h.ListArticlesPaged)
	g.GET("/articles/export", h.ExportArticles)
	g.GET("/articles/:id", h.GetArticle)
	g.POST("/articles", h.SaveArticle)
	g.DELETE("/articles/:id", h.DeleteArticle)
	g.GET("/articles/:id/logs", h.GetArticleLogs)
	g.GET("/articles/analytics", h.GetReportAnalyticsByArticle)
	g.POST("/articles/conversions", h.SaveArticleConversion)
	g.DELETE("/articles/conversions/:id", h.DeleteArticleConversion)

	g.GET("/companies", h.ListCompanies)
	g.GET("/companies/:id", h.GetCompany)
	g.POST("/companies", h.SaveCompany)
	g.DELETE("/companies/:id", h.DeleteCompany)

	g.GET("/unit-measures", h.ListUnitMeasures)
	g.POST("/unit-measures", h.SaveUnitMeasure)
	g.DELETE("/unit-measures/:id", h.DeleteUnitMeasure)
	g.GET("/unit-measures/conversions", h.ListUnitMeasureConversions)
	g.POST("/unit-measures/conversions", h.SaveUnitMeasureConversion)
	g.DELETE("/unit-measures/conversions/:id", h.DeleteUnitMeasureConversion)

	g.GET("/reports/types", h.ListReportTypes)
	g.GET("/reports/sign-users", h.ListSignUsers)
	g.GET("/reports/locations", h.ListReportLocations)
	g.GET("/reports/next-code", h.GetNextReportCode)
	g.GET("/reports", h.ListReports)
	g.GET("/reports/paged", h.ListReportsPaged)
	g.GET("/reports/export", h.ExportReportsHTTP)
	g.POST("/reports", h.SaveReport)
	g.GET("/reports/:id", h.GetReport)
	g.DELETE("/reports/:id", h.DeleteReport)
	g.GET("/reports/:id/logs", h.GetReportLogs)
	g.GET("/reports/:id/export", h.ExportReport)
	g.GET("/reports/:id/export-work-order", h.ExportWorkOrder)

	g.GET("/activity-logs", h.GetActivityLogsPaged)

	g.GET("/notes", h.GetNote)
	g.PUT("/notes", h.SaveNote)

	g.GET("/servers", h.ListServers)
	g.GET("/servers/config", h.GetServerConfig)
	g.PUT("/servers/config", h.SaveServerConfig)

	return r
}

func (h *Handler) req(c *gin.Context) (appinfra.Request, context.CancelFunc) {
	ctx, cancel := context.WithTimeout(c.Request.Context(), requestTimeout)
	req := appinfra.Request{
		DB:  h.db,
		Ctx: ctx,
	}

	v, ok := c.Get(jwtClaimsKey)
	if ok {
		claims, ok := v.(account.JWTClaims)
		if ok {
			req.User = appinfra.User{
				ID:       claims.UserID,
				Username: claims.Username,
			}
		}
	}

	return req, cancel
}

const jwtClaimsKey = "jwtClaims"

func (h *Handler) jwtMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		auth := c.GetHeader("Authorization")
		if !strings.HasPrefix(auth, "Bearer ") {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
			return
		}
		claims, err := account.ParseJWT(strings.TrimPrefix(auth, "Bearer "))
		if err != nil {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
			return
		}
		c.Set(jwtClaimsKey, claims)
		c.Next()
	}
}

func parseUintParam(c *gin.Context, name string) (uint, bool) {
	v, err := strconv.ParseUint(c.Param(name), 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid " + name})
		return 0, false
	}
	return uint(v), true
}

func optionalQuery(c *gin.Context, key string) *string {
	v := c.Query(key)
	if v == "" {
		return nil
	}
	return &v
}

func pagedFromQuery(c *gin.Context) paged.Paged {
	page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
	limit, _ := strconv.Atoi(c.DefaultQuery("limit", "100"))
	return paged.Paged{Page: page, Limit: limit}
}
