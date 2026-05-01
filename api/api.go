// Package api provides the HTTP REST API for Magacin.
//
//	@title			Magacin API
//	@version		1.0
//	@description	Warehouse management system REST API
//	@host			localhost:8080
//	@BasePath		/api
//
//	@securityDefinitions.apikey	BearerAuth
//	@in							header
//	@name						Authorization
package api

import (
	"net/http"
	//"strconv"
	//"strings"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"gorm.io/gorm"
	//
	//"hrvanovicm/magacin/dbmanager"
	//"hrvanovicm/magacin/internal/account"
	//"hrvanovicm/magacin/internal/activitylog"
	//"hrvanovicm/magacin/internal/article"
	//"hrvanovicm/magacin/internal/company"
	//"hrvanovicm/magacin/internal/health"
	//"hrvanovicm/magacin/internal/notes"
	//"hrvanovicm/magacin/internal/report"
	//"hrvanovicm/magacin/internal/server"
	//"hrvanovicm/magacin/internal/um"
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

	//r.GET("/health", h.Health)
	//r.GET("/ws", h.HandleWS)
	//
	//api := r.Group("/api")
	//
	//// Public
	//api.POST("/auth/sign-in", h.SignIn)
	//api.POST("/auth/register-first-admin", h.RegisterFirstAdmin)
	//
	//// Protected
	//g := api.Group("/", h.jwtMiddleware())
	//
	//g.GET("/accounts", h.ListAccounts)
	//g.GET("/accounts/paged", h.ListAccountsPaged)
	//g.GET("/accounts/:id", h.GetAccount)
	//g.POST("/accounts", h.SaveAccount)
	//g.POST("/accounts/change-password", h.ChangeAccountPassword)
	//g.DELETE("/accounts/:id", h.DeleteAccount)
	//
	//g.GET("/articles", h.ListArticles)
	//g.GET("/articles/paged", h.ListArticlesPaged)
	//g.GET("/articles/export", h.ExportArticles)
	//g.GET("/articles/:id", h.GetArticle)
	//g.POST("/articles", h.SaveArticle)
	//g.DELETE("/articles/:id", h.DeleteArticle)
	//g.GET("/articles/:id/logs", h.GetArticleLogs)
	//
	//g.GET("/companies", h.ListCompanies)
	//g.GET("/companies/:id", h.GetCompany)
	//g.POST("/companies", h.SaveCompany)
	//
	//g.GET("/unit-measures", h.ListUnitMeasures)
	//g.POST("/unit-measures", h.SaveUnitMeasure)
	//g.DELETE("/unit-measures/:id", h.DeleteUnitMeasure)
	//
	//g.GET("/reports/types", h.ListReportTypes)
	//g.GET("/reports/sign-users", h.ListSignUsers)
	//g.GET("/reports/locations", h.ListReportLocations)
	//g.GET("/reports/next-code", h.GetNextReportCode)
	//g.GET("/reports", h.ListReports)
	//g.GET("/reports/paged", h.ListReportsPaged)
	//g.GET("/reports/export", h.ExportReportsHTTP)
	//g.POST("/reports", h.SaveReport)
	//g.DELETE("/reports/:id", h.DeleteReport)
	//g.GET("/reports/:id/logs", h.GetReportLogs)
	//g.GET("/reports/:id/export", h.ExportReport)
	//g.GET("/reports/:id/export-work-order", h.ExportWorkOrder)
	//
	//g.GET("/activity-logs", h.GetActivityLogsPaged)
	//
	//g.GET("/notes", h.GetNote)
	//g.PUT("/notes", h.SaveNote)
	//
	//g.GET("/servers", h.ListServers)
	//g.GET("/servers/config", h.GetServerConfig)
	//g.PUT("/servers/config", h.SaveServerConfig)

	return r
}

