package api

import (
	"net/http"

	"strconv"

	"github.com/gin-gonic/gin"
	"hrvanovicm/magacin/internal/activitylog"
)

func (h *Handler) GetActivityLogsPaged(c *gin.Context) {
	req, cancel := h.req(c)
	defer cancel()

	subID, _ := strconv.ParseInt(c.Query("subject_id"), 10, 64)

	qry := activitylog.GetLogsPagedQuery{
		Search:      optionalQuery(c, "search"),
		SubjectType: c.Query("subject_type"),
		SubjectID:   subID,
		Paged:       pagedFromQuery(c),
	}

	logs, err := activitylog.GetLogsPaged(req.Ctx, req.DB, qry)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, logs)
}
