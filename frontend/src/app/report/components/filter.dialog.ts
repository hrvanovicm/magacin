import {Component, inject, OnInit, signal} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatButton} from '@angular/material/button';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {ReporTypeNamePipe} from '../report.pipes';
import {ReportType, ReportTypeValues} from '../../api';
import {
  CompanyAutocompleteComponent,
  LocationAutocompleteComponent,
  UserAutocompleteComponent,
} from '../../shared/inputs';
import {ServerManagerService} from '../../core/server-manager.service';
import {MatAutocompleteModule} from '@angular/material/autocomplete';

export interface ReportFilterReq {
  search?: string;
  types?: ReportType[];
  company?: string;
  date_from?: string;
  date_to?: string;
  location?: string;
  signed_by?: string;
  article_name?: string;
}

@Component({
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButton,
    ReactiveFormsModule,
    ReporTypeNamePipe,
    CompanyAutocompleteComponent,
    LocationAutocompleteComponent,
    UserAutocompleteComponent,
    MatAutocompleteModule,
  ],
  template: `
    <h2 mat-dialog-title>Filter</h2>
    <mat-dialog-content class="flex flex-col gap-4 pt-2 min-w-[360px]">

      <mat-form-field class="w-full">
        <mat-label>Tip izvještaja</mat-label>
        <mat-select multiple [formControl]="form.controls.types">
          @for (type of typeValues; track type) {
            <mat-option [value]="type">{{ type | reportTypeName }}</mat-option>
          }
        </mat-select>
      </mat-form-field>

      <mat-form-field class="w-full">
        <mat-label>Sadrži artikal</mat-label>
        <input matInput [formControl]="form.controls.article_name"
               [matAutocomplete]="articleAuto" (input)="filterArticles($event)" (focus)="loadArticles()"/>
        <mat-autocomplete #articleAuto="matAutocomplete">
          @for (name of filteredArticleNames(); track name) {
            <mat-option [value]="name">{{ name }}</mat-option>
          }
        </mat-autocomplete>
      </mat-form-field>

      <app-company-autocomplete label="Kompanija" [control]="$any(form.controls.company)"/>

      <div class="flex gap-3">
        <mat-form-field class="flex-1">
          <mat-label>Datum od</mat-label>
          <input matInput [matDatepicker]="fromPicker" [formControl]="dateFromControl"
                 (dateChange)="onDateChange('date_from', $event.value)"/>
          <mat-datepicker-toggle matIconSuffix [for]="fromPicker"/>
          <mat-datepicker #fromPicker/>
        </mat-form-field>

        <mat-form-field class="flex-1">
          <mat-label>Datum do</mat-label>
          <input matInput [matDatepicker]="toPicker" [formControl]="dateToControl"
                 (dateChange)="onDateChange('date_to', $event.value)"/>
          <mat-datepicker-toggle matIconSuffix [for]="toPicker"/>
          <mat-datepicker #toPicker/>
        </mat-form-field>
      </div>

      <app-location-autocomplete label="Lokacija" [control]="$any(form.controls.location)"/>
      <app-user-autocomplete label="Potpisao" [control]="$any(form.controls.signed_by)"/>

    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button matButton (click)="reset()">Resetuj</button>
      <button matButton (click)="dialogRef.close()">Odustani</button>
      <button matButton color="primary" (click)="apply()">Primjeni</button>
    </mat-dialog-actions>
  `,
})
export class ReportFilterDialog implements OnInit {
  readonly dialogRef = inject(MatDialogRef<ReportFilterDialog>);
  private readonly data: ReportFilterReq = inject(MAT_DIALOG_DATA) ?? {};
  private readonly serverManager = inject(ServerManagerService);

  readonly form = new FormGroup({
    types: new FormControl<ReportType[]>(this.data.types ?? []),
    article_name: new FormControl<string>(this.data.article_name ?? ''),
    company: new FormControl<string>(this.data.company ?? ''),
    location: new FormControl<string>(this.data.location ?? ''),
    signed_by: new FormControl<string>(this.data.signed_by ?? ''),
  });

  readonly dateFromControl = new FormControl<Date | null>(
    this.data.date_from ? new Date(this.data.date_from) : null
  );
  readonly dateToControl = new FormControl<Date | null>(
    this.data.date_to ? new Date(this.data.date_to) : null
  );

  private date_from?: string = this.data.date_from;
  private date_to?: string = this.data.date_to;

  private allArticleNames: string[] = [];
  readonly filteredArticleNames = signal<string[]>([]);

  async ngOnInit() {
    try {
      const articles = await this.serverManager.activeServer()!.api.article.list({} as any);
      this.allArticleNames = articles.map(a => a.name);
      this.applyArticleFilter(this.form.controls.article_name.value ?? '');
    } catch {}
  }

  filterArticles(event: Event) {
    this.applyArticleFilter((event.target as HTMLInputElement).value);
  }

  async loadArticles() {
    if (!this.allArticleNames.length) await this.ngOnInit();
    else this.applyArticleFilter(this.form.controls.article_name.value ?? '');
  }

  private applyArticleFilter(q: string) {
    const lower = q.toLowerCase();
    this.filteredArticleNames.set(
      lower ? this.allArticleNames.filter(n => n.toLowerCase().includes(lower)) : this.allArticleNames.slice(0, 20)
    );
  }

  onDateChange(field: 'date_from' | 'date_to', value: Date | null) {
    if (field === 'date_from') this.date_from = value ? value.toISOString().split('T')[0] : undefined;
    else this.date_to = value ? value.toISOString().split('T')[0] : undefined;
  }

  reset() {
    this.form.reset({types: [], article_name: '', company: '', location: '', signed_by: ''});
    this.dateFromControl.reset(null);
    this.dateToControl.reset(null);
    this.date_from = undefined;
    this.date_to = undefined;
  }

  apply() {
    const v = this.form.value;
    this.dialogRef.close({
      types: v.types?.length ? v.types : undefined,
      article_name: v.article_name || undefined,
      company: v.company || undefined,
      location: v.location || undefined,
      signed_by: v.signed_by || undefined,
      date_from: this.date_from,
      date_to: this.date_to,
    } satisfies ReportFilterReq);
  }

  protected readonly typeValues = ReportTypeValues;
}
