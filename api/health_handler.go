package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/health"
)

func (h *Handler) Health(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	result := health.CheckHealth(req)
	c.JSON(http.StatusOK, result)
}
