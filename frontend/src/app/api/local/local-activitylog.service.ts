import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {ActivityLog, ActivityLogListPagedRequest, ActivityLogService, Paged} from '../index';

export class LocalActivityLogService implements ActivityLogService {
  async listPaged(req: ActivityLogListPagedRequest): Promise<Paged<ActivityLog>> {
    return await wailsApp.GetActivityLogsPaged({
      subjectType: req.subjectType,
      subjectId: req.subjectId,
      search: req.search ?? null,
      page: req.page,
      limit: req.limit,
    } as any) as Paged<ActivityLog>;
  }
}
