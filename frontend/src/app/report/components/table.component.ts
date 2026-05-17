import { Component, EventEmitter, Output } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { DatePipe } from '@angular/common';
import { PagedTableBase } from '../../shared/page/paged-table.base';
import { Report, ReportType } from '../../api';

@Component({
  selector: 'app-report-table',
  providers: [{ provide: PagedTableBase, useExisting: ReportTableComponent }],
  imports: [MatTableModule, MatSort, MatSortHeader, MatIconModule, DatePipe],
  template: `
    <table mat-table [dataSource]="dataSource" class="mat-elevation-z8" matSort
           (matSortChange)="sorted.emit($event)">
      <ng-container matColumnDef="position">
        <th mat-header-cell *matHeaderCellDef>Rb.</th>
        <td mat-cell *matCellDef="let i = index">{{ i + 1 }}.</td>
      </ng-container>

      <ng-container matColumnDef="type">
        <th mat-header-cell *matHeaderCellDef mat-sort-header="type"></th>
        <td mat-cell *matCellDef="let row">
          <div class="flex flex-col items-center justify-center">
            @if (row.type === ReportType.RECEIPT) {
              <mat-icon class="text-xl text-gray-600">present_to_all</mat-icon>
              <span>Primka</span>
            } @else if (row.type === ReportType.WORK_ORDER) {
              <mat-icon class="text-xl text-gray-600">engineering</mat-icon>
              <span>Radni nalog</span>
            } @else {
              <mat-icon class="text-xl text-gray-600">local_shipping</mat-icon>
              <span>Otpremnica</span>
            }
          </div>
        </td>
      </ng-container>

      <ng-container matColumnDef="code">
        <th mat-header-cell *matHeaderCellDef mat-sort-header="code">Šifra</th>
        <td mat-cell *matCellDef="let row"><strong>{{ row.code }}</strong></td>
      </ng-container>

      <ng-container matColumnDef="company">
        <th mat-header-cell *matHeaderCellDef>Kompanija</th>
        <td mat-cell *matCellDef="let row">
          @if (row.type === ReportType.RECEIPT) {
            {{ row.receipt.supplierCompany.name }}
          } @else if (row.type === ReportType.SHIPMENT) {
            {{ row.shipment.receiptCompany.name }}
          } @else {
            —
          }
        </td>
      </ng-container>

      <ng-container matColumnDef="signedAt">
        <th mat-header-cell *matHeaderCellDef mat-sort-header="signed_at">Datum</th>
        <td mat-cell *matCellDef="let row">{{ row.signedAt | date:'longDate' }}</td>
      </ng-container>

      <ng-container matColumnDef="signedAtLocation">
        <th mat-header-cell *matHeaderCellDef mat-sort-header="signed_at_location">Lokacija</th>
        <td mat-cell *matCellDef="let row">{{ row.signedAtLocation }}</td>
      </ng-container>

      <ng-container matColumnDef="signedBy">
        <th mat-header-cell *matHeaderCellDef mat-sort-header="signed_by">Potpisao</th>
        <td mat-cell *matCellDef="let row">{{ row.signedBy }}</td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="columns; sticky: true"></tr>
      <tr mat-row *matRowDef="let row; columns: columns" (click)="rowClick.emit(row)"></tr>
    </table>
  `,
  styles: `
    :host { @apply flex flex-col h-full w-full overflow-y-scroll; }
    tr { cursor: pointer !important; }
    tr:hover { background: whitesmoke !important; }
  `,
})
export class ReportTableComponent extends PagedTableBase {
  @Output() rowClick = new EventEmitter<Report>();

  protected readonly columns = ['position', 'type', 'code', 'company', 'signedAt', 'signedAtLocation', 'signedBy'];
  protected readonly dataSource = new MatTableDataSource<Report>([]);
  protected readonly ReportType = ReportType;

  setData(data: Report[]): void {
    this.dataSource.data = data;
  }
}
