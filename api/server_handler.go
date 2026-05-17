package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/server"
)

func (h *Handler) ListServers(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	servers, err := server.ListServers(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, servers)
}

func (h *Handler) GetServerConfig(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	cfg, err := server.GetLocalConfig(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, cfg)
}

func (h *Handler) SaveServerConfig(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd server.SaveConfigCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if err := server.SaveConfig(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
