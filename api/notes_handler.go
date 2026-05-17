package api

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/notes"
)

func (h *Handler) GetNote(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	subType := c.Query("subject_type")
	subID, _ := strconv.ParseUint(c.Query("subject_id"), 10, 64)

	qry := notes.GetQuery{
		SubjectType: subType,
		SubjectID:   int64(subID),
	}

	note, err := notes.Get(req, qry)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, note)
}

func (h *Handler) SaveNote(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	var cmd notes.SaveCommand
	if err := c.ShouldBindJSON(&cmd); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if err := notes.Save(req, cmd); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "ok"})
}
