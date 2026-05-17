import { DatePipe } from '@angular/common';
import { Component, effect, inject, input, signal, untracked } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { Article, Report, ReportType } from '../../api';
import { ServerManagerService } from '../../core/server-manager.service';
import { REPORT_LINKS } from '../../report/config';
import { ArticleAnalyticsComponent } from './analytics.component';

@Component({
  imports: [
    MatButtonModule,
    MatDivider,
    MatExpansionModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    DatePipe,
    ArticleAnalyticsComponent,
  ],
  selector: 'app-article-analytics-tab',
  template: `
    <div class="overflow-y-hidden h-full p-4 flex flex-col gap-4">
      @if (loading()) {
        <mat-progress-bar mode="indeterminate"/>
      }

      <mat-accordion>
        <mat-expansion-panel>
          <mat-expansion-panel-header>
            <mat-panel-title>Promet zadnjih 12 mjeseci</mat-panel-title>
          </mat-expansion-panel-header>
          <app-article-analytics [article]="article()" [data]="chartData()"/>
        </mat-expansion-panel>
      </mat-accordion>

      <mat-divider/>

      <div class="overflow-y-auto w-full h-full">
        <table mat-table [dataSource]="reports()" class="w-full h-full overflow-hidden">
          <ng-container matColumnDef="signedAt">
            <th mat-header-cell *matHeaderCellDef>Datum</th>
            <td mat-cell *matCellDef="let row">{{ row.signedAt | date:'dd.MM.yyyy' }}</td>
          </ng-container>
          <ng-container matColumnDef="code">
            <th mat-header-cell *matHeaderCellDef>Šifra</th>
            <td mat-cell *matCellDef="let row"><strong>{{ row.code }}</strong></td>
          </ng-container>
          <ng-container matColumnDef="type">
            <th mat-header-cell *matHeaderCellDef>Tip</th>
            <td mat-cell *matCellDef="let row">
              @if (row.type === ReportType.RECEIPT) { Primka }
              @else if (row.type === ReportType.SHIPMENT) { Otpremnica }
              @else { Radni nalog }
            </td>
          </ng-container>
          <ng-container matColumnDef="company">
            <th mat-header-cell *matHeaderCellDef>Kompanija</th>
            <td mat-cell *matCellDef="let row">
              @if (row.type === ReportType.RECEIPT) { {{ row.receipt?.supplierCompany?.name }} }
              @else if (row.type === ReportType.SHIPMENT) { {{ row.shipment?.receiptCompany?.name }} }
              @else { — }
            </td>
          </ng-container>
          <ng-container matColumnDef="amount">
            <th mat-header-cell *matHeaderCellDef>Količina</th>
            <td mat-cell *matCellDef="let row">{{ articleAmount(row) }}</td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="cols"></tr>
          <tr mat-row *matRowDef="let row; columns: cols" style="cursor:pointer" (click)="navigate(row)"></tr>
        </table>
      </div>

      <mat-paginator
        [length]="total()"
        [pageSize]="pageSize"
        [pageSizeOptions]="[10, 30, 100]"
        (page)="onPage($event)">
      </mat-paginator>
    </div>
  `,
})
export class ArticleAnalyticsTabComponent {
  private readonly router = inject(Router);
  private readonly serverManager = inject(ServerManagerService);

  readonly article = input.required<Article>();

  readonly loading = signal(false);
  readonly reports = signal<Report[]>([]);
  readonly total = signal(0);
  readonly chartData = signal<{ label: string; key: string; in: number; out: number }[]>([]);
  readonly pageSize = 10;
  readonly cols = ['signedAt', 'code', 'type', 'company', 'amount'];

  constructor() {
    effect(() => {
      const art = this.article();
      if (art) {
        untracked(() => {
          this.loadChart();
          this.loadReports(0, this.pageSize);
        });
      }
    });
  }

  articleAmount(report: Report): number | string {
    return report.articles?.find(a => a.articleId === this.article().id)?.amount ?? '—';
  }

  onPage(event: PageEvent) {
    this.loadReports(event.pageIndex, event.pageSize);
  }

  navigate(rep: Report) {
    this.router.navigate([REPORT_LINKS.edit((rep as any).id)]);
  }

  private async loadChart() {
    const data = await this.serverManager.activeServer()!.api.article.getAnalytics({ ArticleID: this.article().id });
    this.chartData.set(data);
  }

  private async loadReports(pageIndex: number, pageSize: number) {
    const from = this.fromDate();
    const paged = await this.serverManager.activeServer()!.api.report.listPaged({
      article_name: this.article().name,
      date_from: from,
      page: pageIndex + 1,
      limit: pageSize,
    } as any);
    this.reports.set(paged.content);
    this.total.set(paged.total);
  }

  private fromDate(): string {
    const now = new Date();
    const d = new Date(now.getFullYear(), now.getMonth() - 11, 1);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-01`;
  }

  protected readonly ReportType = ReportType;
}
