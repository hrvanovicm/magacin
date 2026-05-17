package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/um"
)

func (h *Handler) ListUnitMeasures(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := um.ListQuery{
		Search:  optionalQuery(c, "search"),
		OrderBy: optionalQuery(c, "order_by"),
	}

	units, err := um.List(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, units)
}

func (h *Handler) SaveUnitMeasure(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd um.SaveCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if _, err := um.Save(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) DeleteUnitMeasure(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := um.Delete(req, um.DeleteCommand{ID: id}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) ListUnitMeasureConversions(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	convs, err := um.ListConversions(req, um.ListConversionsQuery{})
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, convs)
}

func (h *Handler) SaveUnitMeasureConversion(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd um.SaveConversionCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	id, err := um.SaveConversion(req, cmd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok", "id": id})
}

func (h *Handler) DeleteUnitMeasureConversion(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := um.DeleteConversion(req, um.DeleteConversionCommand{ID: int64(id)}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
