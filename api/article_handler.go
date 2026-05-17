package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/article"
)

func (h *Handler) ListArticles(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := article.ListQuery{
		Search:  optionalQuery(c, "search"),
		OrderBy: optionalQuery(c, "order_by"),
	}

	articles, err := article.List(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, articles)
}

func (h *Handler) ListArticlesPaged(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := article.ListPagedQuery{
		ListQuery: article.ListQuery{
			Search:       optionalQuery(c, "search"),
			OrderBy:      optionalQuery(c, "order_by"),
			Categories:   c.QueryArray("categories"),
			IsLowInStock: c.Query("is_low_in_stock") == "true",
		},
		Paged: pagedFromQuery(c),
	}

	result, err := article.ListPaged(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

func (h *Handler) ExportArticles(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := article.ListQuery{
		Search:       optionalQuery(c, "search"),
		OrderBy:      optionalQuery(c, "order_by"),
		Categories:   c.QueryArray("categories"),
		IsLowInStock: c.Query("is_low_in_stock") == "true",
	}

	data, err := article.GetExport(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.Header("Content-Disposition", "attachment; filename=artikli.xlsx")
	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
}

func (h *Handler) GetArticle(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	art, err := article.Get(req, article.GetQuery{ID: id})
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, art)
}

func (h *Handler) SaveArticle(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd article.SaveCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if _, err := article.Save(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) DeleteArticle(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := article.Delete(req, article.DeleteCommand{ID: id}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) GetArticleLogs(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	logs, err := article.GetLogs(req, article.GetLogsQuery{ID: int64(id)})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, logs)
}

func (h *Handler) SaveArticleConversion(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd article.Conversion
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	id, err := article.SaveConversion(req, cmd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok", "id": id})
}

func (h *Handler) DeleteArticleConversion(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := article.DeleteConversion(req, article.DeleteConversionCommand{ID: int64(id)}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
