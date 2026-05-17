package app

import (
	"hrvanovicm/magacin/internal/company"
)

type ListCompaniesRequest = company.ListQuery

func (a *WailsApp) ListCompanies(req ListCompaniesRequest) ([]company.Company, error) {
	res, err := company.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type GetCompanyRequest = company.GetQuery

func (a *WailsApp) GetCompany(req GetCompanyRequest) (*company.Company, error) {
	res, err := company.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type SaveCompanyRequest = company.SaveCommand

func (a *WailsApp) SaveCompany(req SaveCompanyRequest) (uint, error) {
	res, err := company.Save(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type DeleteCompanyRequest = company.DeleteCommand

func (a *WailsApp) DeleteCompany(req DeleteCompanyRequest) error {
	err := company.Delete(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}
