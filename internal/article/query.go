package article

import (
	"fmt"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/export"
	"hrvanovicm/magacin/infra/paged"

	"gorm.io/gorm"

	"hrvanovicm/magacin/internal/activitylog"
	"hrvanovicm/magacin/internal/server"
	"hrvanovicm/magacin/internal/um"
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
		Preload("UnitMeasure").
		Preload("Conversions").
		Preload("Conversions.ToUnitMeasure").
		Preload("Recipes.RawMaterial.UnitMeasure")

	spec := Specification{
		Search:       qry.Search,
		OrderBy:      qry.OrderBy,
		Categories:   qry.Categories,
		IsLowInStock: qry.IsLowInStock,
	}
	executeFilter(query, spec)

	articles := make([]Article, 0)
	err := query.Find(&articles).Error
	return articles, err
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

	articles := make([]Article, 0)
	err := query.
		Preload("UnitMeasure").
		Preload("Conversions").
		Preload("Conversions.ToUnitMeasure").
		Preload("Recipes.RawMaterial.UnitMeasure").
		Limit(qry.Limit).
		Offset(qry.Paged.Offset()).
		Find(&articles).
		Error

	if err != nil {
		return paged.NewDefaultPagedResult[Article](), err
	}

	res := paged.PagedResult[Article]{
		Content: articles,
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
		Preload("Conversions").
		Preload("Conversions.ToUnitMeasure").
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

	defaultName := ""
	if cfg, err := server.GetLocalConfig(r); err == nil && cfg.CompanyName != "" {
		defaultName = cfg.CompanyName
	}

	return export.GenerateXL("Roba", func(b *export.XLBuilder) {
		b.WriteTitle("Izvještaji")
		b.WriteRow(defaultName)
		b.WriteHeader("Rb.", "Naziv", "Šifra", "Tip", "Oznake", "Na stanju", "Mjerna jedinica")
		for i, rep := range articles {
			b.WriteRow(i+1, deref(&rep.Name), deref(rep.Code), categoryLabel(deref(&rep.Category)), deref(&rep.Tags), rep.InStockAmount, rep.UnitMeasure.Name)
		}
	})
}

func executeFilter(query *gorm.DB, spec Specification) {
	if spec.Search != nil && *spec.Search != "" {
		searchTerm := "%" + *spec.Search + "%"
		query = query.Where("(code LIKE ? OR name LIKE ? OR tags LIKE ?)", searchTerm, searchTerm, searchTerm)
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
	return *s
}

func GetConversionFactor(r app.Request, articleID int64, fromUM int64, toUM int64) (float32, error) {
	if fromUM == toUM {
		return 1.0, nil
	}

	var artConv Conversion
	err := r.DB.WithContext(r.Ctx).
		Where("article_id = ? AND from_unit_measure_id = ? AND to_unit_measure_id = ?", articleID, fromUM, toUM).
		First(&artConv).Error
	if err == nil {
		return artConv.Factor, nil
	}

	var umConv um.Conversion
	err = r.DB.WithContext(r.Ctx).
		Where("from_unit_measure_id = ? AND to_unit_measure_id = ?", fromUM, toUM).
		First(&umConv).Error
	if err == nil {
		return umConv.Factor, nil
	}

	return 0, fmt.Errorf("no conversion factor found between %d and %d", fromUM, toUM)
}
