import {Component, inject, signal} from '@angular/core';
import {lastValueFrom} from 'rxjs';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ServerManagerService} from '../core/server-manager.service';
import {PagedPage} from '../shared/page/paged.page';
import {ReportTableComponent} from './components/table.component';
import {ReportFilterDialog, ReportFilterReq} from './components/filter.dialog';
import {ConfirmDialog} from '../shared/confirm-dialog';
import {Report} from '../api';
import {REPORT_LINKS} from './config';

@Component({
  imports: [PagedPage, ReportTableComponent],
  template: `
    <app-paged-page
      title="Izvještaji"
      [loadCallback]="serverManager.activeServer()!.api.report.listPaged"
      [filter]="filter()"
      [reloadTrigger]="reloadTrigger()"
      (createClickCallback)="navigate()"
      (exportClickCallback)="onExportClick()"
      (filterClickCallback)="onFilterClick($event)"
    >
      <app-report-table table
        (rowClick)="navigate($event)"
        (deleteClick)="onDeleteClick($event)"/>
    </app-paged-page>
  `,
  styles: `:host { @apply flex flex-col h-full w-full overflow-hidden; }`,
})
export class ReportIndexPage {
  readonly serverManager = inject(ServerManagerService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackbar = inject(MatSnackBar);

  filter = signal<Record<string, any> | null>(null);
  reloadTrigger = signal(0);

  navigate(report?: Report) {
    if (report?.id) {
      this.router.navigate([REPORT_LINKS.edit(report.id)], {state: {report}});
    } else {
      this.router.navigate([REPORT_LINKS.create()]);
    }
  }

  onExportClick() {
    this.serverManager.activeServer()?.api.report.export({});
  }

  async onFilterClick(currentReq: any) {
    const result = await lastValueFrom(
      this.dialog.open(ReportFilterDialog, {
        width: '420px',
        data: {
          search: currentReq.search,
          types: currentReq.types,
          company: currentReq.company,
          date_from: currentReq.date_from,
          date_to: currentReq.date_to,
          location: currentReq.location,
          signed_by: currentReq.signed_by,
          article_name: currentReq.article_name,
        } satisfies ReportFilterReq,
      }).afterClosed()
    );
    if (result !== undefined) this.filter.set(result);
  }

  async onDeleteClick(report: Report) {
    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: {title: 'Obriši izvještaj', message: `Da li sigurno želite obrisati izvještaj "${report.code}"?`, confirmLabel: 'Obriši'},
      }).afterClosed()
    );
    if (!confirmed) return;

    try {
      await this.serverManager.activeServer()!.api.report.delete({ID: report.id as any});
      this.reloadTrigger.update(n => n + 1);
    } catch (error) {
      this.snackbar.open(`❌ Greška pri brisanju: ${error}`);
    }
  }
}
