import {Component, computed, inject, signal} from '@angular/core';
import {lastValueFrom} from 'rxjs';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ServerManagerService} from '../core/server-manager.service';
import {PagedPage} from '../shared/page/paged.page';
import {ReportTableComponent} from './components/table.component';
import {ReportFilterDialog, ReportFilterReq} from './components/filter.dialog';
import {ActiveFiltersComponent, ActiveFilterItem} from '../shared/components/active-filters.component';
import {ConfirmDialog} from '../shared/confirm-dialog';
import {Report} from '../api';
import {REPORT_LINKS} from './config';

@Component({
  imports: [PagedPage, ReportTableComponent, ActiveFiltersComponent],
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
      <app-active-filters
        filters
        [items]="activeFilters()"
        (remove)="onRemoveFilter($event)"
      />
      <app-report-table table
        (rowClick)="navigate($event)"/>
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

  activeFilters = computed<ActiveFilterItem[]>(() => {
    const typeMap: Record<string, string> = {
      'RECEIPT': 'Prijemnica',
      'SHIPMENT': 'Otpremnica',
      'WORK_ORDER': 'Radni nalog',
    };
    const f = this.filter() || {};
    const items: ActiveFilterItem[] = [];
    if (f['types']?.length) {
      const mapped = f['types'].map((t: string) => typeMap[t] || t);
      items.push({ key: 'types', label: 'Tip', value: mapped.join(', ') });
    }
    if (f['company']) items.push({ key: 'company', label: 'Firma', value: f['company'] });
    if (f['date_from']) items.push({ key: 'date_from', label: 'Od', value: f['date_from'] });
    if (f['date_to']) items.push({ key: 'date_to', label: 'Do', value: f['date_to'] });
    if (f['location']) items.push({ key: 'location', label: 'Lokacija', value: f['location'] });
    if (f['signed_by']) items.push({ key: 'signed_by', label: 'Potpisao', value: f['signed_by'] });
    if (f['article_name']) items.push({ key: 'article_name', label: 'Artikal', value: f['article_name'] });
    return items;
  });

  onRemoveFilter(key: string) {
    this.filter.update(prev => ({ ...prev, [key]: undefined }));
  }

  navigate(report?: Report) {
    if (report?.id) {
      this.router.navigate([REPORT_LINKS.edit(report.id)]);
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
}
