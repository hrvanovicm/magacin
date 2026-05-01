package company

import (
	"hrvanovicm/magacin/core"

	"gorm.io/gorm"
)

type ListQuery struct {
	Search  *string `json:"search"`
	OrderBy *string `json:"order_by"`
}

func List(r core.Request, qry ListQuery) ([]Company, error) {
	query := r.DB.WithContext(r.Ctx)

	spec := Specification{
		Search:  qry.Search,
		OrderBy: qry.OrderBy,
	}

	executeFilter(query, spec)

	companies := make([]Company, 0)
	if err := query.Find(&companies).Error; err != nil {
		return nil, err
	}

	return companies, nil
}

type GetQuery struct {
	ID uint
}

func Get(r core.Request, qry GetQuery) (*Company, error) {
	var acc Company

	err := r.DB.WithContext(r.Ctx).First(&acc, qry.ID).Error
	if err != nil {
		return nil, err
	}

	return &acc, nil
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
