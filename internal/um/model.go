package um

type Specification struct {
	Search  *string
	OrderBy *string
}

type UnitMeasure struct {
	ID   uint   `gorm:"column:id;primaryKey" json:"id"`
	Name string `gorm:"column:name" json:"name"`
}

func (UnitMeasure) TableName() string {
	return "main.unit_measurements"
}
