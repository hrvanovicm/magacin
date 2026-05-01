import {
  activitylog,
  article,
  company,
  report,
  um,
  account,
} from '../../../wailsjs/go/models';



export type Paged<T> = {
  content: T[];
  total: number;
  page: number;
  limit: number;
}



export type ActivityLog = any;
export type ActivityLogGetRequest = { ID: number };
export type ActivityLogListPagedRequest = {
  subjectType: string;
  subjectId: number;
  search?: string;
  page: number;
  limit: number;
};

export interface ActivityLogService {
  listPaged(req: ActivityLogListPagedRequest): Promise<Paged<ActivityLog>>;
}



export type Article = article.Article;
export type ArticleRecipe = article.Recipe;

export enum ArticleCategory {
  PRODUCT = 'PRODUCT',
  COMMERCIAL = 'COMMERCIAL',
  RAW_MATERIAL = 'RAW_MATERIAL',
}

export const ArticleCategoryValues = Object.values(ArticleCategory);


export type ArticleListRequest = article.ListQuery

export type ArticleListPagedRequest = ArticleListRequest & {
  page: number;
  limit: number;
  categories?: string[];
  is_low_in_stock?: boolean;
};

export type ArticleGetRequest = { ID: number };
export type ArticleSaveRequest = Article;
export type ArticleDeleteRequest = { ID: number };
export type ArticleExportRequest = ArticleListRequest & {
  categories?: string[];
  is_low_in_stock?: boolean;
};
export type ArticleAnalyticsResult = report.ArticleAnalyticsResult
export type ArticleAnalyticsRequest = report.GetAnalyticsByArticleQuery

export interface ArticleService {
  list(req: ArticleListRequest): Promise<Article[]>;
  listPaged(req: ArticleListPagedRequest): Promise<Paged<Article>>;
  get(req: ArticleGetRequest): Promise<Article | undefined>;
  save(req: ArticleSaveRequest): Promise<void>;
  delete(req: ArticleDeleteRequest): Promise<void>;
  export(req: Partial<ArticleExportRequest>): Promise<void>;
  getLogs(req: ActivityLogGetRequest): Promise<ActivityLog[]>;
  getAnalytics(req: ArticleAnalyticsRequest): Promise<ArticleAnalyticsResult[]>;
}



export class Company extends company.Company {}

export type CompanyListRequest = {
  search?: string;
  order_by?: string;
};

export type CompanyGetRequest = { ID: number };
export type CompanySaveRequest = Company;
export type CompanyDeleteRequest = { ID: number };

export interface CompanyService {
  list(req: CompanyListRequest): Promise<Company[]>;
  get(req: CompanyGetRequest): Promise<Company | undefined>;
  save(req: CompanySaveRequest): Promise<void>;
  delete(req: CompanyDeleteRequest): Promise<void>;
}



export type Report = report.Report;
export type ReportReceipt = report.Receipt;
export type ReportShipment = report.Shipment;
export type ReportHasArticle = report.HasArticle;
export type ReportHasRecipe = report.HasRecipe;
export enum ReportType {
  RECEIPT = 'RECEIPT',
  SHIPMENT = 'SHIPMENT',
  WORK_ORDER = 'WORK_ORDER',
}
export const ReportTypeValues = Object.values(ReportType);

export type ReportListRequest = {
  search?: string;
  order_by?: string;
  company?: string;
  date_from?: string;
  date_to?: string;
  location?: string;
  signed_by?: string;
  types?: ReportType[];
  article_name?: string;
};

export type ReportListPagedRequest = ReportListRequest & {
  page: number;
  limit: number;
};

export type ReportSaveRequest = Report;
export type ReportDeleteRequest = { ID: number };
export type ReportExportRequest = ReportListRequest;
export type ReportExportWorkOrderRequest = { ID: number };
export type ReportExportSingleRequest = { ID: number };

export interface ReportService {
  listTypes(): Promise<ReportType[]>;
  list(req: ReportListRequest): Promise<Report[]>;
  listPaged(req: ReportListPagedRequest): Promise<Paged<Report>>;
  listSignUsers(): Promise<string[]>;
  listPublicLocations(): Promise<string[]>;
  getNextCode(type: ReportType): Promise<string>;
  save(req: ReportSaveRequest): Promise<void>;
  delete(req: ReportDeleteRequest): Promise<void>;
  export(req: ReportExportRequest): Promise<void>;
  exportReport(req: ReportExportSingleRequest): Promise<void>;
  exportWorkOrder(req: ReportExportWorkOrderRequest): Promise<void>;
  getLogs(req: ActivityLogGetRequest): Promise<ActivityLog[]>;
}



export type Account = account.Account;
export type AccountSignInRequest = { username: string; password: string };
export type AccountSignInResult = account.SignInResult;

export type AccountListRequest = {
  search?: string;
  order_by?: string;
};

export type AccountListPagedRequest = AccountListRequest & {
  page: number;
  limit: number;
};

export type AccountGetRequest = { ID: number };
export type AccountSaveRequest = account.SaveCommand;
export type AccountDeleteRequest = { ID: number };
export type AccountChangePasswordRequest = account.ChangePasswordCommand;
export type AccountAdminChangePasswordRequest = { id: number; new_password: string };

export interface AccountService {
  signIn(req: AccountSignInRequest): Promise<AccountSignInResult>;
  list(req: AccountListRequest): Promise<Account[]>;
  listPaged(req: AccountListPagedRequest): Promise<Paged<Account>>;
  get(req: AccountGetRequest): Promise<Account | undefined>;
  save(req: AccountSaveRequest): Promise<void>;
  delete(req: AccountDeleteRequest): Promise<void>;
  changePassword(req: AccountChangePasswordRequest): Promise<void>;
  adminChangePassword(req: AccountAdminChangePasswordRequest): Promise<void>;
}



export type UnitMeasure = um.UnitMeasure;

export type UmListRequest = {
  search?: string;
  order_by?: string;
};

export type UmSaveRequest = UnitMeasure;
export type UmDeleteRequest = { ID: number };

export interface UmService {
  list(req: UmListRequest): Promise<UnitMeasure[]>;
  save(req: UmSaveRequest): Promise<void>;
  delete(req: UmDeleteRequest): Promise<void>;
}



export type Note = { subjectType: string; subjectId: number; content: string; updatedAt: string };
export type NoteGetRequest = { SubjectType: string; SubjectID: number };
export type NoteSaveRequest = { subjectType: string; subjectId: number; content: string };

export interface NotesService {
  get(req: NoteGetRequest): Promise<Note>;
  save(req: NoteSaveRequest): Promise<void>;
}



export interface Api {
  article: ArticleService;
  company: CompanyService;
  report: ReportService;
  account: AccountService;
  um: UmService;
  notes: NotesService;
  activityLog: ActivityLogService;
}
