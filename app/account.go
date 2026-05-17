package app

import (
	"fmt"
	"hrvanovicm/magacin/infra/paged"
	"hrvanovicm/magacin/internal/account"
)

type SignInRequest = account.SignInCommand

func (a *WailsApp) SignIn(req SignInRequest) (*account.SignInResult, error) {
	result, err := account.SignIn(a.getRequest(), req)
	if err != nil {
		a.report(err)
	} else {
		a.currentActorID = result.Account.ID
		a.currentActorUsername = result.Account.Username
	}
	return result, err
}

type ListAccountsRequest = account.ListQuery

func (a *WailsApp) ListAccounts(req ListAccountsRequest) ([]account.Account, error) {
	res, err := account.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type ListAccountsPagedRequest = account.ListPagedQuery

func (a *WailsApp) ListAccountsPaged(req ListAccountsPagedRequest) (paged.PagedResult[account.Account], error) {
	res, err := account.ListPaged(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type GetAccountRequest = account.GetQuery

func (a *WailsApp) GetAccount(req GetAccountRequest) (*account.Account, error) {
	acc, err := account.Get(a.getRequest(), req)
	if err != nil {
		return nil, err
	}

	return acc, nil
}

type SaveAccountRequest = account.SaveCommand

func (a *WailsApp) SaveAccount(req SaveAccountRequest) (uint, error) {
	return account.Save(a.getRequest(), req)
}

type DeleteAccountRequest = account.DeleteCommand

func (a *WailsApp) DeleteAccount(req DeleteAccountRequest) error {
	return account.Delete(a.getRequest(), req)
}

type ChangePasswordAccountRequest = account.ChangePasswordCommand

func (a *WailsApp) ChangePasswordAccount(req ChangePasswordAccountRequest) error {
	return account.ChangePassword(a.getRequest(), req)
}

func (a *WailsApp) HasAdminAccounts() (bool, error) {
	return account.HasAdminAccounts(a.getRequest())
}

type RegisterFirstAdminRequest = account.SaveCommand

func (a *WailsApp) RegisterFirstAdmin(req RegisterFirstAdminRequest) (uint, error) {
	hasAdmins, err := account.HasAdminAccounts(a.getRequest())
	if err != nil {
		return 0, err
	}
	if hasAdmins {
		return 0, fmt.Errorf("admin account already exists")
	}
	role := account.RoleAdmin
	req.ID = 0
	req.Role = &role
	return account.Save(a.getRequest(), req)
}
