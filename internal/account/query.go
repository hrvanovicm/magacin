package account

import (
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/paged"

	"gorm.io/gorm"
)

type ListQuery struct {
	Search  *string `json:"search"`
	OrderBy *string `json:"order_by"`
}

func List(r app.Request, qry ListQuery) ([]Account, error) {
	spec := Specification{
		Search:  qry.Search,
		OrderBy: qry.OrderBy,
	}

	query := r.DB.WithContext(r.Ctx)

	executeFilter(query, spec)

	accounts := make([]Account, 0)
	if err := query.Find(&accounts).Error; err != nil {
		return nil, err
	}

	return accounts, nil
}

type ListPagedQuery struct {
	ListQuery
	paged.Paged
}

func ListPaged(r app.Request, qry ListPagedQuery) (paged.PagedResult[Account], error) {
	spec := Specification{
		Search:  qry.Search,
		OrderBy: qry.OrderBy,
	}

	query := r.DB.WithContext(r.Ctx).Model(&Account{})

	executeFilter(query, spec)

	var total int64
	if err := query.Count(&total).Error; err != nil {
		return paged.NewDefaultPagedResult[Account](), err
	}

	accounts := make([]Account, 0)
	err := query.Limit(qry.Limit).Offset(qry.Paged.Offset()).Find(&accounts).Error
	if err != nil {
		return paged.NewDefaultPagedResult[Account](), err
	}

	res := paged.PagedResult[Account]{
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

func Get(r app.Request, qry GetQuery) (*Account, error) {
	var acc Account

	err := r.DB.WithContext(r.Ctx).First(&acc, qry.ID).Error
	if err != nil {
		return nil, err
	}

	return &acc, nil
}

func HasAdminAccounts(r app.Request) (bool, error) {
	var count int64

	err := r.DB.WithContext(r.Ctx).
		Model(&Account{}).
		Where("role = ?", RoleAdmin).
		Count(&count).
		Error

	return count > 0, err
}

func executeFilter(query *gorm.DB, spec Specification) {
	if spec.Search != nil && *spec.Search != "" {
		searchTerm := "%" + *spec.Search + "%"
		query = query.Where("username LIKE ?", searchTerm)
	}

	if spec.OrderBy != nil && *spec.OrderBy != "" {
		query = query.Order(*spec.OrderBy)
	}
}
