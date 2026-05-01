package app

import (
	"context"
	"time"

	"hrvanovicm/magacin/dbmanager"
	"hrvanovicm/magacin/internal/activitylog"
)

type GetActivityLogsPagedRequest struct {
	SubjectType string  `json:"subjectType"`
	SubjectID   int64   `json:"subjectId"`
	Search      *string `json:"search"`
	dbmanager.Paged
}

func (a *WailsApp) GetActivityLogsPaged(req GetActivityLogsPagedRequest) (dbmanager.PagedResult[activitylog.Entry], error) {
	ctx, cancel := context.WithTimeout(a.getRequest().Ctx, 5*time.Second)
	defer cancel()

	result, err := activitylog.GetLogsPaged(ctx, a.database(), activitylog.GetLogsPagedQuery{
		SubjectType: req.SubjectType,
		SubjectID:   req.SubjectID,
		Search:      req.Search,
		Paged:       req.Paged,
	})
	if err != nil {
		a.report(err)
		return dbmanager.NewDefaultPagedResult[activitylog.Entry](), err
	}
	return result, nil
}
