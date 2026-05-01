import {
  AfterContentInit, AfterViewInit,
  Component,
  computed,
  effect,
  inject,
  input,
  OnInit,
  output,
  signal,
  untracked,
  ViewChild
} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {DatePipe, Location} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {lastValueFrom, map} from 'rxjs';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatToolbar} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatDivider} from '@angular/material/divider';
import {MatTabsModule} from '@angular/material/tabs';
import {MatExpansionModule, MatExpansionPanel} from '@angular/material/expansion';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialog} from '@angular/material/dialog';
import {MatListModule} from '@angular/material/list';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorIntl, MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import RecipeTableComponent, {RecipeAmountChange, RecipeLike} from './components/recipe-table.component';
import {NoteTabComponent} from '../shared/note-tab.component';
import {LogTabComponent} from '../shared/log-tab.component';
import {article, report} from '../../../wailsjs/go/models';
import {ReadableArticleCategoryPipePipe} from './pipe';
import {
  AmountInputComponent,
  ArticleAutocompleteComponent,
  TagsInputComponent,
  UnitMeasureAutocompleteComponent,
} from '../shared/inputs';
import {ServerManagerService} from '../core/server-manager.service';
import {CanDeactivateComponent} from '../core/guards';
import {ArticleCategory, ArticleCategoryValues, Report, ReportType} from '../api';
import {ConfirmDialog} from '../shared/confirm-dialog';
import {bsPaginatorIntl} from '../shared/page/paged.page';
import {REPORT_LINKS} from '../report/config';
import {ARTICLE_LINKS} from './config';
import {ArticleAnalyticsComponent} from './components/analytics.component';
import Article = article.Article;
import Recipe = article.Recipe;
import HasRecipe = report.HasRecipe;
import {BackToolbarComponent} from '../shared/back-toolbar';

@Component({
  imports: [
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDivider,
    MatTabsModule,
    MatExpansionModule,
    MatListModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    DatePipe,
    ArticleAnalyticsComponent,
  ],
  selector: 'app-article-analytics-tab',
  template: `
    <div class="overflow-y-hidden h-full p-4 flex flex-col gap-4">
      @if (analyticsLoading()) {
        <mat-progress-bar mode="indeterminate"/>
      }

      <mat-accordion>
        <mat-expansion-panel>
          <mat-expansion-panel-header>
            <mat-panel-title> Analitika zadnjih 12 mjeseci </mat-panel-title>
          </mat-expansion-panel-header>
          <app-article-analytics [article]="article()" [data]="_chartMonths()"/>
        </mat-expansion-panel>
      </mat-accordion>

      <mat-divider/>

      <div class="overflow-y-auto w-full h-full">
        <table mat-table [dataSource]="analyticsReports()" class="w-full h-full overflow-hidden">
          <ng-container matColumnDef="signedAt">
            <th mat-header-cell *matHeaderCellDef>Datum</th>
            <td mat-cell *matCellDef="let row">{{ row.signedAt | date:'dd.MM.yyyy' }}</td>
          </ng-container>
          <ng-container matColumnDef="code">
            <th mat-header-cell *matHeaderCellDef>Šifra</th>
            <td mat-cell *matCellDef="let row"><strong>{{ row.code }}</strong></td>
          </ng-container>
          <ng-container matColumnDef="type">
            <th mat-header-cell *matHeaderCellDef>Tip</th>
            <td mat-cell *matCellDef="let row">
              @if (row.type === ReportType.RECEIPT) {
                Primka
              } @else if (row.type === ReportType.SHIPMENT) {
                Otpremnica
              } @else {
                Radni nalog
              }
            </td>
          </ng-container>
          <ng-container matColumnDef="company">
            <th mat-header-cell *matHeaderCellDef>Kompanija</th>
            <td mat-cell *matCellDef="let row">
              @if (row.type === ReportType.RECEIPT) {
                {{ row.receipt?.supplierCompany?.name }}
              } @else if (row.type === ReportType.SHIPMENT) {
                {{ row.shipment?.receiptCompany?.name }}
              } @else {
                —
              }
            </td>
          </ng-container>
          <ng-container matColumnDef="amount">
            <th mat-header-cell *matHeaderCellDef>Količina</th>
            <td mat-cell *matCellDef="let row">
              {{ getterAmountFromArticle(row) }}
            </td>
          </ng-container>
          <tr mat-header-row *matHeaderRowDef="analyticsCols"></tr>
          <tr mat-row *matRowDef="let row; columns: analyticsCols"
              style="cursor:pointer" (click)="navigateToReport(row)"></tr>
        </table>
      </div>

      <mat-paginator
        [length]="analyticsTotal()"
        [pageSize]="analyticsPageSize"
        [pageSizeOptions]="[10, 30, 100]"
        (page)="onAnalyticsPage($event)">
      </mat-paginator>
    </div>
  `
})
export class ArticleAnalyticsTabComponent {
  private readonly router = inject(Router);
  private readonly serverManager = inject(ServerManagerService);

