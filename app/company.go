package app

import (
	"hrvanovicm/magacin/internal/company"
)

type ListCompaniesRequest = company.ListQuery

func (a *WailsApp) ListCompanies(req ListCompaniesRequest) ([]company.Company, error) {
	companies, err := company.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return nil, err
	}

	return companies, nil
}

type GetCompanyRequest = company.GetQuery

func (a *WailsApp) GetCompany(req GetCompanyRequest) (*company.Company, error) {
	com, err := company.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return nil, err
	}

	return com, nil
}

type SaveCompanyRequest = company.SaveCommand

func (a *WailsApp) SaveCompany(req SaveCompanyRequest) error {
	if err := company.Save(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}

	return nil
}

type DeleteCompanyRequest = company.DeleteCommand

func (a *WailsApp) DeleteCompany(req DeleteCompanyRequest) error {
	if err := company.Delete(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}

	return nil
}
