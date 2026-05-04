import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {
  UmDeleteRequest,
  UmListRequest,
  UmSaveRequest,
  UmService,
  UnitMeasure,
} from '../index';

export class LocalUmService implements UmService {
  async list(req: UmListRequest): Promise<UnitMeasure[]> {
    return await wailsApp.ListUnitMeasurements(req);
  }

  async save(req: UmSaveRequest): Promise<number> {
    return await wailsApp.SaveUnitMeasure(req);
  }

  async delete(req: UmDeleteRequest): Promise<void> {
    return await wailsApp.DeleteUnitMeasure(req);
  }
}
