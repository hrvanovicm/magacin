import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';
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
  ActivityLog,
  ActivityLogGetRequest,
  ActivityLogListPagedRequest,
  ActivityLogService,
  Api,
  Article, ArticleAnalyticsRequest, ArticleAnalyticsResult,
  ArticleDeleteRequest,
  ArticleExportRequest,
  ArticleGetRequest,
  ArticleListPagedRequest,
  ArticleListRequest,
  ArticleService,
  Company,
  CompanyDeleteRequest,
  CompanyGetRequest,
  CompanyListRequest,
  CompanySaveRequest,
  CompanyService,
  Note,
  NoteGetRequest,
  NoteSaveRequest,
  NotesService,
  Paged,
  Report,
  ReportDeleteRequest,
  ReportExportRequest,
  ReportExportWorkOrderRequest,
  ReportListPagedRequest,
  ReportListRequest,
  ReportService,
  ReportType,
  UmDeleteRequest,
  UmListRequest,
  UmSaveRequest,
  UmService,
  UnitMeasure,
} from '../index';

function toParams(obj?: Record<string, any>): HttpParams {
  let params = new HttpParams();
  if (!obj) return params;
  for (const [k, v] of Object.entries(obj)) {
    if (v == null || v === '' || v === false) continue;
    if (Array.isArray(v)) {
      for (const item of v) params = params.append(k, String(item));
    } else {
      params = params.set(k, String(v));
    }
  }
  return params;
}

function apiError(err: HttpErrorResponse): never {
  throw new Error(err.error?.error ?? err.statusText);
}

function downloadBlob(blob: Blob, filename: string): void {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

class ExternalAccountService implements AccountService {
  constructor(private http: HttpClient, private base: string) {}

  signIn = (req: AccountSignInRequest) =>
    firstValueFrom(this.http.post<AccountSignInResult>(`${this.base}/api/auth/sign-in`, req));

  list = (req: AccountListRequest) =>
    firstValueFrom(this.http.get<Account[]>(`${this.base}/api/accounts`, {params: toParams(req as any)}));

  listPaged = (req: AccountListPagedRequest) =>
    firstValueFrom(this.http.get<Paged<Account>>(`${this.base}/api/accounts/paged`, {params: toParams(req as any)}));

  get = (req: AccountGetRequest) =>
    firstValueFrom(this.http.get<Account>(`${this.base}/api/accounts/${req.ID}`));

  save = (req: AccountSaveRequest) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/accounts`, req));

  delete = (req: AccountDeleteRequest) =>
    firstValueFrom(this.http.delete<void>(`${this.base}/api/accounts/${req.ID}`));

  changePassword = (req: AccountChangePasswordRequest) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/accounts/change-password`, req));

  adminChangePassword = (req: AccountAdminChangePasswordRequest) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/accounts/admin-change-password`, req));
}

class ExternalArticleService implements ArticleService {
  constructor(private http: HttpClient, private base: string) {}

  list = (req: ArticleListRequest) =>
    firstValueFrom(this.http.get<Article[]>(`${this.base}/api/articles`, {params: toParams(req as any)}));

  listPaged = (req: ArticleListPagedRequest) =>
    firstValueFrom(this.http.get<Paged<Article>>(`${this.base}/api/articles/paged`, {params: toParams(req as any)}));

  get = (req: ArticleGetRequest) =>
    firstValueFrom(this.http.get<Article>(`${this.base}/api/articles/${req.ID}`));

  save = (req: Article) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/articles`, req));

  delete = (req: ArticleDeleteRequest) =>
    firstValueFrom(this.http.delete<void>(`${this.base}/api/articles/${req.ID}`));

  export = async (req: Partial<ArticleExportRequest>) => {
    const blob = await firstValueFrom(
      this.http.get(`${this.base}/api/articles/export`, {params: toParams(req as any), responseType: 'blob'})
    );
    downloadBlob(blob, 'artikli.xlsx');
  };

  getLogs = (req: ActivityLogGetRequest) =>
    firstValueFrom(this.http.get<ActivityLog[]>(`${this.base}/api/articles/${req.ID}/logs`));

  getAnalytics = (req: ArticleAnalyticsRequest)  =>
    firstValueFrom(this.http.get<ArticleAnalyticsResult[]>(`${this.base}/api/articles/`)); 
}

class ExternalCompanyService implements CompanyService {
  constructor(private http: HttpClient, private base: string) {}

  list = (req: CompanyListRequest) =>
    firstValueFrom(this.http.get<Company[]>(`${this.base}/api/companies`, {params: toParams(req as any)}));

  get = (req: CompanyGetRequest) =>
    firstValueFrom(this.http.get<Company>(`${this.base}/api/companies/${req.ID}`));

