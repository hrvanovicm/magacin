package um

import (
	"hrvanovicm/magacin/infra/app"

	"gorm.io/gorm"
)

type ListQuery struct {
	Search  *string `json:"search"`
	OrderBy *string `json:"order_by"`
}

func List(r app.Request, qry ListQuery) ([]UnitMeasure, error) {
	query := r.DB.WithContext(r.Ctx)

	spec := Specification{
		Search:  qry.Search,
		OrderBy: qry.OrderBy,
	}

	executeFilter(query, spec)

	ums := make([]UnitMeasure, 0)
	err := query.Find(&ums).Error
	return ums, err
}

func executeFilter(query *gorm.DB, spec Specification) {
	if spec.Search != nil && *spec.Search != "" {
		searchTerm := "%" + *spec.Search + "%"
		query = query.Where("name LIKE ?", searchTerm)
	}

	if spec.OrderBy != nil && *spec.OrderBy != "" {
		query = query.Order(*spec.OrderBy)
	}
}

type ListConversionsQuery struct {
}

func ListConversions(r app.Request, qry ListConversionsQuery) ([]Conversion, error) {
	convs := make([]Conversion, 0)
	err := r.DB.WithContext(r.Ctx).Preload("ToUnitMeasure").Find(&convs).Error
	return convs, err
}
