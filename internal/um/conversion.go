package um

type Conversion struct {
	ID                int64   `gorm:"column:id;primaryKey" json:"id"`
	FromUnitMeasureID int64   `gorm:"column:from_unit_measure_id" json:"fromUnitMeasureId"`
	ToUnitMeasureID   int64   `gorm:"column:to_unit_measure_id" json:"toUnitMeasureId"`
	ToUnitMeasure     *UnitMeasure `gorm:"foreignKey:ToUnitMeasureID;references:ID" json:"toUnitMeasure"`
	Factor            float32 `gorm:"column:factor" json:"factor"`
}

func (Conversion) TableName() string {
	return "main.unit_measurement_conversions"
}
