import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {
  Account,
  AccountAdminChangePasswordRequest,
  AccountChangePasswordRequest,
  AccountDeleteRequest,
  AccountGetRequest,
  AccountListPagedRequest,
  AccountListRequest,
  AccountSaveRequest,
  AccountService,
  AccountSignInRequest,
  AccountSignInResult,
  Paged,
} from '../index';

export class LocalAccountService implements AccountService {
  async signIn(req: AccountSignInRequest): Promise<AccountSignInResult> {
    return await wailsApp.SignIn(req);
  }

  async list(req: AccountListRequest): Promise<Account[]> {
    return await wailsApp.ListAccounts(req);
  }

  async listPaged(req: AccountListPagedRequest): Promise<Paged<Account>> {
    const result = await wailsApp.ListAccounts(req);
    return {
      content: result,
      total: result.length,
      page: req.page,
      limit: req.limit,
    };
  }

  async get(req: AccountGetRequest): Promise<Account | undefined> {
    return await wailsApp.GetAccount(req);
  }

  async save(req: AccountSaveRequest): Promise<void> {
    return await wailsApp.SaveAccount(req);
  }

  async delete(req: AccountDeleteRequest): Promise<void> {
    return await wailsApp.DeleteAccount(req);
  }

  async changePassword(req: AccountChangePasswordRequest): Promise<void> {
    return await wailsApp.ChangePasswordAccount(req);
  }

  async adminChangePassword(req: AccountAdminChangePasswordRequest): Promise<void> {
    return; // TODO
  }
}
