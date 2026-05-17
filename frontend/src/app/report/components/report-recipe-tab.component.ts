import { Component, input, output, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ArticleAutocompleteComponent, AmountInputComponent } from '../../shared/inputs';
import { RecipeTableComponent, RecipeAmountChange, RecipeLike } from '../../article/components/recipe-table.component';
import { ArticleCategoryValues } from '../../api';
import { article, report } from '../../../../wailsjs/go/models';

import ReportArticle = report.HasArticle;
import ReportRecipe = report.HasRecipe;
import Article = article.Article;

@Component({
  selector: 'app-report-recipe-tab',
  imports: [
    MatExpansionModule,
    MatIconModule,
    MatButtonModule,
    ArticleAutocompleteComponent,
    AmountInputComponent,
    ReactiveFormsModule,
    RecipeTableComponent,
  ],
  template: `
    <div class="p-3 border-b shrink-0">
      <mat-accordion>
        <mat-expansion-panel class="!shadow-none" #createPanel>
          <mat-expansion-panel-header>
            <mat-panel-title>
              <mat-icon>add</mat-icon>
              <span class="ml-2">Dodaj recepturu</span>
            </mat-panel-title>
          </mat-expansion-panel-header>
          <form class="flex flex-row items-end gap-3 pt-2"
                [formGroup]="form"
                (submit)="add()">
            <app-article-autocomplete label="Sirovina" class="flex-1"
                                      [control]="form.controls.rawMaterial"
                                      [excludes]="recipes()"
                                      [includeCategories]="ArticleCategoryValues"/>
            <app-amount-input label="Količina"
                              [control]="form.controls.amount"
                              [unitMeasure]="form.controls.rawMaterial.value?.unitMeasure"
                              [conversions]="form.controls.rawMaterial.value?.conversions || []"
                              [disabled]="!form.controls.rawMaterial.value"
                              class="w-28"/>
            <button matButton="filled" type="submit">Dodaj</button>
          </form>
        </mat-expansion-panel>
      </mat-accordion>
    </div>
    <div class="flex-1 overflow-y-auto">
      <app-recipe-table [recipes]="article().usedRecipes"
                        (amountChange)="updateAmount($event)"
                        (remove)="remove($event)"/>
    </div>
  `
})
export class ReportRecipeTabComponent {
  @ViewChild('createPanel') private readonly createPanel!: MatExpansionPanel;

  readonly article = input.required<ReportArticle>();
  readonly updated = output<ReportRecipe[]>()
  readonly form = new FormGroup({
    rawMaterial: new FormControl<Article | null>(null, [Validators.required]),
    amount: new FormControl(0, [Validators.required]),
  });
  readonly recipes = () => (this.article().usedRecipes).map(r => r.rawMaterial as Article);

  remove(recipe: RecipeLike) {
    const recipes = this.article().usedRecipes.filter(r => r.rawMaterial.id !== recipe.rawMaterial.id);
    this.updated.emit(recipes);
  }

  add() {
    if (!this.form.valid) return;

    const recipes = [
      ...this.article().usedRecipes,
      ReportRecipe.createFrom({
        articleId: this.article().articleId,
        rawMaterialId: this.form.value.rawMaterial!.id,
        rawMaterial: this.form.value.rawMaterial!,
        amount: parseFloat(String(this.form.value.amount)),
      }),
    ];

    this.updated.emit(recipes);
    this.createPanel.close();
    this.form.reset({ rawMaterial: null, amount: 0 });
  }

  updateAmount({ recipe, amount }: RecipeAmountChange) {
    const recipes = this.article().usedRecipes.map(r =>
      r.rawMaterial.id === recipe.rawMaterial.id ? ReportRecipe.createFrom({ ...r, amount }) : r,
    );
    this.updated.emit(recipes);
  }

  protected readonly ArticleCategoryValues = ArticleCategoryValues;
}
