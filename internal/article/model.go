package article

import (
	"hrvanovicm/magacin/internal/um"
)

const (
	CategoryRawMaterial = "RAW_MATERIAL"
	CategoryProduct     = "PRODUCT"
	CategoryCommercial  = "COMMERCIAL"
)

type Specification struct {
	Search       *string
	OrderBy      *string
	Categories   []string
	IsLowInStock bool
}

type Article struct {
	ID                   int64   `gorm:"column:id;primaryKey" json:"id"`
	Name                 string  `gorm:"column:name" json:"name"`
	Code                 *string `gorm:"column:code" json:"code"`
	Tags                 string  `gorm:"column:tags" json:"tags"`
	Category             string  `gorm:"column:category" json:"category"`
	InStockAmount        float32 `gorm:"column:in_stock_amount" json:"inStockAmount"`
	InStockWarningAmount float32 `gorm:"column:in_stock_warning_amount" json:"inStockWarningAmount"`

	UnitMeasureID *int64          `gorm:"column:unit_measure_id" json:"unitMeasureID"`
	UnitMeasure   *um.UnitMeasure `gorm:"foreignKey:UnitMeasureID;references:ID" json:"unitMeasure"`

	Recipes []Recipe `gorm:"foreignKey:ArticleID" json:"recipes"`
}

func (Article) TableName() string {
	return "main.articles"
}

type Recipe struct {
	ArticleID     int64   `gorm:"primaryKey;column:article_id" json:"articleId"`
	RawMaterialID int64   `gorm:"primaryKey;column:raw_material_id" json:"rawMaterialId"`
	RawMaterial   Article `gorm:"foreignKey:RawMaterialID;references:ID" json:"rawMaterial"`
	Amount        float32 `gorm:"column:amount" json:"amount"`
}

func (Recipe) TableName() string {
	return "main.article_has_recipes"
}
