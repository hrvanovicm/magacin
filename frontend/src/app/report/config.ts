import {Routes} from '@angular/router';
import {UnsavedChangesGuard} from '../core/guards';
import {ReportEditPage} from './report-edit.page';
import {ReportIndexPage} from './report.page';

export const REPORT_LINKS = {
  index: () => `/reports`,
  create: () => `/reports/new`,
  edit: (id: number) => `/reports/${id}`,
}

export const REPORT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {path: '', component: ReportIndexPage},
      {path: 'new', component: ReportEditPage, canDeactivate: [UnsavedChangesGuard]},
      {path: ':id', component: ReportEditPage, canDeactivate: [UnsavedChangesGuard]},
    ]
  }
];
