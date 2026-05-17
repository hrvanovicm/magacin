import { Component, computed, inject, signal, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgClass } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatToolbar } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDivider } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTabsModule } from '@angular/material/tabs';
import { lastValueFrom } from 'rxjs';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { company, report } from '../../../wailsjs/go/models';
import { ReporTypeNamePipe } from './report.pipes';
import { CompanyAutocompleteComponent, LocationAutocompleteComponent, UserAutocompleteComponent } from '../shared/inputs';
import { NoteTabComponent } from '../shared/note-tab.component';
import { LogTabComponent } from '../shared/log-tab.component';
import { ReportType, ReportTypeValues } from '../api';
import { ServerManagerService } from '../core/server-manager.service';
import { CanDeactivateComponent } from '../core/guards';
import { REPORT_LINKS } from './config';
import { ReportArticlesComponent } from './components/report-article-tab.component';

import Report = report.Report;
import ReportArticle = report.HasArticle;
import Company = company.Company;

@Component({
  imports: [
    ReactiveFormsModule,
    MatToolbar,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDivider,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTabsModule,
    NgClass,
    ReporTypeNamePipe,
    CompanyAutocompleteComponent,
    LocationAutocompleteComponent,
    UserAutocompleteComponent,
    NoteTabComponent,
    LogTabComponent,
    ReportArticlesComponent,
  ],
  template: `
    <mat-toolbar>
      <button matIconButton (click)="goBack()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <span class="ml-2 text-xl">{{ data()?.id ? data()!.code : 'Novi izvještaj' }}</span>
      <span class="flex-1"></span>
      @if (data()?.id) {
        @if (form.controls.type.value === ReportType.WORK_ORDER) {
          <button matButton (click)="exportWorkOrder()">
            <mat-icon>download</mat-icon>
            Izvezi nalog
          </button>
        } @else {
          <button matButton (click)="exportReport()">
            <mat-icon>download</mat-icon>
            Izvezi
          </button>
        }
        @if (serverManager.currentRole() !== 'GUEST') {
          <button matIconButton color="warn" (click)="delete()">
            <mat-icon>delete</mat-icon>
          </button>
        }
      }
      @if (serverManager.currentRole() !== 'GUEST') {
        <button matButton="filled" (click)="save()" [disabled]="!form.valid">Sačuvaj</button>
      }
    </mat-toolbar>

    <div class="flex flex-1 min-h-0 overflow-hidden">
      <div [ngClass]="isArticleExpanded() ? 'w-1/4' : 'w-1/2'" class="shrink-0 overflow-y-auto border-r p-6">
        <form class="flex flex-col gap-5 max-w-[420px] mx-auto" [formGroup]="form">
          <mat-form-field class="w-full">
            <mat-label>Tip</mat-label>
            <mat-select formControlName="type">
              @for (type of ReportTypeValues; track type) {
                <mat-option [value]="type">{{ type | reportTypeName }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field class="w-full">
            <mat-label>Šifra</mat-label>
            <input matInput formControlName="code"/>
          </mat-form-field>

          @if (form.controls.type.value == ReportType.RECEIPT) {
            <mat-form-field class="w-full">
              <mat-label>Šifra dobavljača</mat-label>
              <input matInput formControlName="supplierReportCode"/>
            </mat-form-field>
            <app-company-autocomplete label="Dobavljač" [control]="form.controls.supplierCompany"/>
          }
          @if (form.controls.type.value == ReportType.SHIPMENT) {
            <app-company-autocomplete label="Primaoc" [control]="form.controls.receiptCompany"/>
          }

          <mat-divider/>

          <mat-form-field class="w-full">
            <mat-label>Datum izdavanja</mat-label>
            <input matInput [matDatepicker]="datepicker" formControlName="signedAt"/>
            <mat-hint>YYYY-MM-DD</mat-hint>
            <mat-datepicker-toggle matIconSuffix [for]="datepicker"></mat-datepicker-toggle>
            <mat-datepicker #datepicker/>
          </mat-form-field>

          <app-location-autocomplete label="Lokacija izdavanja" [control]="$any(form.controls.signedAtLocation)"/>
          <app-user-autocomplete label="Izdao/la" [control]="$any(form.controls.signedBy)"/>
        </form>
      </div>

      <mat-tab-group class="flex flex-1 min-h-0 overflow-hidden" mat-stretch-tabs="false">

        <mat-tab label="Artikli">
          <div class="p-4 flex flex-col h-full">
            @if (form.controls.type.value === ReportType.RECEIPT) {
              <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-4">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <mat-icon class="text-yellow-400">info</mat-icon>
                  </div>
                  <div class="ml-3">
                    <p class="text-sm text-yellow-700">
                      Ova roba će se dodati na stanje.
                    </p>
                  </div>
                </div>
              </div>
            } @else if (form.controls.type.value === ReportType.WORK_ORDER) {
              <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-4">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <mat-icon class="text-yellow-400">info</mat-icon>
                  </div>
                  <div class="ml-3">
                    <p class="text-sm text-yellow-700">
                      Ova roba će se dodati na stanje, a sirovine iz recepture će se skinuti sa stanja.
                    </p>
                  </div>
                </div>
              </div>
            } @else if (form.controls.type.value === ReportType.SHIPMENT) {
              <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-4">
                <div class="flex">
                  <div class="flex-shrink-0">
                    <mat-icon class="text-yellow-400">info</mat-icon>
                  </div>
                  <div class="ml-3">
                    <p class="text-sm text-yellow-700">
                      Ova roba će se skinuti sa stanja.
                    </p>
                  </div>
                </div>
              </div>
            }
            <app-report-article-tab
              class="flex-1"
              [articles]="articles()"
              [canRecipe]="form.controls.type.value === ReportType.WORK_ORDER"
              [globalConversions]="globalConversions()"
              (updated)="onArticleUpdated($event)"
              (expandedChange)="isArticleExpanded.set($event)" />
          </div>
        </mat-tab>

        <mat-tab label="Bilješke">
          <app-note-tab #noteTab subjectType="REPORT" [subjectId]="data()?.id ?? 0"/>
        </mat-tab>

        <mat-tab label="Logovi">
          <app-log-tab #logTab subjectType="REPORT" [subjectId]="data()?.id ?? 0"/>
        </mat-tab>
      </mat-tab-group>
    </div>
  `,
  styles: `
    :host {
      @apply flex flex-col h-full w-full overflow-hidden;
    }

    tr {
      cursor: pointer;
    }

    tr:hover td {
      background: #f5f5f5;
    }

    tr.selected-row td {
      background: #e3f2fd;
    }

    table {
      width: 100%;
    }
  `,
})
export class ReportEditPage implements CanDeactivateComponent {
  @ViewChild('noteTab') private noteTab?: NoteTabComponent;
  @ViewChild('logTab') private logTab?: LogTabComponent;

  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly snackbar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);
  readonly serverManager = inject(ServerManagerService);

  readonly data = signal<Report | undefined>(undefined);
  readonly articles = signal<ReportArticle[]>([]);
  readonly dirty = signal(false);
  readonly isArticleExpanded = signal(false);
  readonly globalConversions = signal<any[]>([]);

  readonly isNew = computed(() => !this.data()?.id);

  readonly form = new FormGroup({
    type: new FormControl<ReportType | null>(null, [Validators.required]),
    code: new FormControl(''),
    receiptCompany: new FormControl<string | Company | null>(''),
    supplierCompany: new FormControl<string | Company | null>(''),
    supplierReportCode: new FormControl(''),
    signedAt: new FormControl(''),
    signedAtLocation: new FormControl(''),
    signedBy: new FormControl(''),
  });

  constructor() {
    this.route.paramMap.subscribe(async params => {
      const id = params.get('id');
      if (id) {
        await this.loadReport(parseInt(id));
      } else {
        await this.initializeNewReport();
      }
      if (this.serverManager.currentRole() === 'GUEST') {
        this.form.disable();
      }
    });

    this.form.controls.type.valueChanges.subscribe(async type => {
      if (type && this.isNew()) {
        const code = await this.serverManager.activeServer()?.api.report.getNextCode(type);
        this.form.patchValue({ code: code ?? '' }, { emitEvent: false });
      }
    });

    this.loadGlobalConversions();
  }

  canDeactivate(): boolean {
    if (!this.dirty() && !this.form.dirty) return true;
    return confirm('Imate nesačuvane izmjene. Da li sigurno želite napustiti stranicu?');
  }

  onArticleUpdated(articles: ReportArticle[]): void {
    this.articles.set(articles);
    this.dirty.set(true);
  }

  private async loadGlobalConversions() {
    try {
      const convs = await this.serverManager.activeServer()!.api.um.listConversions({});
      this.globalConversions.set(convs ?? []);
    } catch { /* ignore */ }
  }

  private async loadReport(id: number) {
    try {
      const reportObj = await this.serverManager.activeServer()?.api.report.get({ ID: id });
      if (reportObj) {
        this.data.set(reportObj);
        this.populateForm(reportObj);
        this.logTab?.load();
        this.noteTab?.load();
      }
    } catch (e) {
      this.snackbar.open('❌ Greška pri učitavanju izvještaja!');
    }
  }

  private populateForm(reportObj: Report) {
    this.form.setValue({
      code: reportObj.code ?? '',
      signedBy: reportObj.signedBy ?? '',
      signedAtLocation: reportObj.signedAtLocation ?? '',
      signedAt: reportObj.signedAt ?? '',
      receiptCompany: reportObj.shipment?.receiptCompany?.name ?? '',
      type: reportObj.type as ReportType,
      supplierCompany: reportObj.receipt?.supplierCompany?.name ?? '',
      supplierReportCode: reportObj.receipt?.supplierReportCode ?? '',
    }, { emitEvent: false });
    this.articles.set(reportObj.articles ?? []);
  }

  private async initializeNewReport() {
    const today = new Date().toISOString().split('T')[0];
    this.form.patchValue({
      type: ReportType.RECEIPT,
      signedAt: today,
      signedBy: this.serverManager.currentUsername(),
    });
    const code = await this.serverManager.activeServer()?.api.report.getNextCode(ReportType.RECEIPT);
    this.form.patchValue({ code: code ?? '' });
  }

  async exportWorkOrder() {
    if (!this.data()?.id) return;
    try {
      await this.serverManager.activeServer()!.api.report.exportWorkOrder({ ID: this.data()!.id });
    } catch (error) {
      this.snackbar.open(`❌ Greška pri izvozu! ${error}`);
    }
  }

  async exportReport() {
    if (!this.data()?.id) return;
    try {
      await this.serverManager.activeServer()!.api.report.exportReport({ ID: this.data()!.id as any });
    } catch (error) {
      this.snackbar.open(`❌ Greška pri izvozu! ${error}`);
    }
  }

  async delete() {
    if (!this.data()?.id) return;
    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: {
          title: 'Obriši izvještaj',
          message: `Da li sigurno želite obrisati izvještaj "${this.data()!.code}"?`,
          confirmLabel: 'Obriši'
        },
      }).afterClosed()
    );
    if (!confirmed) return;
    try {
      await this.serverManager.activeServer()!.api.report.delete({ ID: this.data()!.id as any });
      this.dirty.set(false);
      this.form.markAsPristine();
      this.router.navigate([REPORT_LINKS.index()]);
    } catch (error) {
      this.snackbar.open(`❌ Greška pri brisanju! ${error}`);
    }
  }

  async save() {
    const rep = this.buildReport();
    const articles = this.articles().map(a => ReportArticle.createFrom({
      ...a,
      usedRecipes: rep.type === ReportType.WORK_ORDER ? a.usedRecipes : [],
    }));

    const wasNew = this.isNew();

    try {
      const generatedId = await this.serverManager.activeServer()?.api.report.save({ ...rep, articles } as Report);

      if (generatedId && this.noteTab && this.noteTab.content) {
        if (wasNew) {
          await this.serverManager.activeServer()!.api.notes.save({
            subjectType: 'REPORT',
            subjectId: generatedId,
            content: this.noteTab.content,
          });
        } else {
          await this.noteTab.save();
        }
      }

      this.form.markAsPristine();
      this.dirty.set(false);
      this.snackbar.open(`✅ Uspješno sačuvan izvještaj!`);

      if (wasNew && typeof generatedId === 'number') {
        this.router.navigate([REPORT_LINKS.edit(generatedId)], { replaceUrl: true });
      } else if (generatedId) {
        await this.loadReport(generatedId);
      }
    } catch (error) {
      this.snackbar.open(`❌ Došlo je do greške! ${error}`);
    }
  }

  private buildReport(): Report {
    const v = this.form.value;

    const supplierCompany = (v.supplierCompany && typeof v.supplierCompany === 'object')
      ? Company.createFrom(v.supplierCompany)
      : (typeof v.supplierCompany === 'string' && v.supplierCompany.length > 0
        ? Company.createFrom({ name: v.supplierCompany })
        : undefined);

    const receiptCompany = (v.receiptCompany && typeof v.receiptCompany === 'object')
      ? Company.createFrom(v.receiptCompany)
      : (typeof v.receiptCompany === 'string' && v.receiptCompany.length > 0
        ? Company.createFrom({ name: v.receiptCompany })
        : undefined);

    return Report.createFrom({
      id: this.data()?.id ?? 0,
      type: v.type,
      code: v.code?.length ? v.code : undefined,
      signedAt: v.signedAt || undefined,
      signedAtLocation: v.signedAtLocation?.length ? v.signedAtLocation : undefined,
      signedBy: v.signedBy?.length ? v.signedBy : undefined,
      receipt: {
        supplierCompany: supplierCompany,
        supplierReportCode: v.supplierReportCode || undefined,
      },
      shipment: {
        receiptCompany: receiptCompany,
      },
    });
  }

  goBack() {
    history.back();
  }

  protected readonly ReportTypeValues = ReportTypeValues;
  protected readonly ReportType = ReportType;
}
