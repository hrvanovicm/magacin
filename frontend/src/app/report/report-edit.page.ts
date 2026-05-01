import {AfterContentInit, Component, DestroyRef, inject, signal, ViewChild} from '@angular/core';
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop';
import {lastValueFrom, map, startWith} from 'rxjs';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Location, NgClass} from '@angular/common';
import {Router} from '@angular/router';
import {MatToolbar} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatDivider} from '@angular/material/divider';
import {MatTableModule} from '@angular/material/table';
import {
  MatAccordion,
  MatExpansionModule,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialog} from '@angular/material/dialog';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatTabsModule} from '@angular/material/tabs';
import {MatListModule} from '@angular/material/list';
import {ConfirmDialog} from '../shared/confirm-dialog';
import {article, company, report} from '../../../wailsjs/go/models';
import {ReporTypeNamePipe} from './report.pipes';
import {
  AmountInputComponent,
  ArticleAutocompleteComponent,
  CompanyAutocompleteComponent,
  LocationAutocompleteComponent,
  UserAutocompleteComponent,
} from '../shared/inputs';
import RecipeTableComponent, {RecipeAmountChange, RecipeLike} from '../article/components/recipe-table.component';
import {NoteTabComponent} from '../shared/note-tab.component';
import {LogTabComponent} from '../shared/log-tab.component';
import {ArticleCategoryValues, ReportType, ReportTypeValues} from '../api';
import {ServerManagerService} from '../core/server-manager.service';
import {CanDeactivateComponent} from '../core/guards';
import {REPORT_LINKS} from './config';
import Report = report.Report;
import ReportArticle = report.HasArticle;
import ReportRecipe = report.HasRecipe;
import Company = company.Company;
import Article = article.Article;

