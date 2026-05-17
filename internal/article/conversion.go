package article

import (
	"hrvanovicm/magacin/internal/um"
)

type Conversion struct {
	ID                int64   `gorm:"column:id;primaryKey" json:"id"`
	ArticleID         int64   `gorm:"column:article_id" json:"articleId"`
	FromUnitMeasureID int64   `gorm:"column:from_unit_measure_id" json:"fromUnitMeasureId"`
	ToUnitMeasureID   int64   `gorm:"column:to_unit_measure_id" json:"toUnitMeasureId"`
	ToUnitMeasure     *um.UnitMeasure `gorm:"foreignKey:ToUnitMeasureID;references:ID" json:"toUnitMeasure"`
	Factor            float32 `gorm:"column:factor" json:"factor"`
}

func (Conversion) TableName() string {
	return "main.article_unit_measurement_conversions"
}
