package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/company"
)

func (h *Handler) ListCompanies(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := company.ListQuery{
		Search:  optionalQuery(c, "search"),
		OrderBy: optionalQuery(c, "order_by"),
	}

	companies, err := company.List(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, companies)
}

func (h *Handler) GetCompany(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	com, err := company.Get(req, company.GetQuery{ID: id})
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, com)
}

func (h *Handler) SaveCompany(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd company.SaveCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if _, err := company.Save(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) DeleteCompany(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := company.Delete(req, company.DeleteCommand{ID: id}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
