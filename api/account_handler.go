package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/account"
)

func (h *Handler) ListAccounts(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := account.ListQuery{
		Search:  optionalQuery(c, "search"),
		OrderBy: optionalQuery(c, "order_by"),
	}

	accounts, err := account.List(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, accounts)
}

func (h *Handler) ListAccountsPaged(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	qry := account.ListPagedQuery{
		ListQuery: account.ListQuery{
			Search:  optionalQuery(c, "search"),
			OrderBy: optionalQuery(c, "order_by"),
		},
		Paged: pagedFromQuery(c),
	}

	result, err := account.ListPaged(req, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}

func (h *Handler) GetAccount(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	acc, err := account.Get(req, account.GetQuery{ID: id})
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, acc)
}

func (h *Handler) SaveAccount(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd account.SaveCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if _, err := account.Save(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) DeleteAccount(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	id, ok := parseUintParam(c, "id")
	if !ok {
		return
	}

	if err := account.Delete(req, account.DeleteCommand{ID: id}); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}

func (h *Handler) ChangeAccountPassword(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd account.ChangePasswordCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if err := account.ChangePassword(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
