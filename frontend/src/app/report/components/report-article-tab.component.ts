import {Component, effect, input, output, signal, computed, untracked, ViewChild} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule, NgClass} from '@angular/common';
import {MatExpansionModule, MatExpansionPanel} from '@angular/material/expansion';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTableModule} from '@angular/material/table';

import {ArticleAutocompleteComponent, AmountInputComponent} from '../../shared/inputs';
import {ReportRecipeTabComponent} from './report-recipe-tab.component';
import {ArticleCategoryValues} from '../../api';
import {article, report} from '../../../../wailsjs/go/models';

import ReportArticle = report.HasArticle;
import ReportRecipe = report.HasRecipe;
import Article = article.Article;

@Component({
  imports: [
    MatExpansionModule,
    MatIconModule,
    MatButtonModule,
    ArticleAutocompleteComponent,
    AmountInputComponent,
    ReactiveFormsModule,
    CommonModule,
    MatTableModule,
    ReportRecipeTabComponent,
    NgClass
  ],
  selector: `app-report-article-tab`,
  template: `
    <div class="flex h-full min-h-0 overflow-hidden">
      <div class="flex flex-col min-h-0 overflow-hidden border-r" [ngClass]="expandedElement() ? 'w-1/2' : 'w-full'">

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
                    [formGroup]="form"
                    (submit)="add()">
                <app-article-autocomplete label="Artikal" class="flex-1"
                                          [control]="form.controls.article"
                                          [excludes]="currentArticles()"
                                          [includeCategories]="ArticleCategoryValues"/>
                <app-amount-input label="Količina"
                                  [control]="form.controls.amount"
                                  [disabled]="!form.controls.article.value"
                                  [conversions]="form.controls.article.value?.conversions || []"
                                  [globalConversions]="filteredGlobalConversions(form.controls.article.value)"
                                  [unitMeasure]="form.controls.article.value?.unitMeasure"
                                  class="w-28"/>
                <button matButton="filled" type="submit">Dodaj</button>
              </form>
            </mat-expansion-panel>
          </mat-accordion>
        </div>

        <div class="flex-1 overflow-y-auto">
          <table mat-table class="w-full" [dataSource]="articles()" [trackBy]="trackById">
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
                                  [conversions]="element.article.conversions || []"
                                  [globalConversions]="filteredGlobalConversions(element.article)"
                                  [unitMeasure]="element.article.unitMeasure"
                                  (onValueChange)="updateAmount($event, element.article)"
                                  (click)="$event.stopPropagation()"/>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef></th>
              <td mat-cell *matCellDef="let element">
                <button matIconButton (click)="expand(element)" [disabled]="!canRecipe()">
                  <mat-icon>receipt_long</mat-icon>
                </button>
                <button matIconButton (click)="remove(element); $event.stopPropagation()">
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
            <button matIconButton (click)="expandedElementId.set(null)">
              <mat-icon>close</mat-icon>
            </button>
          </div>

          @if (canRecipe()) {
            <app-report-recipe-tab [article]="el" (updated)="onRecipeUpdated($event)"/>
          } @else {
            <p class="px-4 py-6 text-gray-500 text-sm">
              Ovaj izvještaj ne može koristiti recepture
            </p>
          }
        </div>
      }
    </div>
  `
})
export class ReportArticlesComponent {
  @ViewChild('addArticlePanel') private readonly articlePanel!: MatExpansionPanel;

  readonly articles = input.required<ReportArticle[]>();
  readonly canRecipe = input<boolean>(true);
  readonly globalConversions = input<any[]>([]);
  readonly updated = output<ReportArticle[]>();
  readonly expandedChange = output<boolean>();

  readonly expandedElementId = signal<number | null>(null);
  readonly expandedElement = computed(() => 
    this.articles().find(a => a.article.id === this.expandedElementId()) || null
  );

  constructor() {
    effect(() => {
      const expanded = !!this.expandedElement();
      untracked(() => this.expandedChange.emit(expanded));
    });
  }

  readonly form = new FormGroup({
    article: new FormControl<Article | null>(null, [Validators.required]),
    amount: new FormControl(0, [Validators.required]),
  });

  readonly articleCols = ['position', 'name', 'amount', 'actions'];

  readonly currentArticles = () => this.articles().map(r => r.article);

  onRecipeUpdated(recipes: ReportRecipe[]) {
    let articles = this.articles().map(r => {
      if (this.expandedElementId() === r.article.id) {
        r.usedRecipes = recipes;
      }
      return r;
    });

    this.updated.emit(articles);
  }

  expand(element: ReportArticle) {
    this.expandedElementId.set(this.expandedElementId() === element.article.id ? null : element.article.id);
  }

  remove(row: ReportArticle) {
    if (this.expandedElementId() === row.article.id) {
      this.expandedElementId.set(null);
    }

    const articles = this.articles().filter(r => r.article.id !== row.article.id)
    this.updated.emit(articles);
  }

  async add() {
    if (!this.form.valid) return;

    const art = this.form.value.article as Article;
    const amount = parseFloat(String(this.form.value.amount));

    let usedRecipes: ReportRecipe[] = [];
    if (this.canRecipe() && art.recipes) {
      usedRecipes = art.recipes.map(r => ReportRecipe.createFrom({
        articleId: art.id,
        rawMaterialId: r.rawMaterialId,
        rawMaterial: r.rawMaterial,
        amount: r.amount * amount,
      }));
    }

    this.updated.emit([
      ...this.articles(),
      ReportArticle.createFrom({articleId: art.id, article: art, amount, usedRecipes}),
    ]);

    this.articlePanel.close();
    this.form.reset({article: null, amount: 0});
  }

  updateAmount(amount: number, article?: Article) {
    if (!article) return;
    this.updated.emit(this.articles().map(r => {
      if (r.article.id !== article.id) return r;

      let usedRecipes = r.usedRecipes;
      if (this.canRecipe() && r.article.recipes) {
        usedRecipes = usedRecipes.map(ur => {
          const br = r.article.recipes.find(base => base.rawMaterialId === ur.rawMaterialId);
          if (br) {
            return ReportRecipe.createFrom({...ur, amount: br.amount * amount});
          }
          if (r.amount !== 0 && amount !== 0) {
            return ReportRecipe.createFrom({...ur, amount: ur.amount * (amount / r.amount)});
          } else if (amount === 0) {
            return ReportRecipe.createFrom({...ur, amount: 0});
          }
          return ur;
        });
      }

      return ReportArticle.createFrom({...r, amount, usedRecipes});
    }));
  }

  trackById(index: number, item: ReportArticle) {
    return index;
  }

  filteredGlobalConversions(art: Article | null): any[] {
    if (!art?.unitMeasure?.id) return [];
    return this.globalConversions().filter(c => c.fromUnitMeasureId === art.unitMeasure!.id);
  }

  protected readonly ArticleCategoryValues = ArticleCategoryValues;
}