  readonly article = input.required<Article>();

  readonly analyticsLoading = signal(false);
  readonly analyticsReports = signal<Report[]>([]);
  readonly analyticsTotal = signal(0);
  readonly analyticsPageSize = 10;

  constructor() {
    effect(() => {
      const art = this.article();
      if (art) {
        untracked(() => this.loadAnalytics());
        this.onAnalyticsPage({pageIndex: 0, pageSize: this.analyticsPageSize} as PageEvent);
      }
    });
  }

  getterAmountFromArticle(report: Report) {
    return report.articles?.find(a => a.articleId === this.article().id)?.amount ?? '—';
  }

  readonly _chartMonths = signal<{ label: string; key: string; in: number; out: number }[]>([]);

  readonly analyticsCols = ['signedAt', 'code', 'type', 'company', 'amount'];

  private async loadAnalytics() {
    let analytics = await this.serverManager.activeServer()!.api.article.getAnalytics({
      ArticleID: this.article().id
    })

    this._chartMonths.set(analytics);
  }

  async onAnalyticsPage(event: PageEvent) {
    const art = this.article();
    const paged = await this.serverManager.activeServer()!.api.report.listPaged({
      article_name: art.name,
      date_from: this.analyticsFromDate(),
      page: event.pageIndex + 1,
      limit: event.pageSize,
    } as any);
    this.analyticsReports.set(paged.content);
    this.analyticsTotal.set(paged.total);
  }

