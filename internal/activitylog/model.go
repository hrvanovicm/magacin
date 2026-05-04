package activitylog

import (
	"context"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/paged"
	"time"

	"gorm.io/gorm"
)

const (
	SubjectArticle = "ARTICLE"
	SubjectReport  = "REPORT"
)

type ActivityLog struct {
	ID          int64     `gorm:"column:id" json:"id"`
	SubjectID   int64     `gorm:"column:subject_id" json:"subjectId"`
	SubjectType string    `gorm:"column:subject_type" json:"subjectType"`
	Description string    `gorm:"column:description" json:"description"`
	ActorID     uint      `gorm:"column:actor_id" json:"actorId"`
	CreatedAt   time.Time `gorm:"column:created_at" json:"createdAt"`
}

func (ActivityLog) TableName() string {
	return "main.activity_logs"
}

type Entry struct {
	ID            int64     `gorm:"column:id" json:"id"`
	SubjectID     int64     `gorm:"column:subject_id" json:"subjectId"`
	SubjectType   string    `gorm:"column:subject_type" json:"subjectType"`
	Description   string    `gorm:"column:description" json:"description"`
	ActorID       uint      `gorm:"column:actor_id" json:"actorId"`
	ActorUsername string    `gorm:"column:actor_username" json:"actorUsername"`
	CreatedAt     time.Time `gorm:"column:created_at" json:"createdAt"`
}

type Logger struct {
	actorID       uint
	actorUsername string
}

func NewLogger(actorID uint, actorUsername string) *Logger {
	return &Logger{actorID: actorID, actorUsername: actorUsername}
}

func (l *Logger) Log(ctx context.Context, db *gorm.DB, subjectID int64, subjectType, description string) {
	if l == nil || l.actorID == 0 {
		return
	}
	if len(description) > 250 {
		description = description[:250]
	}
	entry := ActivityLog{
		SubjectID:   subjectID,
		SubjectType: subjectType,
		Description: description,
		ActorID:     l.actorID,
	}

	db.WithContext(ctx).Create(&entry)
}

func (l *Logger) Username() string {
	if l == nil || l.actorUsername == "" {
		return "sistem"
	}
	return l.actorUsername
}

type GetLogsQuery struct {
	SubjectID   int64
	SubjectType string
}

type GetLogsPagedQuery struct {
	SubjectID   int64
	SubjectType string
	Search      *string
	paged.Paged
}

func GetLogsPaged(ctx context.Context, db *gorm.DB, qry GetLogsPagedQuery) (paged.PagedResult[Entry], error) {
	base := db.WithContext(ctx).
		Table("main.activity_logs al").
		Select("al.id, al.subject_id, al.subject_type, al.description, al.actor_id, al.created_at, COALESCE(a.username, 'Nepoznat') as actor_username").
		Joins("LEFT JOIN main.accounts a ON a.id = al.actor_id").
		Where("al.subject_id = ? AND al.subject_type = ?", qry.SubjectID, qry.SubjectType)

	if qry.Search != nil && *qry.Search != "" {
		base = base.Where("al.description LIKE ?", "%"+*qry.Search+"%")
	}

	var total int64
	if err := base.Count(&total).Error; err != nil {
		return paged.NewDefaultPagedResult[Entry](), err
	}

	var logs []Entry
	err := base.Order("al.created_at DESC").
		Limit(qry.Limit).
		Offset(qry.Paged.Offset()).
		Scan(&logs).Error
	if err != nil {
		return paged.NewDefaultPagedResult[Entry](), err
	}
	if logs == nil {
		logs = []Entry{}
	}

	return paged.PagedResult[Entry]{
		Content: logs,
		Total:   total,
		Page:    qry.Page,
		Limit:   qry.Limit,
	}, nil
}

func GetLogs(r app.Request, qry GetLogsQuery) ([]Entry, error) {
	var logs []Entry
	err := r.DB.WithContext(r.Ctx).
		Table("main.activity_logs al").
		Select("al.id, al.subject_id, al.subject_type, al.description, al.actor_id, al.created_at, COALESCE(a.username, 'Nepoznat') as actor_username").
		Joins("LEFT JOIN main.accounts a ON a.id = al.actor_id").
		Where("al.subject_id = ? AND al.subject_type = ?", qry.SubjectID, qry.SubjectType).
		Order("al.created_at DESC").
		Scan(&logs).Error
	if err != nil {
		return nil, err
	}
	if logs == nil {
		logs = []Entry{}
	}
	return logs, nil
}
