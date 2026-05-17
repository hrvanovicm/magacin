package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/account"
)

func (h *Handler) SignIn(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd account.SignInCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	result, err := account.SignIn(req, cmd)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

func (h *Handler) RegisterFirstAdmin(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd account.SaveCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	hasAdmins, err := account.HasAdminAccounts(req)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	if hasAdmins {
		c.JSON(http.StatusBadRequest, gin.H{"error": "admin account already exists"})
		return
	}

	role := account.RoleAdmin
	cmd.ID = 0
	cmd.Role = &role
	if _, err := account.Save(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
