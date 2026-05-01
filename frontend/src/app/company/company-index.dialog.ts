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
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatDialog} from '@angular/material/dialog';
import {lastValueFrom} from 'rxjs';
import {Company} from '../api';
import {ServerManagerService} from '../core/server-manager.service';
import {ConfirmDialog} from '../shared/confirm-dialog';

@Component({
  template: `
    <h2 mat-dialog-title>Kompanije</h2>
    <mat-dialog-content class="mat-typography">
      <div class="pt-3">
        <mat-accordion>
          <mat-expansion-panel #createPanel>
            <mat-expansion-panel-header>
              <mat-panel-title>
                <mat-icon>add</mat-icon>
                <span class="ml-3">Kreiraj kompaniju</span>
              </mat-panel-title>
            </mat-expansion-panel-header>
            <form [formGroup]="createForm" (submit)="create()">
              <div class="w-full flex flex-row items-center gap-x-3">
                <mat-form-field class="w-full">
                  <mat-label>Naziv</mat-label>
                  <input matInput formControlName="name" />
                </mat-form-field>
                <button matButton="filled" type="submit" [disabled]="creating">Sačuvaj</button>
              </div>
              <mat-checkbox class="mt-3" formControlName="inHouseProduction">Vlastita proizvodnja</mat-checkbox>
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
            <td mat-cell *matCellDef="let element">
              <span>{{ element.name }}</span>
              @if (element.inHouseProduction) {
                <span class="ml-2 inline-flex items-center rounded-full bg-blue-100 px-2 py-0.5 text-xs font-medium text-blue-800">
                  Vlastita proizvodnja
                </span>
              }
            </td>
          </ng-container>
          <ng-container matColumnDef="actions">
            <th mat-header-cell *matHeaderCellDef></th>
            <td mat-cell *matCellDef="let element" class="text-right">
              <button matIconButton (click)="delete(element)" [disabled]="deleting === element.id">
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
    MatCheckboxModule,
  ],
})
export class CompanyIndexDialog {
  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);
  private readonly matDialog = inject(MatDialog);
  readonly dialogRef = inject(MatDialogRef<CompanyIndexDialog>);

  @ViewChild('createPanel') createPanel!: MatExpansionPanel;

  readonly displayedColumns = ['name', 'actions'];
  readonly dataSource = new MatTableDataSource<Company>([]);
  readonly createForm = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
    inHouseProduction: new FormControl<boolean>(false),
  });

  loading = false;
  creating = false;
  deleting: number | null = null;

  async ngOnInit() {
    await this.reload();
  }

  private async reload() {
    this.loading = true;
    try {
      this.dataSource.data = await this.serverManager.activeServer()!.api.company.list({});
    } finally {
      this.loading = false;
    }
  }

  async create() {
    if (!this.createForm.valid) return;
    this.creating = true;
    try {
      await this.serverManager.activeServer()!.api.company.save({
        id: 0,
        name: this.createForm.value.name!,
        inHouseProduction: this.createForm.value.inHouseProduction ?? false,
      });
      this.createForm.reset({inHouseProduction: false});
      this.createPanel.close();
      await this.reload();
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    } finally {
      this.creating = false;
    }
  }

  async delete(company: Company) {
    const confirmed = await lastValueFrom(
      this.matDialog.open(ConfirmDialog, {
        data: {title: 'Obriši kompaniju', message: `Da li sigurno želite obrisati "${company.name}"?`, confirmLabel: 'Obriši'},
      }).afterClosed()
    );
    if (!confirmed) return;

    this.deleting = company.id;
    try {
      await this.serverManager.activeServer()!.api.company.delete({ID: company.id});
      await this.reload();
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    } finally {
      this.deleting = null;
    }
  }
}