  save = (req: CompanySaveRequest) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/companies`, req));

  delete = (req: CompanyDeleteRequest) =>
    firstValueFrom(this.http.delete<void>(`${this.base}/api/companies/${req.ID}`));
}

class ExternalReportService implements ReportService {
  constructor(private http: HttpClient, private base: string) {}

  listTypes = () =>
    firstValueFrom(this.http.get<ReportType[]>(`${this.base}/api/reports/types`));

  list = (req: ReportListRequest) =>
    firstValueFrom(this.http.get<Report[]>(`${this.base}/api/reports`, {params: toParams(req as any)}));

  listPaged = (req: ReportListPagedRequest) =>
    firstValueFrom(this.http.get<Paged<Report>>(`${this.base}/api/reports/paged`, {params: toParams(req as any)}));

  listSignUsers = () =>
    firstValueFrom(this.http.get<string[]>(`${this.base}/api/reports/sign-users`));

  listPublicLocations = () =>
    firstValueFrom(this.http.get<string[]>(`${this.base}/api/reports/locations`));

  getNextCode = async (type: ReportType) => {
    const data = await firstValueFrom(this.http.get<{code: string}>(`${this.base}/api/reports/next-code`, {params: toParams({type})}));
    return data.code;
  };

  save = (req: Report) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/reports`, req));

  delete = (req: ReportDeleteRequest) =>
    firstValueFrom(this.http.delete<void>(`${this.base}/api/reports/${req.ID}`));

  export = async (req: ReportExportRequest) => {
    const blob = await firstValueFrom(
      this.http.get(`${this.base}/api/reports/export`, {params: toParams(req as any), responseType: 'blob'})
    );
    downloadBlob(blob, 'izvjestaji.xlsx');
  };

  exportReport = async (req: {ID: number}) => {
    const blob = await firstValueFrom(
      this.http.get(`${this.base}/api/reports/${req.ID}/export`, {responseType: 'blob'})
    );
    downloadBlob(blob, `izvjestaj-${req.ID}.xlsx`);
  };

  exportWorkOrder = async (req: ReportExportWorkOrderRequest) => {
    const blob = await firstValueFrom(
      this.http.get(`${this.base}/api/reports/${req.ID}/export-work-order`, {responseType: 'blob'})
    );
    downloadBlob(blob, `radni-nalog-${req.ID}.xlsx`);
  };

  getLogs = (req: ActivityLogGetRequest) =>
    firstValueFrom(this.http.get<ActivityLog[]>(`${this.base}/api/reports/${req.ID}/logs`));
}

class ExternalActivityLogService implements ActivityLogService {
  constructor(private http: HttpClient, private base: string) {}

  listPaged = (req: ActivityLogListPagedRequest) =>
    firstValueFrom(this.http.get<Paged<ActivityLog>>(`${this.base}/api/activity-logs`, {
      params: toParams({
        subject_type: req.subjectType,
        subject_id: req.subjectId,
        search: req.search,
        page: req.page,
        limit: req.limit,
      }),
    }));
}

class ExternalNotesService implements NotesService {
  constructor(private http: HttpClient, private base: string) {}

  get = (req: NoteGetRequest) =>
    firstValueFrom(this.http.get<Note>(`${this.base}/api/notes`, {
      params: toParams({subject_type: req.SubjectType, subject_id: req.SubjectID}),
    }));

  save = (req: NoteSaveRequest) =>
    firstValueFrom(this.http.put<void>(`${this.base}/api/notes`, req));
}

class ExternalUmService implements UmService {
  constructor(private http: HttpClient, private base: string) {}

  list = (req: UmListRequest) =>
    firstValueFrom(this.http.get<UnitMeasure[]>(`${this.base}/api/unit-measures`, {params: toParams(req as any)}));

  save = (req: UmSaveRequest) =>
    firstValueFrom(this.http.post<void>(`${this.base}/api/unit-measures`, req));

  delete = (req: UmDeleteRequest) =>
    firstValueFrom(this.http.delete<void>(`${this.base}/api/unit-measures/${req.ID}`));
}

@Injectable({providedIn: 'root'})
export class ExternalApiFactory {
  private readonly http = inject(HttpClient);

  create(baseUrl: string): Api {
    return {
      account: new ExternalAccountService(this.http, baseUrl),
      article: new ExternalArticleService(this.http, baseUrl),
      company: new ExternalCompanyService(this.http, baseUrl),
      report: new ExternalReportService(this.http, baseUrl),
      um: new ExternalUmService(this.http, baseUrl),
      notes: new ExternalNotesService(this.http, baseUrl),
      activityLog: new ExternalActivityLogService(this.http, baseUrl),
    };
  }
}
