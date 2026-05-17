package api

import (
	"fmt"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/report"
)

func (h *Handler) ListReportTypes(c *gin.Context) {
	c.JSON(http.StatusOK, report.ValidListTypes)
}

func (h *Handler) ListSignUsers(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	users, err := report.ListSignUsers(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, users)
}

func (h *Handler) ListReportLocations(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	locations, err := report.ListPublishLocations(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, locations)
}

func (h *Handler) GetNextReportCode(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	reportType := c.Query("type")
	code, err := report.GetNextReportCodeForType(req, report.Type(reportType))

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"code": code})
}

func (h *Handler) ListReports(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := report.ListQuery{
		Search:  optionalQuery(c, "search"),
		OrderBy: optionalQuery(c, "order_by"),
	}

	reports, err := report.List(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, reports)
}

func (h *Handler) GetReport(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	rep, err := report.Get(req, report.GetQuery{ID: id})
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, rep)
}

func (h *Handler) GetReportAnalyticsByArticle(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	artIDStr := c.Query("article_id")
	artID, err := strconv.ParseUint(artIDStr, 10, 64)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid article_id"})
		return
	}

	result, err := report.GetAnalyticsByArticle(req, report.GetAnalyticsByArticleQuery{ArticleID: uint(artID)})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

func (h *Handler) ListReportsPaged(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	rawTypes := c.QueryArray("types")
	types := make([]report.Type, 0, len(rawTypes))
	for _, t := range rawTypes {
		types = append(types, report.Type(t))
	}

	qry := report.ListPagedQuery{
		ListQuery: report.ListQuery{
			Search:      optionalQuery(c, "search"),
			OrderBy:     optionalQuery(c, "order_by"),
			Types:       types,
			Location:    optionalQuery(c, "location"),
		},
		Paged: pagedFromQuery(c),
	}

	result, err := report.ListPaged(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

func (h *Handler) ExportReportsHTTP(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	rawTypes := c.QueryArray("types")
	types := make([]report.Type, 0, len(rawTypes))
	for _, t := range rawTypes {
		types = append(types, report.Type(t))
	}

	qry := report.ListExportQuery{
		ListQuery: report.ListQuery{
			Search:    optionalQuery(c, "search"),
			OrderBy:   optionalQuery(c, "order_by"),
			Types:     types,
			Location:  optionalQuery(c, "location"),
		},
		Format: report.FormatXLSX,
	}

	data, err := report.ListExport(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.Header("Content-Disposition", "attachment; filename=izvjestaji.xlsx")
	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
}

func (h *Handler) SaveReport(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd report.Report
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	id, err := report.Save(req, cmd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok", "id": id})
}

func (h *Handler) DeleteReport(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := report.Delete(req, report.DeleteCommand{ID: id}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) GetReportLogs(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	logs, err := report.GetLogs(req, report.GetLogsQuery{ID: int64(id)})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, logs)
}

func (h *Handler) ExportReport(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	data, err := report.GetExport(req, report.GetQuery{ID: id})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.Header("Content-Disposition", fmt.Sprintf("attachment; filename=izvjestaj-%d.xlsx", id))
	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
}

func (h *Handler) ExportWorkOrder(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	data, err := report.ExportWorkOrderXLSX(req, report.GetQuery{ID: id})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.Header("Content-Disposition", fmt.Sprintf("attachment; filename=radni-nalog-%d.xlsx", id))
	c.Data(http.StatusOK, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", data)
}
