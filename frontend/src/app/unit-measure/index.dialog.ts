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
import {MatTabsModule} from '@angular/material/tabs';
import {MatSelectModule} from '@angular/material/select';
import {lastValueFrom} from 'rxjs';
import {UnitMeasure} from '../api';
import {ServerManagerService} from '../core/server-manager.service';
import {ConfirmDialog} from '../shared/confirm-dialog';

@Component({
  template: `
    <h2 mat-dialog-title>Podešavanje Mjernih Jedinica</h2>
    <mat-dialog-content class="mat-typography !p-0">
      <mat-tab-group mat-stretch-tabs="false" mat-align-tabs="start">
        <mat-tab label="Mjerne jedinice">
          <div class="p-4">
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
          </div>
        </mat-tab>
        
        <mat-tab label="Konverzije">
          <div class="p-4">
            <mat-accordion>
              <mat-expansion-panel #createConvPanel>
                <mat-expansion-panel-header>
                  <mat-panel-title>
                    <mat-icon>add</mat-icon>
                    <span class="ml-3">Kreiraj konverziju</span>
                  </mat-panel-title>
                </mat-expansion-panel-header>
                <form class="flex flex-row items-center gap-x-3" [formGroup]="createConvForm" (submit)="createConversion()">
                  <mat-form-field class="w-1/3">
                    <mat-label>Iz jedinice</mat-label>
                    <mat-select formControlName="fromId">
                      @for (um of dataSource.data; track um.id) {
                        <mat-option [value]="um.id">{{ um.name }}</mat-option>
                      }
                    </mat-select>
                  </mat-form-field>
                  <mat-form-field class="w-1/3">
                    <mat-label>U jedinicu</mat-label>
                    <mat-select formControlName="toId">
                      @for (um of dataSource.data; track um.id) {
                        <mat-option [value]="um.id">{{ um.name }}</mat-option>
                      }
                    </mat-select>
                  </mat-form-field>
                  <mat-form-field class="w-1/3">
                    <mat-label>Faktor množenja</mat-label>
                    <input matInput type="number" step="0.001" formControlName="factor" />
                  </mat-form-field>
                  <button matButton="filled" type="submit" [disabled]="creatingConversion">Sačuvaj</button>
                </form>
              </mat-expansion-panel>
            </mat-accordion>
            <mat-divider class="!mt-5"/>
            <mat-progress-bar mode="indeterminate" [class.invisible]="!loadingConversions"/>
            <div class="mt-1 max-h-80 overflow-y-auto">
              <table mat-table [dataSource]="conversionsDataSource" class="mat-elevation-z8 w-full">
                <ng-container matColumnDef="rule">
                  <th mat-header-cell *matHeaderCellDef>Pravilo</th>
                  <td mat-cell *matCellDef="let element">1 {{ getUmName(element.fromUnitMeasureId) }} = {{ element.factor }} {{ getUmName(element.toUnitMeasureId) }}</td>
                </ng-container>
                <ng-container matColumnDef="actions">
                  <th mat-header-cell *matHeaderCellDef></th>
                  <td mat-cell *matCellDef="let element">
                    <button matIconButton (click)="deleteConversion(element)" [disabled]="loadingConversions">
                      <mat-icon>delete</mat-icon>
                    </button>
                  </td>
                </ng-container>
                <tr mat-header-row *matHeaderRowDef="convColumns; sticky: true"></tr>
                <tr mat-row *matRowDef="let row; columns: convColumns"></tr>
              </table>
            </div>
          </div>
        </mat-tab>
      </mat-tab-group>
    </mat-dialog-content>
    <mat-dialog-actions align="end" class="!px-4">
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
    MatTabsModule,
    MatSelectModule,
  ],
})
export class UnitMeasureIndexDialog {
  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);
  private readonly matDialog = inject(MatDialog);
  readonly dialogRef = inject(MatDialogRef<UnitMeasureIndexDialog>);

  @ViewChild('createPanel') createPanel!: MatExpansionPanel;
  @ViewChild('createConvPanel') createConvPanel!: MatExpansionPanel;

  readonly displayedColumns = ['name', 'actions'];
  readonly dataSource = new MatTableDataSource<UnitMeasure>([]);
  readonly createForm = new FormGroup({
    name: new FormControl<string>('', [Validators.required]),
  });

  readonly convColumns = ['rule', 'actions'];
  readonly conversionsDataSource = new MatTableDataSource<any>([]);
  readonly createConvForm = new FormGroup({
    fromId: new FormControl<number | null>(null, [Validators.required]),
    toId: new FormControl<number | null>(null, [Validators.required]),
    factor: new FormControl<number | null>(null, [Validators.required, Validators.min(0.0001)]),
  });

  loading = false;
  creating = false;
  loadingConversions = false;
  creatingConversion = false;

  async ngOnInit() {
    await this.reload();
    await this.loadConversions();
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

  async loadConversions() {
    this.loadingConversions = true;
    try {
      this.conversionsDataSource.data = await this.serverManager.activeServer()!.api.um.listConversions({});
    } catch (err) {
      this.snackbar.open(`Greška: ${err}`);
    } finally {
      this.loadingConversions = false;
    }
  }

  async createConversion() {
    if (!this.createConvForm.valid) return;
    this.creatingConversion = true;
    try {
      await this.serverManager.activeServer()?.api.um.saveConversion({
        id: 0,
        fromUnitMeasureId: this.createConvForm.value.fromId,
        toUnitMeasureId: this.createConvForm.value.toId,
        factor: Number(this.createConvForm.value.factor)
      });
      this.createConvForm.reset();
      this.createConvPanel.close();
      await this.loadConversions();
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    } finally {
      this.creatingConversion = false;
    }
  }

  async deleteConversion(conv: any) {
    const confirmed = await lastValueFrom(
      this.matDialog.open(ConfirmDialog, {
        data: {title: 'Obriši konverziju', message: `Da li sigurno želite obrisati ovu konverziju?`, confirmLabel: 'Obriši'},
      }).afterClosed()
    );
    if (!confirmed) return;

    try {
      await this.serverManager.activeServer()?.api.um.deleteConversion({ID: conv.id});
      this.conversionsDataSource.data = this.conversionsDataSource.data.filter(c => c.id !== conv.id);
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    }
  }

  getUmName(id: number): string {
    return this.dataSource.data.find(u => u.id === id)?.name ?? String(id);
  }
}

