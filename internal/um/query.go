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

	var ums []UnitMeasure
	if err := query.Find(&ums).Error; err != nil {
		return nil, err
	}

	if ums == nil {
		return []UnitMeasure{}, nil
	}

	return ums, nil
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
