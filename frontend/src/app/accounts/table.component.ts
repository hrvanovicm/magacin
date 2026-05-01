import {Component, EventEmitter, inject, Output} from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource,
} from '@angular/material/table';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {MatChip} from '@angular/material/chips';
import {MatTooltip} from '@angular/material/tooltip';
import {Account} from '../api';
import {PagedTableBase} from '../shared/page/paged-table.base';
import {OnlineStatusService} from '../core/online-status.service';

@Component({
  selector: 'app-account-table',
  providers: [{provide: PagedTableBase, useExisting: TableComponent}],
  imports: [
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    MatChip,
    MatTooltip,
  ],
  template: `
    <table mat-table [dataSource]="dataSource" class="mat-elevation-z8" matSort (matSortChange)="sorted.emit($event)">
      <ng-container matColumnDef="position">
        <th mat-header-cell *matHeaderCellDef>Rb.</th>
        <td mat-cell *matCellDef="let i = index">{{ i + 1 }}.</td>
      </ng-container>
      <ng-container matColumnDef="status">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let element">
          @if (isOnline(element.username)) {
            <span class="inline-block w-2.5 h-2.5 rounded-full bg-green-500"
                  [matTooltip]="'Online'"></span>
          } @else {
            <span class="inline-block w-2.5 h-2.5 rounded-full bg-gray-300"
                  [matTooltip]="'Offline'"></span>
          }
        </td>
      </ng-container>
      <ng-container matColumnDef="username">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>Korisničko ime</th>
        <td mat-cell *matCellDef="let element"><strong>{{ element.username }}</strong></td>
      </ng-container>
      <ng-container matColumnDef="role">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>Uloga</th>
        <td mat-cell *matCellDef="let element">
          @if (element.role) {
            <mat-chip>{{ element.role }}</mat-chip>
          }
        </td>
      </ng-container>
      <ng-container matColumnDef="ip_address">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>IP Adresa</th>
        <td mat-cell *matCellDef="let element">{{ element.ipAddress }}</td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="columns; sticky: true"></tr>
      <tr mat-row *matRowDef="let row; columns: columns" (click)="rowClick.emit(row)"></tr>
    </table>
  `,
  styles: `
    :host { @apply flex flex-col h-full w-full overflow-hidden; }
    tr { cursor: pointer !important; }
    tr:not(.mat-header-row):hover { background: whitesmoke !important; }
  `,
})
export class TableComponent extends PagedTableBase {
  private readonly ws = inject(OnlineStatusService);

  readonly columns = ['status', 'position', 'username', 'role', 'ip_address'];

  @Output() rowClick = new EventEmitter<Account>();

  protected dataSource = new MatTableDataSource<Account>([]);

  isOnline(username: string): boolean {
    return this.ws.connectedUsers().includes(username);
  }

  setData(data: Account[]): void {
    this.dataSource.data = data;
  }
}
