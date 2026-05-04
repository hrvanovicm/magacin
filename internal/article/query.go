package article

import (
	"fmt"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/export"
	"hrvanovicm/magacin/infra/paged"

	"gorm.io/gorm"

	"hrvanovicm/magacin/internal/activitylog"
)

func ListCategories(r app.Request) []string {
	return []string{CategoryProduct, CategoryCommercial, CategoryRawMaterial}
}

type ListQuery struct {
	Search       *string  `json:"search"`
	Categories   []string `json:"categories"`
	OrderBy      *string  `json:"order_by"`
	IsLowInStock bool     `json:"is_low_in_stock"`
}

func List(r app.Request, qry ListQuery) ([]Article, error) {
	query := r.DB.WithContext(r.Ctx).
		Preload("UnitMeasure")

	spec := Specification{
		Search:       qry.Search,
		OrderBy:      qry.OrderBy,
		Categories:   qry.Categories,
		IsLowInStock: qry.IsLowInStock,
	}
	executeFilter(query, spec)

	articles := []Article{}
	if err := query.Find(&articles).Error; err != nil {
		return nil, err
	}

	return articles, nil
}

type ListPagedQuery struct {
	ListQuery
	paged.Paged
}

func ListPaged(r app.Request, qry ListPagedQuery) (paged.PagedResult[Article], error) {
	query := r.DB.WithContext(r.Ctx).Model(&Article{})

	spec := Specification{
		Search:       qry.Search,
		OrderBy:      qry.OrderBy,
		Categories:   qry.Categories,
		IsLowInStock: qry.IsLowInStock,
	}

	executeFilter(query, spec)

	var total int64
	if err := query.Count(&total).Error; err != nil {
		return paged.NewDefaultPagedResult[Article](), err
	}

	accounts := []Article{}
	err := query.
		Preload("UnitMeasure").
		Preload("Recipes.RawMaterial.UnitMeasure").
		Limit(qry.Limit).
		Offset(qry.Paged.Offset()).
		Find(&accounts).
		Error

	if err != nil {
		return paged.NewDefaultPagedResult[Article](), err
	}

	res := paged.PagedResult[Article]{
		Content: accounts,
		Total:   total,
		Page:    qry.Page,
		Limit:   qry.Limit,
	}

	return res, nil
}

type GetQuery struct {
	ID uint
}

func Get(r app.Request, qry GetQuery) (*Article, error) {
	var acc Article

	err := r.DB.WithContext(r.Ctx).
		Preload("UnitMeasure").
		Preload("Recipes.RawMaterial.UnitMeasure").
		First(&acc, qry.ID).Error

	if err != nil {
		return nil, err
	}

	return &acc, nil
}

type GetLogsQuery struct {
	ID int64
}

func GetLogs(r app.Request, qry GetLogsQuery) ([]activitylog.Entry, error) {
	return activitylog.GetLogs(r, activitylog.GetLogsQuery{
		SubjectID:   qry.ID,
		SubjectType: activitylog.SubjectArticle,
	})
}

type GetExportQuery = ListQuery

func GetExport(r app.Request, qry GetExportQuery) ([]byte, error) {
	articles, err := List(r, qry)
	if err != nil {
		return nil, fmt.Errorf("export: list failed: %w", err)
	}

	return export.GenerateXL("Roba", func(b *export.XLBuilder) {
		b.WriteHeader("Rb.", "Naziv", "Šifra", "Tip", "Oznake", "Na stanju", "Mjerna jedinica")
		for i, rep := range articles {
			b.WriteRow(i+1, deref(&rep.Name), deref(rep.Code), deref(&rep.Category), deref(&rep.Tags), rep.InStockAmount, rep.UnitMeasure.Name)
		}
	})
}

func executeFilter(query *gorm.DB, spec Specification) {
	if spec.Search != nil && *spec.Search != "" {
		searchTerm := "%" + *spec.Search + "%"
		query = query.Where("name LIKE ? OR code LIKE ?", searchTerm, searchTerm)
	}
	if len(spec.Categories) > 0 {
		query = query.Where("category IN ?", spec.Categories)
	}
	if spec.IsLowInStock {
		query = query.Where("in_stock_amount <= in_stock_warning_amount")
	}
	if spec.OrderBy != nil && *spec.OrderBy != "" {
		query = query.Order(*spec.OrderBy)
	}
}

func deref(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}
