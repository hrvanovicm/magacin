import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {AmountInputComponent} from '../../shared/inputs';

export interface RecipeLike {
  rawMaterial: { name: string; unitMeasure?: any; id: number };
  amount: number;
}

export interface RecipeAmountChange {
  recipe: RecipeLike;
  amount: number;
}

@Component({
  selector: 'app-recipe-table',
  imports: [MatTableModule, MatIconModule, MatButtonModule, AmountInputComponent],
  template: `
    <table mat-table class="mat-elevation-z8 mt-2 w-full" [dataSource]="dataSource">
      <ng-container matColumnDef="position">
        <th mat-header-cell *matHeaderCellDef>Rb.</th>
        <td mat-cell *matCellDef="let i = index">{{ i + 1 }}.</td>
      </ng-container>
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef>Sirovina</th>
        <td mat-cell *matCellDef="let row">{{ row.rawMaterial.name }}</td>
      </ng-container>
      <ng-container matColumnDef="amount">
        <th mat-header-cell *matHeaderCellDef>Količina</th>
        <td mat-cell *matCellDef="let row">
          <app-amount-input label="Količina"
                            [compact]="true"
                            [initValue]="row.amount"
                            [unitMeasure]="row.rawMaterial.unitMeasure"
                            (onValueChange)="amountChange.emit({recipe: row, amount: $event})"
                            (click)="$event.preventDefault()"/>
        </td>
      </ng-container>
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let row">
          <button matIconButton (click)="remove.emit(row)">
            <mat-icon>delete</mat-icon>
          </button>
        </td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="cols"></tr>
      <tr mat-row *matRowDef="let row; columns: cols"></tr>
    </table>
  `,
})
class RecipeTableComponent {
  protected readonly cols = ['position', 'name', 'amount', 'actions'];
  protected readonly dataSource = new MatTableDataSource<RecipeLike>([]);

  @Input() set recipes(list: RecipeLike[]) {
    this.dataSource.data = list ?? [];
  }

  @Output() amountChange = new EventEmitter<RecipeAmountChange>();
  @Output() remove = new EventEmitter<RecipeLike>();
}

export default RecipeTableComponent
