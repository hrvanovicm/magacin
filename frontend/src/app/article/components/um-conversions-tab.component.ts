import { Component, inject, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { lastValueFrom } from 'rxjs';
import { UnitMeasureAutocompleteComponent } from '../../shared/inputs';
import { ConfirmDialog } from '../../shared/confirm-dialog';
import { ServerManagerService } from '../../core/server-manager.service';
import { Article } from '../../api';

@Component({
  imports: [
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatExpansionModule,
    UnitMeasureAutocompleteComponent,
  ],
  selector: 'app-article-conversions-tab',
  template: `
    <div class="pt-3">
      <mat-accordion>
        <mat-expansion-panel #addPanel>
          <mat-expansion-panel-header>
            <mat-panel-title>
              <mat-icon>add</mat-icon>
              <span class="ml-2">Dodaj konverziju</span>
            </mat-panel-title>
          </mat-expansion-panel-header>
          <form class="flex flex-row items-end gap-3" [formGroup]="form" (ngSubmit)="add(addPanel)">
            <app-unit-measure-autocomplete label="Iz jedinice (koja se unosi)" class="flex-1"
                                           [control]="form.controls.toUnitMeasure" />
            <mat-form-field class="w-40">
              <mat-label>Faktor množenja</mat-label>
              <input matInput type="number" step="0.001" formControlName="factor" />
            </mat-form-field>
            <button matButton="filled" type="submit">Dodaj</button>
          </form>
        </mat-expansion-panel>
      </mat-accordion>
    </div>

    <div class="mt-4">
      <table mat-table [dataSource]="article().conversions || []" class="mat-elevation-z8 w-full">
        <ng-container matColumnDef="desc">
          <th mat-header-cell *matHeaderCellDef>Pravilo</th>
          <td mat-cell *matCellDef="let row">
            1 {{ row.toUnitMeasure?.name }} = {{ row.factor }} {{ article().unitMeasure?.name }}
          </td>
        </ng-container>
        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef></th>
          <td mat-cell *matCellDef="let row">
            <button matIconButton (click)="remove(row)">
              <mat-icon>delete</mat-icon>
            </button>
          </td>
        </ng-container>
        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </div>
  `,
})
export class ArticleConversionsTabComponent {
  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  readonly article = input.required<Article>();
  readonly updated = output<any[]>();

  readonly cols = ['desc', 'actions'];

  readonly form = new FormGroup({
    toUnitMeasure: new FormControl<any>(null, Validators.required),
    factor: new FormControl<number>(0, [Validators.required, Validators.min(0.001)]),
  });

  async add(panel: MatExpansionPanel) {
    if (!this.form.valid) return;
    const { toUnitMeasure, factor } = this.form.value;
    try {
      await this.serverManager.activeServer()?.api.article.saveConversion({
        id: 0,
        articleId: this.article().id,
        fromUnitMeasureId: this.article().unitMeasure?.id,
        toUnitMeasureId: toUnitMeasure.id,
        factor: Number(factor),
      } as any);
      panel.close();
      this.form.reset();
      const updated = await this.serverManager.activeServer()!.api.article.get({ ID: this.article().id });
      if (updated) this.updated.emit(updated.conversions ?? []);
    } catch (err) {
      this.snackbar.open(`Greška pri dodavanju: ${err}`);
    }
  }

  async remove(conv: any) {
    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: { title: 'Obriši', message: 'Da li ste sigurni?', confirmLabel: 'Obriši' },
      }).afterClosed()
    );
    if (!confirmed) return;
    try {
      await this.serverManager.activeServer()?.api.article.deleteConversion({ ID: conv.id });
      this.updated.emit((this.article().conversions ?? []).filter(c => c.id !== conv.id));
    } catch (err) {
      this.snackbar.open(`Greška: ${err}`);
    }
  }
}