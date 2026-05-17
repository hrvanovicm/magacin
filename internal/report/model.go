package report

import (
	"hrvanovicm/magacin/internal/article"
	"hrvanovicm/magacin/internal/company"
)

type Type string

const (
	TypeReceipt   Type = "RECEIPT"
	TypeShipment  Type = "SHIPMENT"
	TypeWorkOrder Type = "WORK_ORDER"
)

type Specification struct {
	Search      *string
	OrderBy     *string
	Company     *string
	DateFrom    *string
	DateTo      *string
	Location    *string
	SignedBy    *string
	Types       []Type
	ArticleName *string
}

type Report struct {
	ID             int64   `gorm:"primaryKey;column:id" json:"id"`
	Type           Type    `gorm:"column:type" json:"type" log:"tipa"`
	Code           *string `gorm:"column:code" json:"code" log:"šifre"`
	Date           *string `gorm:"column:signed_at" json:"signedAt" log:"datuma"`
	PlaceOfPublish *string `gorm:"column:signed_at_location" json:"signedAtLocation" log:"mjesta izdavanja"`
	SignedByName   *string `gorm:"column:signed_by" json:"signedBy" log:"potpisnika"`

	Receipt  Receipt      `gorm:"foreignKey:ReportID" json:"receipt"`
	Shipment Shipment     `gorm:"foreignKey:ReportID" json:"shipment"`
	Articles []HasArticle `gorm:"foreignKey:ReportID" json:"articles"`
}

func (Report) TableName() string {
	return "main.reports"
}

type Receipt struct {
	ReportID            int64           `gorm:"primaryKey;column:report_id" json:"-"`
	SupplierCompanyName *string         `gorm:"column:supplier_company_name" json:"-" log:"dobavljača"`
	SupplierCompany     company.Company `gorm:"foreignKey:SupplierCompanyName;references:Name" json:"supplierCompany"`
	SupplierReportCode  *string         `gorm:"column:supplier_report_code" json:"supplierReportCode" log:"šifre dostavnice"`
}

func (Receipt) TableName() string {
	return "main.receipts"
}

type Shipment struct {
	ReportID           int64           `gorm:"primaryKey;column:report_id" json:"-"`
	ReceiptCompanyName *string         `gorm:"column:receipt_company_name" json:"-" log:"primaoca"`
	ReceiptCompany     company.Company `gorm:"foreignKey:ReceiptCompanyName;references:Name" json:"receiptCompany"`
}

func (Shipment) TableName() string {
	return "main.shipments"
}

type HasArticle struct {
	ReportID  int             `gorm:"column:report_id;primaryKey" json:"-"`
	ArticleID int             `gorm:"column:article_id;primaryKey" json:"articleId"`
	Article   article.Article `gorm:"foreignKey:ArticleID;references:ID" json:"article"`
	Amount    float32         `gorm:"column:amount" json:"amount"`
	Recipes   []HasRecipe     `gorm:"foreignKey:ReportID,ArticleID;references:ReportID,ArticleID" json:"usedRecipes"`
}

func (HasArticle) TableName() string {
	return "main.report_has_articles"
}

type HasRecipe struct {
	ReportID      int             `gorm:"column:report_id;primaryKey" json:"-"`
	ArticleID     int             `gorm:"column:article_id;primaryKey" json:"articleId"`
	RawMaterialID int             `gorm:"column:raw_material_id;primaryKey" json:"rawMaterialId"`
	RawMaterial   article.Article `gorm:"foreignKey:RawMaterialID;references:ID" json:"rawMaterial"`
	Amount        float32         `gorm:"column:amount" json:"amount"`
}

func (HasRecipe) TableName() string {
	return "main.report_has_recipes"
}