//
//func (h *Handler) ctx() (context.Context, context.CancelFunc) {
//	return context.WithTimeout(context.Background(), requestTimeout)
//}
//
//const jwtClaimsKey = "jwtClaims"
//
//func (h *Handler) jwtMiddleware() gin.HandlerFunc {
//	return func(c *gin.Context) {
//		auth := c.GetHeader("Authorization")
//		if !strings.HasPrefix(auth, "Bearer ") {
//			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
//			return
//		}
//		claims, err := account.ParseJWT(strings.TrimPrefix(auth, "Bearer "))
//		if err != nil {
//			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
//			return
//		}
//		c.Set(jwtClaimsKey, claims)
//		c.Next()
//	}
//}
//
//func actorLogger(c *gin.Context) *activitylog.Logger {
//	v, ok := c.Get(jwtClaimsKey)
//	if !ok {
//		return nil
//	}
//	claims, ok := v.(account.JWTClaims)
//	if !ok {
//		return nil
//	}
//	return activitylog.NewLogger(claims.UserID, claims.Username)
//}
//
//func parseUintParam(c *gin.Context, name string) (uint, bool) {
//	v, err := strconv.ParseUint(c.Param(name), 10, 64)
//	if err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid " + name})
//		return 0, false
//	}
//	return uint(v), true
//}
//
//func optionalQuery(c *gin.Context, key string) *string {
//	v := c.Query(key)
//	if v == "" {
//		return nil
//	}
//	return &v
//}
//
//func pagedFromQuery(c *gin.Context) dbmanager.Paged {
//	page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
//	limit, _ := strconv.Atoi(c.DefaultQuery("limit", "30"))
//	return dbmanager.Paged{Page: page, Limit: limit}
//}
//
//// ── Auth ──────────────────────────────────────────────────────────────────────
//
//// SignIn godoc
////
////	@Summary		Sign in
////	@Description	Authenticate with username and password, returns JWT token
////	@Tags			auth
////	@Accept			json
////	@Produce		json
////	@Param			body	body		account.SignInCommand	true	"Credentials"
////	@Success		200		{object}	account.SignInResult
////	@Failure		400		{object}	map[string]string
////	@Failure		401		{object}	map[string]string
////	@Router			/auth/sign-in [post]
//
//// ── Accounts ──────────────────────────────────────────────────────────────────
//
//// ListAccounts godoc
////
////	@Summary		List accounts
////	@Description	Returns all accounts, optionally filtered
////	@Tags			accounts
////	@Produce		json
////	@Param			search		query		string	false	"Search by username"
////	@Param			order_by	query		string	false	"Order by column"
////	@Success		200			{array}		account.Account
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/accounts [get]
//
//// ListAccountsPaged godoc
////
////	@Summary		List accounts paged
////	@Description	Returns paginated accounts
////	@Tags			accounts
////	@Produce		json
////	@Param			search		query		string	false	"Search by username"
////	@Param			order_by	query		string	false	"Order by column"
////	@Param			page		query		int		false	"Page number"		default(1)
////	@Param			limit		query		int		false	"Items per page"	default(30)
////	@Success		200			{object}	database.PagedResult[account.Account]
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/accounts/paged [get]
//
//// GetAccount godoc
////
////	@Summary		Get account
////	@Description	Returns a single account by ID
////	@Tags			accounts
////	@Produce		json
////	@Param			id	path		int	true	"Account ID"
////	@Success		200	{object}	account.Account
////	@Failure		400	{object}	map[string]string
////	@Failure		404	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/accounts/{id} [get]
//
//// SaveAccount godoc
////
////	@Summary		Save account
////	@Description	Create or update an account
////	@Tags			accounts
////	@Accept			json
////	@Produce		json
////	@Param			body	body		account.SaveCommand	true	"Account data"
////	@Success		200		{object}	map[string]string
////	@Failure		400		{object}	map[string]string
////	@Failure		500		{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/accounts [post]
//
//// DeleteAccount godoc
////
////	@Summary		Delete account
////	@Description	Delete an account by ID
////	@Tags			accounts
////	@Produce		json
////	@Param			id	path		int	true	"Account ID"
////	@Success		200	{object}	map[string]string
////	@Failure		400	{object}	map[string]string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/accounts/{id} [delete]
//
//// ── Articles ──────────────────────────────────────────────────────────────────
//
//// ListArticles godoc
////
////	@Summary		List articles
////	@Description	Returns all articles, optionally filtered
////	@Tags			articles
////	@Produce		json
////	@Param			search		query		string	false	"Search by name or code"
////	@Param			order_by	query		string	false	"Order by column"
////	@Success		200			{array}		article.Article
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/articles [get]
//func (h *Handler) ListArticles(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := article.ListQuery{
//		Search:  optionalQuery(c, "search"),
//		OrderBy: optionalQuery(c, "order_by"),
//	}
//
//	articles, err := article.List(article.NewApp(ctx, h.db, nil), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, articles)
//}
//
//// ListArticlesPaged godoc
////
////	@Summary		List articles paged
////	@Description	Returns paginated articles with recipes and unit measure
////	@Tags			articles
////	@Produce		json
////	@Param			search		query		string	false	"Search by name or code"
////	@Param			order_by	query		string	false	"Order by column"
////	@Param			page		query		int		false	"Page number"		default(1)
////	@Param			limit		query		int		false	"Items per page"	default(30)
////	@Success		200			{object}	database.PagedResult[article.Article]
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/articles/paged [get]
//func (h *Handler) ListArticlesPaged(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := article.ListPagedQuery{
//		ListQuery: article.ListQuery{
//			Search:       optionalQuery(c, "search"),
//			OrderBy:      optionalQuery(c, "order_by"),
//			Categories:   c.QueryArray("categories"),
//			IsLowInStock: c.Query("is_low_in_stock") == "true",
//		},
//		Paged: pagedFromQuery(c),
//	}
//
//	result, err := article.ListPaged(article.NewApp(ctx, h.db, nil), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, result)
//}
//
//func (h *Handler) ExportArticles(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := article.ListQuery{
//		Search:       optionalQuery(c, "search"),
//		OrderBy:      optionalQuery(c, "order_by"),
//		Categories:   c.QueryArray("categories"),
//		IsLowInStock: c.Query("is_low_in_stock") == "true",
//	}
//
//	data, err := article.ExportXLSX(article.NewApp(ctx, h.db, nil), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.Header("Content-Disposition", "attachment; filename=artikli.xlsx")
//	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
//}
//
//// GetArticle godoc
////
////	@Summary		Get article
////	@Description	Returns a single article by ID
////	@Tags			articles
////	@Produce		json
////	@Param			id	path		int	true	"Article ID"
////	@Success		200	{object}	article.Article
////	@Failure		400	{object}	map[string]string
////	@Failure		404	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/articles/{id} [get]
//func (h *Handler) GetArticle(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	art, err := article.Get(article.NewApp(ctx, h.db, nil), article.GetQuery{ID: id})
//	if err != nil {
//		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, art)
//}
//
//// SaveArticle godoc
////
////	@Summary		Save article
////	@Description	Create or update an article with recipes; unit measure is linked by ID only
////	@Tags			articles
////	@Accept			json
////	@Produce		json
////	@Param			body	body		article.Article	true	"Article data"
////	@Success		200		{object}	map[string]string
////	@Failure		400		{object}	map[string]string
////	@Failure		500		{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/articles [post]
//func (h *Handler) SaveArticle(c *gin.Context) {
//	var cmd article.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := article.Save(article.NewApp(ctx, h.db, actorLogger(c)), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// DeleteArticle godoc
////
////	@Summary		Delete article
////	@Description	Delete an article by ID
////	@Tags			articles
////	@Produce		json
////	@Param			id	path		int	true	"Article ID"
////	@Success		200	{object}	map[string]string
////	@Failure		400	{object}	map[string]string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/articles/{id} [delete]
//func (h *Handler) DeleteArticle(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := article.Delete(article.NewApp(ctx, h.db, actorLogger(c)), article.DeleteCommand{ID: id}); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//func (h *Handler) GetArticleLogs(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	logs, err := article.GetLogs(article.NewApp(ctx, h.db, nil), article.GetLogsQuery{ID: int64(id)})
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, logs)
//}
//
//// ── Companies ─────────────────────────────────────────────────────────────────
//
//// ListCompanies godoc
////
////	@Summary		List companies
////	@Description	Returns all companies, optionally filtered
////	@Tags			companies
////	@Produce		json
////	@Param			search		query		string	false	"Search by name"
////	@Param			order_by	query		string	false	"Order by column"
////	@Success		200			{array}		company.Company
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/companies [get]
//func (h *Handler) ListCompanies(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := company.ListQuery{
//		Search:  optionalQuery(c, "search"),
//		OrderBy: optionalQuery(c, "order_by"),
//	}
//
//	companies, err := company.List(company.NewApp(ctx, h.db), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, companies)
//}
//
//// GetCompany godoc
////
////	@Summary		Get company
////	@Description	Returns a single company by ID
////	@Tags			companies
////	@Produce		json
////	@Param			id	path		int	true	"Company ID"
////	@Success		200	{object}	company.Company
////	@Failure		400	{object}	map[string]string
////	@Failure		404	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/companies/{id} [get]
//func (h *Handler) GetCompany(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	com, err := company.Get(company.NewApp(ctx, h.db), company.GetQuery{ID: id})
//	if err != nil {
//		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, com)
//}
//
//// SaveCompany godoc
////
////	@Summary		Save company
////	@Description	Create or update a company
////	@Tags			companies
////	@Accept			json
////	@Produce		json
////	@Param			body	body		company.Company	true	"Company data"
////	@Success		200		{object}	map[string]string
////	@Failure		400		{object}	map[string]string
////	@Failure		500		{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/companies [post]
//func (h *Handler) SaveCompany(c *gin.Context) {
//	var cmd company.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := company.Save(company.NewApp(ctx, h.db), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// ── Unit Measures ─────────────────────────────────────────────────────────────
//
//// ListUnitMeasures godoc
////
////	@Summary		List unit measures
////	@Description	Returns all unit measures, optionally filtered
////	@Tags			unit-measures
////	@Produce		json
////	@Param			search		query		string	false	"Search by name"
////	@Param			order_by	query		string	false	"Order by column"
////	@Success		200			{array}		um.UnitMeasure
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/unit-measures [get]
//func (h *Handler) ListUnitMeasures(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := um.ListQuery{
//		Search:  optionalQuery(c, "search"),
//		OrderBy: optionalQuery(c, "order_by"),
//	}
//
//	units, err := um.List(um.NewApp(ctx, h.db), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, units)
//}
//
//// SaveUnitMeasure godoc
////
////	@Summary		Save unit measure
////	@Description	Create or update a unit measure
////	@Tags			unit-measures
////	@Accept			json
////	@Produce		json
////	@Param			body	body		um.UnitMeasure	true	"Unit measure data"
////	@Success		200		{object}	map[string]string
////	@Failure		400		{object}	map[string]string
////	@Failure		500		{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/unit-measures [post]
//func (h *Handler) SaveUnitMeasure(c *gin.Context) {
//	var cmd um.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := um.Save(um.NewApp(ctx, h.db), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// DeleteUnitMeasure godoc
////
////	@Summary		Delete unit measure
////	@Description	Delete a unit measure by ID
////	@Tags			unit-measures
////	@Produce		json
////	@Param			id	path		int	true	"Unit measure ID"
////	@Success		200	{object}	map[string]string
////	@Failure		400	{object}	map[string]string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/unit-measures/{id} [delete]
//func (h *Handler) DeleteUnitMeasure(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := um.Delete(um.NewApp(ctx, h.db), um.DeleteCommand{ID: id}); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// ── Reports ───────────────────────────────────────────────────────────────────
//
//// ListReportTypes godoc
////
////	@Summary		List report types
////	@Description	Returns all available report types (RECEIPT, SHIPMENT)
////	@Tags			reports
////	@Produce		json
////	@Success		200	{array}		string
////	@Security		BearerAuth
////	@Router			/reports/types [get]
//func (h *Handler) ListReportTypes(c *gin.Context) {
//	c.JSON(http.StatusOK, report.ListTypes())
//}
//
//// ListSignUsers godoc
////
////	@Summary		List sign users
////	@Description	Returns distinct signed-by names used across all reports
////	@Tags			reports
////	@Produce		json
////	@Success		200	{array}		string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports/sign-users [get]
//func (h *Handler) ListSignUsers(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	users, err := report.ListSignUsers(report.NewApp(ctx, h.db, nil))
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, users)
//}
//
//// ListReportLocations godoc
////
////	@Summary		List report locations
////	@Description	Returns distinct publish locations used across all reports
////	@Tags			reports
////	@Produce		json
////	@Success		200	{array}		string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports/locations [get]
//func (h *Handler) ListReportLocations(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	locations, err := report.ListPublishLocations(report.NewApp(ctx, h.db, nil))
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, locations)
//}
//
//// GetNextReportCode godoc
////
////	@Summary		Get next report code
////	@Description	Returns the next auto-generated report code
////	@Tags			reports
////	@Produce		json
////	@Success		200	{object}	map[string]string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports/next-code [get]
//func (h *Handler) GetNextReportCode(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	reportType := c.Query("type")
//	app := report.NewApp(ctx, h.db, nil)
//
//	var code string
//	var err error
//	if reportType != "" {
//		code, err = report.GetNextReportCodeForType(app, reportType)
//	} else {
//		code, err = report.GetNextReportCode(app)
//	}
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"code": code})
//}
//
//// ListReports godoc
////
////	@Summary		List reports
////	@Description	Returns all reports, optionally filtered
////	@Tags			reports
////	@Produce		json
////	@Param			search		query		string	false	"Search by code"
////	@Param			order_by	query		string	false	"Order by column"
////	@Success		200			{array}		report.Report
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports [get]
//func (h *Handler) ListReports(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := report.ListQuery{
//		Search:  optionalQuery(c, "search"),
//		OrderBy: optionalQuery(c, "order_by"),
//	}
//
//	reports, err := report.List(report.NewApp(ctx, h.db, nil), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, reports)
//}
//
//// ListReportsPaged godoc
////
////	@Summary		List reports paged
////	@Description	Returns paginated reports
////	@Tags			reports
////	@Produce		json
////	@Param			search		query		string	false	"Search by code"
////	@Param			order_by	query		string	false	"Order by column"
////	@Param			page		query		int		false	"Page number"		default(1)
////	@Param			limit		query		int		false	"Items per page"	default(30)
////	@Success		200			{object}	database.PagedResult[report.Report]
////	@Failure		500			{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports/paged [get]
//func (h *Handler) ListReportsPaged(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	rawTypes := c.QueryArray("types")
//	types := make([]report.ReportType, 0, len(rawTypes))
//	for _, t := range rawTypes {
//		types = append(types, report.ReportType(t))
//	}
//
//	qry := report.ListPagedQuery{
//		ListQuery: report.ListQuery{
//			Search:      optionalQuery(c, "search"),
//			OrderBy:     optionalQuery(c, "order_by"),
//			Company:     optionalQuery(c, "company"),
//			DateFrom:    optionalQuery(c, "date_from"),
//			DateTo:      optionalQuery(c, "date_to"),
//			Location:    optionalQuery(c, "location"),
//			SignedBy:    optionalQuery(c, "signed_by"),
//			Types:       types,
//			ArticleName: optionalQuery(c, "article_name"),
//		},
//		Paged: pagedFromQuery(c),
//	}
//
//	result, err := report.ListPaged(report.NewApp(ctx, h.db, nil), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, result)
//}
//
//func (h *Handler) ExportReportsHTTP(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	rawTypes2 := c.QueryArray("types")
//	types2 := make([]report.ReportType, 0, len(rawTypes2))
//	for _, t := range rawTypes2 {
//		types2 = append(types2, report.ReportType(t))
//	}
//
//	qry := report.ListQuery{
//		Search:      optionalQuery(c, "search"),
//		OrderBy:     optionalQuery(c, "order_by"),
//		Company:     optionalQuery(c, "company"),
//		DateFrom:    optionalQuery(c, "date_from"),
//		DateTo:      optionalQuery(c, "date_to"),
//		Location:    optionalQuery(c, "location"),
//		SignedBy:    optionalQuery(c, "signed_by"),
//		Types:       types2,
//		ArticleName: optionalQuery(c, "article_name"),
//	}
//
//	data, err := report.ExportXLSX(report.NewApp(ctx, h.db, nil), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.Header("Content-Disposition", "attachment; filename=izvjestaji.xlsx")
//	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
//}
//
//// SaveReport godoc
////
////	@Summary		Save report
////	@Description	Create or update a report with articles and recipes
////	@Tags			reports
////	@Accept			json
////	@Produce		json
////	@Param			body	body		report.Report	true	"Report data"
////	@Success		200		{object}	map[string]string
////	@Failure		400		{object}	map[string]string
////	@Failure		500		{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports [post]
//func (h *Handler) SaveReport(c *gin.Context) {
//	var cmd report.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := report.Save(report.NewApp(ctx, h.db, actorLogger(c)), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// DeleteReport godoc
////
////	@Summary		Delete report
////	@Description	Delete a report and its associated articles/recipes by ID
////	@Tags			reports
////	@Produce		json
////	@Param			id	path		int	true	"Report ID"
////	@Success		200	{object}	map[string]string
////	@Failure		400	{object}	map[string]string
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/reports/{id} [delete]
//func (h *Handler) DeleteReport(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := report.Delete(report.NewApp(ctx, h.db, actorLogger(c)), report.DeleteCommand{ID: id}); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//func (h *Handler) GetReportLogs(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	logs, err := report.GetLogs(report.NewApp(ctx, h.db, nil), report.GetLogsQuery{ID: int64(id)})
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, logs)
//}
//
//func (h *Handler) ExportReport(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	data, err := report.ExportReportXLSX(report.NewApp(ctx, h.db, nil), report.GetQuery{ID: id})
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.Header("Content-Disposition", "attachment; filename=izvjestaj.xlsx")
//	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
//}
//
//func (h *Handler) ExportWorkOrder(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	data, err := report.ExportWorkOrderXLSX(report.NewApp(ctx, h.db, nil), report.GetQuery{ID: id})
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.Header("Content-Disposition", "attachment; filename=radni-nalog.xlsx")
//	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
//}
//
//// ── Activity Logs ─────────────────────────────────────────────────────────────
//
//func (h *Handler) GetActivityLogsPaged(c *gin.Context) {
//	subjectType := c.Query("subject_type")
//	subjectID, err := strconv.ParseInt(c.Query("subject_id"), 10, 64)
//	if err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid subject_id"})
//		return
//	}
//
//	search := optionalQuery(c, "search")
//	paged := pagedFromQuery(c)
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	result, err := activitylog.GetLogsPaged(ctx, h.db, activitylog.GetLogsPagedQuery{
//		SubjectType: subjectType,
//		SubjectID:   subjectID,
//		Search:      search,
//		Paged:       dbmanager.Paged{Page: paged.Page, Limit: paged.Limit},
//	})
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//	c.JSON(http.StatusOK, result)
//}
//
//// ── Notes ─────────────────────────────────────────────────────────────────────
//
//func (h *Handler) GetNote(c *gin.Context) {
//	subjectType := c.Query("subject_type")
//	subjectIDStr := c.Query("subject_id")
//	subjectID, err := strconv.ParseInt(subjectIDStr, 10, 64)
//	if err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid subject_id"})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	note, err := notes.Get(notes.NewApp(ctx, h.db), notes.GetQuery{
//		SubjectType: subjectType,
//		SubjectID:   subjectID,
//	})
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//	c.JSON(http.StatusOK, note)
//}
//
//func (h *Handler) SaveNote(c *gin.Context) {
//	var cmd notes.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := notes.Save(notes.NewApp(ctx, h.db), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// ── Servers ───────────────────────────────────────────────────────────────────
//
//// ListServers godoc
////
////	@Summary		List servers
////	@Description	Returns all known remote servers
////	@Tags			servers
////	@Produce		json
////	@Success		200	{array}		server.Server
////	@Failure		500	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/servers [get]
//func (h *Handler) ListServers(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	servers, err := server.ListServers(server.NewApp(ctx, h.db))
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, servers)
//}
//
//// GetServerConfig godoc
////
////	@Summary		Get local server config
////	@Description	Returns the local server configuration
////	@Tags			servers
////	@Produce		json
////	@Success		200	{object}	server.LocalConfig
////	@Failure		404	{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/servers/config [get]
//func (h *Handler) GetServerConfig(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	cfg, err := server.GetLocalConfig(server.NewApp(ctx, h.db))
//	if err != nil {
//		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, cfg)
//}
//
//// SaveServerConfig godoc
////
////	@Summary		Save local server config
////	@Description	Update the local server configuration
////	@Tags			servers
////	@Accept			json
////	@Produce		json
////	@Param			body	body		server.LocalConfig	true	"Server config"
////	@Success		200		{object}	map[string]string
////	@Failure		400		{object}	map[string]string
////	@Failure		500		{object}	map[string]string
////	@Security		BearerAuth
////	@Router			/servers/config [put]
//func (h *Handler) SaveServerConfig(c *gin.Context) {
//	var cmd server.SaveConfigCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := server.SaveConfig(server.NewApp(ctx, h.db), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//// ── WebSocket ─────────────────────────────────────────────────────────────────
//
//func (h *Handler) HandleWS(c *gin.Context) {
//	token := c.Query("token")
//	claims, err := account.ParseJWT(token)
//	if err != nil || claims.Username == "" {
//		c.JSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
//		return
//	}
//
//	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
//	if err != nil {
//		return
//	}
//
//	h.hub.Register(conn, claims.Username)
//	defer h.hub.Unregister(conn)
//
//	for {
//		if _, _, err := conn.ReadMessage(); err != nil {
//			break
//		}
//	}
//}
//
//// ── Health ────────────────────────────────────────────────────────────────────
//
//func (h *Handler) Health(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//	result := health.CheckHealth(ctx, h.db)
//	c.JSON(http.StatusOK, gin.H{"status": "ok", "name": result.Name, "up": result.Up})
//}
//
//// ── First-time registration ───────────────────────────────────────────────────
//
//func (h *Handler) RegisterFirstAdmin(c *gin.Context) {
//	var cmd account.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	accApp := account.NewApp(ctx, h.db)
//	hasAdmins, err := account.HasAdminAccounts(accApp)
//	if err != nil || hasAdmins {
//		c.JSON(http.StatusForbidden, gin.H{"error": "admin account already exists"})
//		return
//	}
//
//	role := account.RoleAdmin
//	cmd.Role = &role
//	cmd.ID = 0
//	if err := account.Save(accApp, cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//func (h *Handler) ChangeAccountPassword(c *gin.Context) {
//	var cmd account.ChangePasswordCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := account.ChangePassword(account.NewApp(ctx, h.db), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
