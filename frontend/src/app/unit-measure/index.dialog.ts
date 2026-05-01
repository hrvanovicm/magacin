import {Component, inject, ViewChild} from '@angular/core';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatDivider} from '@angular/material/divider';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatExpansionModule, MatExpansionPanel} from '@angular/material/expansion';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatDialog} from '@angular/material/dialog';
import {lastValueFrom} from 'rxjs';
import {UnitMeasure} from '../api';
import {ServerManagerService} from '../core/server-manager.service';
import {ConfirmDialog} from '../shared/confirm-dialog';

@Component({
  template: `
    <h2 mat-dialog-title>Mjerne jedinice</h2>
    <mat-dialog-content class="mat-typography">
      <div class="pt-3">
        <mat-accordion>
          <mat-expansion-panel #createPanel>
            <mat-expansion-panel-header>
              <mat-panel-title>
                <mat-icon>add</mat-icon>
                <span class="ml-3">Kreiraj mjernu jedinicu</span>
              </mat-panel-title>
            </mat-expansion-panel-header>
            <form class="flex flex-row items-center gap-x-3" [formGroup]="createForm" (submit)="create()">
              <mat-form-field class="w-full">
                <mat-label>Naziv</mat-label>
                <input matInput formControlName="name" />
              </mat-form-field>
              <button matButton="filled" type="submit" [disabled]="creating">Sačuvaj</button>
            </form>
          </mat-expansion-panel>
        </mat-accordion>
      </div>
      <mat-divider class="!mt-5"/>
      <mat-progress-bar mode="indeterminate" [class.invisible]="!loading"/>
      <div class="mt-1 max-h-80 overflow-y-auto">
        <table mat-table [dataSource]="dataSource" class="mat-elevation-z8 w-full">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Naziv</th>
            <td mat-cell *matCellDef="let element">{{ element.name }}</td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let element">
              <button matIconButton (click)="delete(element)" [disabled]="loading">
                <mat-icon>delete</mat-icon>
              </button>
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="displayedColumns; sticky: true"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
        </table>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button matButton mat-dialog-close>Zatvori</button>
    </mat-dialog-actions>
  `,
  imports: [
    MatIconModule,
    MatExpansionModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDivider,
    MatTableModule,
    MatProgressBarModule,
    ReactiveFormsModule,
  ],
})
export class UnitMeasureIndexDialog {
  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);
  private readonly matDialog = inject(MatDialog);
  readonly dialogRef = inject(MatDialogRef<UnitMeasureIndexDialog>);

  @ViewChild('createPanel') createPanel!: MatExpansionPanel;

  readonly displayedColumns = ['name', 'actions'];
  readonly dataSource = new MatTableDataSource<UnitMeasure>([]);
  readonly createForm = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
  });

  loading = false;
  creating = false;

  async ngOnInit() {
    await this.reload();
  }

  private async reload() {
    this.loading = true;
    try {
      this.dataSource.data = await this.serverManager.activeServer()!.api.um.list({});
    } finally {
      this.loading = false;
    }
  }

  async create() {
    if (!this.createForm.valid) return;
    this.creating = true;
    try {
      await this.serverManager.activeServer()?.api.um.save({id: 0, name: this.createForm.value.name!});
      this.createForm.reset();
      this.createPanel.close();
      await this.reload();
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    } finally {
      this.creating = false;
    }
  }

  async delete(um: UnitMeasure) {
    const confirmed = await lastValueFrom(
      this.matDialog.open(ConfirmDialog, {
        data: {title: 'Obriši mjernu jedinicu', message: `Da li sigurno želite obrisati "${um.name}"?`, confirmLabel: 'Obriši'},
      }).afterClosed()
    );
    if (!confirmed) return;

    try {
      await this.serverManager.activeServer()?.api.um.delete({ID: um.id as any});
      this.dataSource.data = this.dataSource.data.filter(u => u.id !== um.id);
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    }
  }
}
