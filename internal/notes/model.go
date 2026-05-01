package notes

import (
	"time"
)

type Note struct {
	SubjectType string    `gorm:"column:subject_type;primaryKey" json:"subjectType"`
	SubjectID   int64     `gorm:"column:subject_id;primaryKey" json:"subjectId"`
	Content     string    `gorm:"column:content" json:"content"`
	UpdatedAt   time.Time `gorm:"column:updated_at;autoUpdateTime" json:"updatedAt"`
}

func (Note) TableName() string {
	return "main.notes"
}
