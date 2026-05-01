import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ReadableArticleCategoryPipePipe, ReadableArticleInStockAmountPipePipe} from '../pipe';
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
  MatTableDataSource
} from '@angular/material/table';
import {MatIcon} from '@angular/material/icon';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {MatIconButton} from '@angular/material/button';
import {Article} from '../../api';
import {PagedTableBase} from '../../shared/page/paged-table.base';

export type ArticleAllowedColumns =
  | 'position'
  | 'icon'
  | 'name'
  | 'code'
  | 'category'
  | 'tags'
  | 'in_stock_amount'
  | 'actions';

@Component({
  selector: 'app-article-table',
  providers: [{provide: PagedTableBase, useExisting: ArticleTableComponent}],
  imports: [
    ReadableArticleCategoryPipePipe,
    ReadableArticleInStockAmountPipePipe,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatTable,
    MatHeaderCellDef,
    MatIconButton,
  ],
  template: `
    <table mat-table [dataSource]="dataSource" class="mat-elevation-z8" matSort (matSortChange)="sorted.emit($event)">
      <ng-container matColumnDef="position">
        <th mat-header-cell *matHeaderCellDef>Rb.</th>
        <td mat-cell *matCellDef="let i = index">{{ i + 1 }}.</td>
      </ng-container>
      <ng-container matColumnDef="icon">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef>
          <mat-icon class="text-xl text-gray-600">sell</mat-icon>
        </td>
      </ng-container>
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>Naziv</th>
        <td mat-cell *matCellDef="let element">
          <strong>{{ element.name }}</strong>
        </td>
      </ng-container>
      <ng-container matColumnDef="code">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>Šifra</th>
        <td mat-cell *matCellDef="let element">{{ element.code }}</td>
      </ng-container>
      <ng-container matColumnDef="category">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>Tip</th>
        <td mat-cell *matCellDef="let element">{{ element.category | ReadableArticleCategoryPipe }}</td>
      </ng-container>
      <ng-container matColumnDef="tags">
        <th mat-header-cell *matHeaderCellDef>Oznake</th>
        <td mat-cell *matCellDef="let element">{{ element.tags.split(',').join(', ') }}</td>
      </ng-container>
      <ng-container matColumnDef="in_stock_amount">
        <th mat-header-cell *matHeaderCellDef mat-sort-header>Na stanju</th>
        <td mat-cell *matCellDef="let element">
          <span [innerHTML]="element.inStockAmount | ReadableArticleInStockAmountPipe: element.unitMeasure"></span>
        </td>
      </ng-container>
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let element" class="text-right">
          <button matIconButton (click)="onDeleteClick(element, $event)">
            <mat-icon>delete</mat-icon>
          </button>
        </td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="columns; sticky: true"></tr>
      <tr mat-row
          [class.low-stock-row]="row.inStockAmount <= row.inStockWarningAmount"
          *matRowDef="let row; columns: columns"
          (click)="onRowClick(row)"></tr>
    </table>
  `,
  styles: `
    :host { @apply flex flex-col h-full w-full overflow-y-scroll; }
    tr { cursor: pointer !important; }
    tr:not(.example-expanded-row):hover { background: whitesmoke !important; }
    .low-stock-row { background-color: #fcd3d7; }
  `
})
export class ArticleTableComponent extends PagedTableBase {
  @Input() columns: ArticleAllowedColumns[] = ['position', 'icon', 'name', 'code', 'category', 'tags', 'in_stock_amount', 'actions'];

  @Output() rowClick = new EventEmitter<Article>();
  @Output() deleteClick = new EventEmitter<Article>();

  protected dataSource = new MatTableDataSource<Article>([]);

  onRowClick(row: Article) {
    this.rowClick.emit(row);
  }

  onDeleteClick(row: Article, event: MouseEvent) {
    event.stopPropagation();
    this.deleteClick.emit(row);
  }

  setData(changes: Article[]): void {
    this.dataSource.data = changes;
  }
}
