package company

type Specification struct {
	Search  *string
	OrderBy *string
}

type Company struct {
	ID                uint   `gorm:"column:id;primaryKey" json:"id"`
	Name              string `gorm:"column:name" json:"name"`
	InHouseProduction bool   `gorm:"column:in_house_production" json:"inHouseProduction"`
}

func (Company) TableName() string {
	return "main.companies"
}
