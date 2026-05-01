import {Pipe, PipeTransform} from '@angular/core';
import {ReportType} from '../api';

@Pipe({name: 'reportTypeName'})
export class ReporTypeNamePipe implements PipeTransform {
  transform(value: string): any {
    switch (value) {
      case ReportType.RECEIPT:
        return 'Prijemnica';
      case ReportType.SHIPMENT:
        return 'Otpremnica';
      case ReportType.WORK_ORDER:
        return 'Radni nalog';
      default:
        return value;
    }
  }
}
