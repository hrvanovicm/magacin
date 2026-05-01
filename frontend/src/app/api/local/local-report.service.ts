import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {
  ActivityLog,
  ActivityLogGetRequest,
  Paged,
  Report,
  ReportDeleteRequest,
  ReportExportRequest,
  ReportExportSingleRequest,
  ReportExportWorkOrderRequest,
  ReportListPagedRequest,
  ReportListRequest,
  ReportSaveRequest,
  ReportService,
  ReportType,
} from '../index';

export class LocalReportService implements ReportService {
  async listTypes(): Promise<ReportType[]> {
    return await wailsApp.ListReportTypes() as ReportType[];
  }

  async list(req: ReportListRequest): Promise<Report[]> {
    return await wailsApp.ListReports(req as any);
  }

  async listPaged(req: ReportListPagedRequest): Promise<Paged<Report>> {
    return await wailsApp.ListReportsPaged(req as any) as Paged<Report>;
  }

  async listSignUsers(): Promise<string[]> {
    return await wailsApp.ListSignUsers();
  }

  async listPublicLocations(): Promise<string[]> {
    return await wailsApp.ListReportPublicLocations();
  }

  async getNextCode(type: ReportType): Promise<string> {
    return await wailsApp.GetNextReportCodeForType(type);
  }

  async save(req: ReportSaveRequest): Promise<void> {
    return await wailsApp.SaveReport(req);
  }

  async delete(req: ReportDeleteRequest): Promise<void> {
    return await wailsApp.DeleteReport(req);
  }

  async export(req: ReportExportRequest): Promise<void> {
    return await wailsApp.ExportReports(req as any);
  }

  async exportReport(req: ReportExportSingleRequest): Promise<void> {
    return await wailsApp.ExportReport(req.ID);
  }

  async exportWorkOrder(req: ReportExportWorkOrderRequest): Promise<void> {
    return await wailsApp.ExportWorkOrder(req.ID);
  }

  async getLogs(req: ActivityLogGetRequest): Promise<ActivityLog[]> {
    return await wailsApp.GetReportLogs(req as any) as ActivityLog[];
  }
}