@Component({
  imports: [
    ReactiveFormsModule,
    MatToolbar,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDivider,
    MatTableModule,
    MatExpansionModule,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTabsModule,
    MatListModule,
    NgClass,
    ReporTypeNamePipe,
    ArticleAutocompleteComponent,
    AmountInputComponent,
    CompanyAutocompleteComponent,
    LocationAutocompleteComponent,
    UserAutocompleteComponent,
    RecipeTableComponent,
    NoteTabComponent,
    LogTabComponent,
  ],
  template: `
    <mat-toolbar>
      <button matIconButton (click)="goBack()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <span class="ml-2 text-xl">{{ data?.id ? data!.code : 'Novi izvještaj' }}</span>
      <span class="flex-1"></span>
      @if (data?.id) {
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
        <button matIconButton color="warn" (click)="delete()">
          <mat-icon>delete</mat-icon>
        </button>
      }
      <button matButton="filled" (click)="save()" [disabled]="!form.valid">Sačuvaj</button>
    </mat-toolbar>

    <div class="flex flex-1 min-h-0 overflow-hidden">
      <div [ngClass]="expandedElement() ? 'w-1/4' : 'w-1/2'" class="shrink-0 overflow-y-auto border-r p-6">
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
          <div class="flex h-full min-h-0 overflow-hidden">
            <div class="flex flex-col min-h-0 overflow-hidden border-r"
                 [ngClass]="expandedElement() ? 'w-1/2' : 'w-full'">

              <div class="p-3 border-b shrink-0">
                <mat-accordion>
                  <mat-expansion-panel #addArticlePanel>
                    <mat-expansion-panel-header>
                      <mat-panel-title>
                        <mat-icon>add</mat-icon>
                        <span class="ml-2">Dodaj artikal</span>
                      </mat-panel-title>
                    </mat-expansion-panel-header>
                    <form class="flex flex-row items-end gap-3 pt-2"
                          [formGroup]="articleForm"
                          (submit)="submitArticleForm()">
                      <app-article-autocomplete label="Artikal" class="flex-1"
                                                [control]="articleForm.controls.article"
                                                [excludes]="currentArticles()"
                                                [includeCategories]="ArticleCategoryValues"/>
                      <app-amount-input label="Količina"
                                        [control]="articleForm.controls.amount"
                                        [disabled]="!articleForm.controls.article.value"
                                        [unitMeasure]="articleForm.controls.article.value?.unitMeasure"
                                        class="w-28"/>
                      <button matButton="filled" type="submit">Dodaj</button>
                    </form>
                  </mat-expansion-panel>
                </mat-accordion>
              </div>

              <div class="flex-1 overflow-y-auto">
                <table mat-table class="w-full" [dataSource]="articles()">
                  <ng-container matColumnDef="position">
                    <th mat-header-cell *matHeaderCellDef>Rb.</th>
                    <td mat-cell *matCellDef="let _; let i = index">{{ i + 1 }}.</td>
                  </ng-container>
                  <ng-container matColumnDef="name">
                    <th mat-header-cell *matHeaderCellDef>Artikal</th>
                    <td mat-cell *matCellDef="let element">{{ element.article.name }}</td>
                  </ng-container>
                  <ng-container matColumnDef="amount">
                    <th mat-header-cell *matHeaderCellDef>Količina</th>
                    <td mat-cell *matCellDef="let element">
                      <app-amount-input label="Količina"
                                        [compact]="true"
                                        [initValue]="element.amount"
                                        [unitMeasure]="element.article.unitMeasure"
                                        (onValueChange)="updateArticleAmount($event, element.article)"
                                        (click)="$event.stopPropagation()"/>
                    </td>
                  </ng-container>
                  <ng-container matColumnDef="actions">
                    <th mat-header-cell *matHeaderCellDef></th>
                    <td mat-cell *matCellDef="let element">
                      <button matIconButton (click)="selectArticle(element)">
                        <mat-icon>receipt_long</mat-icon>
                      </button>
                      <button matIconButton (click)="removeArticle(element); $event.stopPropagation()">
                        <mat-icon>delete</mat-icon>
                      </button>
                    </td>
                  </ng-container>
                  <tr mat-header-row *matHeaderRowDef="articleCols; sticky: true"></tr>
                  <tr mat-row *matRowDef="let row; columns: articleCols"
                      [class.selected-row]="expandedElement() === row"></tr>
                </table>
              </div>
            </div>

            @if (expandedElement(); as el) {
              <div class="flex flex-col w-1/2 min-h-0 overflow-hidden">
                <div class="flex items-center gap-2 px-4 py-3 border-b shrink-0">
                  <mat-icon class="text-gray-500">receipt_long</mat-icon>
                  <span class="font-medium flex-1">Receptura: {{ el.article.name }}</span>
                  <button matIconButton (click)="expandedElement.set(null)">
                    <mat-icon>close</mat-icon>
                  </button>
                </div>

                @if (canRecipe()) {
                  <div class="p-3 border-b shrink-0">
                    <mat-accordion>
                      <mat-expansion-panel class="!shadow-none" #addRecipePanel>
                        <mat-expansion-panel-header>
                          <mat-panel-title>
                            <mat-icon>add</mat-icon>
                            <span class="ml-2">Dodaj recepturu</span>
                          </mat-panel-title>
                        </mat-expansion-panel-header>
                        <form class="flex flex-row items-end gap-3 pt-2"
                              [formGroup]="recipeForm"
                              (submit)="submitRecipeForm()">
                          <app-article-autocomplete label="Sirovina" class="flex-1"
                                                    [control]="recipeForm.controls.rawMaterial"
                                                    [excludes]="currentRecipes()"
                                                    [includeCategories]="ArticleCategoryValues"/>
                          <app-amount-input label="Količina"
                                            [control]="recipeForm.controls.amount"
                                            [unitMeasure]="recipeForm.controls.rawMaterial.value?.unitMeasure"
                                            [disabled]="!recipeForm.controls.rawMaterial.value"
                                            class="w-28"/>
                          <button matButton="filled" type="submit">Dodaj</button>
                        </form>
                      </mat-expansion-panel>
                    </mat-accordion>
                  </div>
                  <div class="flex-1 overflow-y-auto">
                    <app-recipe-table [recipes]="el.usedRecipes"
                                      (amountChange)="updateRecipeAmount($event)"
                                      (remove)="removeRecipe($event)"/>
                  </div>
                } @else {
                  <p class="px-4 py-6 text-gray-500 text-sm">
                    Ovaj izvještaj ne može koristiti recepture
                  </p>
                }
              </div>
            }
          </div>
        </mat-tab>

        <mat-tab label="Bilješke">
          @if (data?.id) {
            <app-note-tab #noteTab subjectType="REPORT" [subjectId]="data!.id"/>
          }
        </mat-tab>

        <mat-tab label="Logovi">
          @if (data?.id) {
            <app-log-tab subjectType="REPORT" [subjectId]="data!.id"/>
          }
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
export class ReportEditPage implements AfterContentInit, CanDeactivateComponent {
  @ViewChild('addArticlePanel') private readonly articlePanel!: MatExpansionPanel;
  @ViewChild('addRecipePanel') private readonly recipePanel!: MatExpansionPanel;
  @ViewChild('noteTab') private noteTab?: NoteTabComponent;

  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly snackbar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);
  private readonly serverManager = inject(ServerManagerService);
  private readonly destroyRef = inject(DestroyRef);

  readonly data: Report | undefined = history.state?.report;

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

  readonly articles = signal<ReportArticle[]>([]);
  readonly expandedElement = signal<ReportArticle | null>(null);

  readonly canRecipe = toSignal(
    this.form.valueChanges.pipe(
      startWith(this.form.value),
      map(v => v.type === ReportType.WORK_ORDER),
    ),
    {initialValue: false},
  );

  readonly articleForm = new FormGroup({
    article: new FormControl<Article | null>(null, [Validators.required]),
    amount: new FormControl(0, [Validators.required]),
  });

  readonly recipeForm = new FormGroup({
    rawMaterial: new FormControl<Article | null>(null, [Validators.required]),
    amount: new FormControl(0, [Validators.required]),
  });

  readonly articleCols = ['position', 'name', 'amount', 'actions'];

  readonly currentArticles = () => this.articles().map(r => r.article);
  readonly currentRecipes = () => (this.expandedElement()?.usedRecipes ?? []).map(r => r.rawMaterial as Article);

  private _dirty = false;

  canDeactivate(): boolean {
    if (!this._dirty && !this.form.dirty) return true;
    return confirm('Imate nesačuvane izmjene. Da li sigurno želite napustiti stranicu?');
  }

  async ngAfterContentInit() {
    if (this.data) {
      this.form.setValue({
        code: this.data.code ?? '',
        signedBy: this.data.signedBy ?? '',
        signedAtLocation: this.data.signedAtLocation ?? '',
        signedAt: this.data.signedAt ?? '',
        receiptCompany: this.data.shipment?.receiptCompany?.name ?? '',
        type: this.data.type as ReportType,
        supplierCompany: this.data.receipt?.supplierCompany?.name ?? '',
        supplierReportCode: this.data.receipt?.supplierReportCode ?? '',
      });
      this.articles.set(this.data.articles ?? []);
    } else {
      const today = new Date().toISOString().split('T')[0];
      this.form.patchValue({
        type: ReportType.RECEIPT,
        signedAt: today,
        signedBy: this.serverManager.currentUsername(),
      });
      const code = await this.serverManager.activeServer()?.api.report.getNextCode(ReportType.RECEIPT);
      this.form.patchValue({code: code ?? ''});
    }

    this.form.controls.type.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(async type => {
        if (type && !this.data) {
          const code = await this.serverManager.activeServer()?.api.report.getNextCode(type);
          this.form.patchValue({code: code ?? ''}, {emitEvent: false});
        }
      });
  }

  selectArticle(element: ReportArticle) {
    this.expandedElement.set(this.expandedElement() === element ? null : element);
  }

  removeArticle(row: ReportArticle) {
    if (this.expandedElement() === row) this.expandedElement.set(null);
    this.articles.update(data => data.filter(r => r.article.id !== row.article.id));
    this._dirty = true;
  }

  removeRecipe(recipe: RecipeLike) {
    const el = this.expandedElement();
    if (!el) return;
    const usedRecipes = el.usedRecipes.filter(r => r.rawMaterial.id !== recipe.rawMaterial.id);
    this.expandedElement.set(ReportArticle.createFrom({...el, usedRecipes}));
    this.articles.update(data => data.map(r =>
      r.article.id === el.article.id ? ReportArticle.createFrom({...r, usedRecipes}) : r,
    ));
    this._dirty = true;
  }

  async submitArticleForm() {
    if (!this.articleForm.valid) return;

    const art = this.articleForm.value.article as Article;
    const amount = parseFloat(String(this.articleForm.value.amount));

    this.articles.update(data => [
      ...data,
      ReportArticle.createFrom({articleId: art.id, article: art, amount, usedRecipes: []}),
    ]);
    this._dirty = true;
    this.articlePanel.close();
    this.articleForm.reset({article: null, amount: 0});
  }

  submitRecipeForm() {
    if (!this.recipeForm.valid) return;
    const el = this.expandedElement();
    if (!el) return;

    const usedRecipes = [
      ...(el.usedRecipes ?? []),
      ReportRecipe.createFrom({
        rawMaterialId: this.recipeForm.value.rawMaterial!.id,
        rawMaterial: this.recipeForm.value.rawMaterial!,
        amount: parseFloat(String(this.recipeForm.value.amount)),
      }),
    ];
    this.expandedElement.set(ReportArticle.createFrom({...el, usedRecipes}));
    this.articles.update(data => data.map(r =>
      r.article.id === el.article.id ? ReportArticle.createFrom({...r, usedRecipes}) : r,
    ));
    this._dirty = true;
    this.recipePanel.close();
    this.recipeForm.reset({rawMaterial: null, amount: 0});
  }

  updateArticleAmount(amount: number, article?: Article) {
    if (!article) return;
    this.articles.update(data => data.map(r =>
      r.article.id === article.id ? ReportArticle.createFrom({...r, amount}) : r,
    ));
    this._dirty = true;
  }

  updateRecipeAmount({recipe, amount}: RecipeAmountChange) {
    const el = this.expandedElement();
    if (!el) return;
    const usedRecipes = el.usedRecipes.map(r =>
      r.rawMaterial.id === recipe.rawMaterial.id ? ReportRecipe.createFrom({...r, amount}) : r,
    );
    this.expandedElement.set(ReportArticle.createFrom({...el, usedRecipes}));
    this.articles.update(data => data.map(r =>
      r.article.id === el.article.id ? ReportArticle.createFrom({...r, usedRecipes}) : r,
    ));
    this._dirty = true;
  }

  async exportWorkOrder() {
    if (!this.data?.id) return;
    try {
      await this.serverManager.activeServer()!.api.report.exportWorkOrder({ID: this.data.id});
    } catch (error) {
      this.snackbar.open(`❌ Greška pri izvozu! ${error}`);
    }
  }

  async exportReport() {
    if (!this.data?.id) return;
    try {
      await this.serverManager.activeServer()!.api.report.exportReport({ID: this.data.id as any});
    } catch (error) {
      this.snackbar.open(`❌ Greška pri izvozu! ${error}`);
    }
  }

  async delete() {
    if (!this.data?.id) return;
    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: {
          title: 'Obriši izvještaj',
          message: `Da li sigurno želite obrisati izvještaj "${this.data.code}"?`,
          confirmLabel: 'Obriši'
        },
      }).afterClosed()
    );
    if (!confirmed) return;
    try {
      await this.serverManager.activeServer()!.api.report.delete({ID: this.data.id as any});
      this._dirty = false;
      this.form.markAsPristine();
      this.router.navigate([REPORT_LINKS.index()]);
    } catch (error) {
      this.snackbar.open(`❌ Greška pri brisanju! ${error}`);
    }
  }

  async save() {
    const rep = await this.toReport();
    const articles = this.articles().map(a => ReportArticle.createFrom({
      ...a,
      usedRecipes: rep.type === ReportType.WORK_ORDER ? a.usedRecipes : [],
    }));

    try {
      await this.serverManager.activeServer()?.api.report.save({...rep, articles} as Report);
      if (this.data?.id) await this.noteTab?.save();
      this.form.markAsPristine();
      this._dirty = false;
      this.snackbar.open(`✅ Uspješno sačuvan izvještaj!`);
      if (!this.data?.id) this.router.navigate([REPORT_LINKS.index()]);
    } catch (error) {
      this.snackbar.open(`❌ Došlo je do greške! ${error}`);
    }
  }

  private async toReport() {
    const v = this.form.value;

    return Report.createFrom({
      id: this.data?.id ?? 0,
      type: v.type,
      code: v.code?.length ? v.code : undefined,
      signedAt: v.signedAt || undefined,
      signedAtLocation: v.signedAtLocation?.length ? v.signedAtLocation : undefined,
      signedBy: v.signedBy?.length ? v.signedBy : undefined,
      receipt: {
        supplierCompany: v.supplierCompany instanceof Company ? v.supplierCompany : undefined,
        supplierReportCode: v.supplierReportCode || undefined,
      },
      shipment: {
        receiptCompany: v.receiptCompany instanceof Company ? v.receiptCompany : undefined,
      },
    });
  }

  goBack() {
    this.location.back();
  }

  protected readonly ReportTypeValues = ReportTypeValues;
  protected readonly ArticleCategoryValues = ArticleCategoryValues;
  protected readonly ReportType = ReportType;
}
