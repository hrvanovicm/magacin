import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {
  Company,
  CompanyDeleteRequest,
  CompanyGetRequest,
  CompanyListRequest,
  CompanySaveRequest,
  CompanyService,
} from '../index';

export class LocalCompanyService implements CompanyService {
  async list(req: CompanyListRequest): Promise<Company[]> {
    return await wailsApp.ListCompanies(req);
  }

  async get(req: CompanyGetRequest): Promise<Company | undefined> {
    return await wailsApp.GetCompany(req);
  }

  async save(req: CompanySaveRequest): Promise<void> {
    return await wailsApp.SaveCompany(req);
  }

  async delete(req: CompanyDeleteRequest): Promise<void> {
    return await wailsApp.DeleteCompany(req);
  }
}