  private analyticsFromDate(): string {
    const now = new Date();
    const d = new Date(now.getFullYear(), now.getMonth() - 11, 1);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-01`;
  }

  navigateToReport(rep: Report) {
    this.router.navigate([REPORT_LINKS.edit((rep as any).id)], {state: {report: rep}});
  }

  protected readonly ReportType = ReportType;
}

@Component({
  imports: [
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTabsModule,
    MatExpansionModule,
    MatListModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    RecipeTableComponent,
    AmountInputComponent,
    ArticleAutocompleteComponent,

  ],
  selector: 'app-article-recipe-tab',
  template: `
    @if (canHaveRecipe()) {
      <div class="pt-3">
        <mat-accordion>
          <mat-expansion-panel #addPanel>
            <mat-expansion-panel-header>
              <mat-panel-title>
                <mat-icon>add</mat-icon>
                <span class="ml-2">Dodaj sirovinu</span>
              </mat-panel-title>
            </mat-expansion-panel-header>
            <form class="flex flex-row items-end gap-3" [formGroup]="form" (ngSubmit)="add(addPanel)">
              <app-article-autocomplete label="Sirovina" class="flex-1"
                                        [control]="form.controls.rawMaterial"
                                        [excludes]="recipeRawMaterials()"
                                        [includeCategories]="[ArticleCategory.RAW_MATERIAL]"/>
              <app-amount-input class="w-40" label="Količina"
                                [control]="form.controls.amount"
                                [unitMeasure]="form.controls.rawMaterial.value?.unitMeasure"/>
              <button matButton="filled" type="submit">Dodaj</button>
            </form>
          </mat-expansion-panel>
        </mat-accordion>
      </div>

      <app-recipe-table
        [recipes]="article().recipes"
        (amountChange)="setAmount($event)"
        (remove)="remove($event)"/>
    } @else {
      <p class="p-6 text-sm text-gray-500">Sirovine nemaju recepturu.</p>
    }
  `
})
export class ArticleRecipeTabComponent {
  readonly recipeRawMaterials = () => this.article().recipes.map(r => r.rawMaterial);
  readonly canHaveRecipe = computed(() => this.article().category == ArticleCategory.RAW_MATERIAL);

  readonly form = new FormGroup({
    rawMaterial: new FormControl<Article | null>(null, Validators.required),
    amount: new FormControl<number>(0, Validators.required),
  });

  readonly article = input.required<Article>();
  readonly updated = output<Recipe[]>();

  private update(list: Recipe[]) {
    this.updated.emit(list);
  }

  add(panel: MatExpansionPanel) {
    if (!this.form.valid) {
      return;
    }

    const {rawMaterial, amount} = this.form.value;
    const rawMaterialId = (rawMaterial as any)?.id ?? (rawMaterial as any)?.ID ?? 0;
    this.update([
      ...this.article().recipes,
      Recipe.createFrom({rawMaterial, rawMaterialId, amount: parseFloat(String(amount))}),
    ]);

    panel.close();
    this.form.reset({rawMaterial: null, amount: 0});
  }

  remove(recipe: RecipeLike) {
    this.update(this.article().recipes.filter(r => r.rawMaterial.id !== recipe.rawMaterial.id));
  }

  setAmount({recipe, amount}: RecipeAmountChange) {
    this.update(this.article().recipes.map(r =>
      r.rawMaterial.id === recipe.rawMaterial.id ? Recipe.createFrom({...r, amount}) : r,
    ));
  }

  protected readonly ArticleCategory = ArticleCategory;
}

@Component({
  imports: [
    ReactiveFormsModule,
    MatToolbar,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDivider,
    MatTabsModule,
    MatExpansionModule,
    MatListModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressBarModule,
    NoteTabComponent,
    LogTabComponent,
    ReadableArticleCategoryPipePipe,
    AmountInputComponent,
    UnitMeasureAutocompleteComponent,
    TagsInputComponent,
    ArticleAnalyticsTabComponent,
    ArticleRecipeTabComponent,
    BackToolbarComponent,
  ],
  providers: [{provide: MatPaginatorIntl, useFactory: bsPaginatorIntl}],
  template: `
    <app-back-toolbar [title]="isNew ? 'Kreirajte novi artikal' : article().name">
      <div actions class="flex items-center gap-3">
        @if (!isNew) {
          <button matIconButton color="warn" (click)="delete()">
            <mat-icon>delete</mat-icon>
          </button>
        }
        <button matButton="filled" (click)="save()" [disabled]="!isFormValid()">Sačuvaj</button>
      </div>
    </app-back-toolbar>

    <div class="flex flex-1 min-h-0 gap-0 overflow-hidden">
      <div class="w-full sm:w-1/2 overflow-y-auto p-6 border-r">
        <form class="flex flex-col gap-5 max-w-[420px] mx-auto" [formGroup]="form">
          <mat-form-field class="w-full">
            <mat-label>Kategorija</mat-label>
            <mat-select formControlName="category">
              @for (category of categoryValues; track category) {
                <mat-option [value]="category">{{ category | ReadableArticleCategoryPipe }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field class="w-full">
            <mat-label>Naziv</mat-label>
            <input matInput formControlName="name"/>
          </mat-form-field>

          <mat-form-field class="w-full">
            <mat-label>Šifra</mat-label>
            <input matInput formControlName="code"/>
          </mat-form-field>

          <mat-divider/>

          <app-unit-measure-autocomplete class="w-full" label="Mjerna jedinica"
                                         [control]="form.controls.unitMeasure"/>

          <app-amount-input class="w-full" label="Na stanju"
                            [control]="form.controls.inStockAmount"
                            [unitMeasure]="form.controls.unitMeasure.value"/>

          <app-amount-input class="w-full" label="Min. količina na stanju"
                            [control]="form.controls.inStockWarningAmount"
                            [unitMeasure]="form.controls.unitMeasure.value"/>

          <mat-divider/>

          <app-tags-input label="Oznake" [control]="form.controls.tags"/>
        </form>
      </div>

      <div class="flex-1 min-w-0 flex flex-col overflow-hidden p-6">
        <mat-tab-group class="flex-1 overflow-hidden">

          @if (!isNew) {
            <mat-tab label="Analitika">
              <app-article-analytics-tab [article]="article()"/>
            </mat-tab>
          }

          <mat-tab [label]="'Receptura (' + article().recipes.length + ')'">
            <app-article-recipe-tab [article]="article()" (updated)="setRecipes($event)" />
          </mat-tab>

          <mat-tab label="Bilješke">
            @if (!isNew) {
              <app-note-tab #noteTab subjectType="ARTICLE" [subjectId]="article().id"/>
            }
          </mat-tab>

          <mat-tab label="Logovi">
            @if (!isNew) {
              <app-log-tab subjectType="ARTICLE" [subjectId]="article().id"/>
            }
          </mat-tab>
        </mat-tab-group>
      </div>
    </div>
  `,
  styles: `:host {
    @apply flex flex-col h-full w-full overflow-hidden;
  }`,
})
export class EditPage implements AfterViewInit {
  @ViewChild('noteTab') private noteTab?: NoteTabComponent;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  readonly isNew = this.route.snapshot.paramMap.get('id') === null;
  readonly article = signal<Article>(Article.createFrom({
    id: 0, name: '', code: '', tags: '', category: '', inStockAmount: 0, inStockWarningAmount: 0, recipes: [],
  }));

  readonly form = new FormGroup({
    category: new FormControl<ArticleCategory | null>(null, Validators.required),
    name: new FormControl('', Validators.required),
    code: new FormControl(''),
    unitMeasure: new FormControl<any>(null),
    inStockAmount: new FormControl<number>(0),
    inStockWarningAmount: new FormControl<number>(0),
    tags: new FormControl<string[]>([]),
  });

  readonly isFormValid = toSignal(
    this.form.statusChanges.pipe(map(s => s === 'VALID')),
    {initialValue: this.form.valid},
  );

  readonly analyticsData = signal<{ label: string; key: string; in: number; out: number }[]>([]);

  async ngAfterViewInit() {
    if (!this.isNew) {
      await this.load();
    }
  }

  setRecipes(recipes: Recipe[]) {
    this.article.update(state => {
      state.recipes = recipes
      return state;
    });
  }

  private async load() {
    if(this.isNew) {
      return;
    }

    const id = Number(this.route.snapshot.paramMap.get('id'));
    const art = await this.serverManager.activeServer()!.api.article.get({ID: id});

    if (!art) {
      throw new Error(`${id} not found`);
    }

    this.article.set(art);
    this.form.patchValue({
      category: art.category as ArticleCategory,
      name: art.name,
      code: art.code ?? '',
      unitMeasure: art.unitMeasure ?? null,
      inStockAmount: art.inStockAmount,
      inStockWarningAmount: art.inStockWarningAmount,
      tags: art.tags ? art.tags.split(',') : [],
    });

    this.form.markAsPristine();

    let analytics = await this.serverManager.activeServer()!.api.article.getAnalytics({
      ArticleID: this.article().id
    })

    this.analyticsData.set(analytics);
  }

  async save() {
    const v = this.form.getRawValue();

    const art = Article.createFrom({
      ...this.article(),
      ...v,
      tags: (v.tags ?? []).join(','),
      inStockAmount: parseFloat(String(v.inStockAmount)),
      inStockWarningAmount: parseFloat(String(v.inStockWarningAmount)),
    });

    let response = await this.serverManager.activeServer()!.api.article.save(art);

    if (!this.isNew) {
      await this.noteTab?.save();
    }

    if (this.isNew) {
      this.snackbar.open(`✅ Uspješno kreiran artikal ${art.name}!`);
      // TODO
    }

    await this.load();

    this.snackbar.open(`✅ Uspješno ažuriran artikal ${art.name}!`);
  }

  async delete() {
    const art = this.article();

    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: {
          title: 'Obriši artikal',
          message: `Da li sigurno želite obrisati "${art.name}"?`,
          confirmLabel: 'Obriši'
        },
      }).afterClosed()
    );

    if (!confirmed) {
      return;
    }

    await this.serverManager.activeServer()!.api.article.delete({ID: art.id});
    await this.router.navigate([ARTICLE_LINKS.index()]);
  }

  protected readonly categoryValues = ArticleCategoryValues;
  protected readonly ReportType = ReportType;
}
