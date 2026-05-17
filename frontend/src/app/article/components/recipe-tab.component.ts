import { Component, input, output, computed } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { AmountInputComponent, ArticleAutocompleteComponent } from '../../shared/inputs';
import { RecipeTableComponent } from './recipe-table.component';
import { article } from '../../../../wailsjs/go/models';
import { ArticleCategory } from '../../api';

import Article = article.Article;
import Recipe = article.Recipe;

@Component({
  imports: [
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatExpansionModule,
    MatTableModule,
    AmountInputComponent,
    ArticleAutocompleteComponent,
    RecipeTableComponent,
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
                                        [excludes]="rawMaterials()"
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
      <p class="p-6 text-sm text-gray-500">Samo proizvodi mogu imati recepturu.</p>
    }
  `,
  styles: [`:host { display: block; width: 100%; }`],
})
export class ArticleRecipeTabComponent {
  readonly article = input.required<Article>();
  readonly updated = output<Recipe[]>();

  readonly canHaveRecipe = computed(() => this.article().category === ArticleCategory.PRODUCT);
  readonly rawMaterials = () => this.article().recipes.map(r => r.rawMaterial);

  readonly form = new FormGroup({
    rawMaterial: new FormControl<Article | null>(null, Validators.required),
    amount: new FormControl<number>(0, Validators.required),
  });

  add(panel: MatExpansionPanel) {
    if (!this.form.valid) return;
    const { rawMaterial, amount } = this.form.value;
    const rawMaterialId = (rawMaterial as any)?.id ?? 0;
    this.emit([
      ...this.article().recipes,
      Recipe.createFrom({ rawMaterial, rawMaterialId, amount: parseFloat(String(amount)) }),
    ]);
    panel.close();
    this.form.reset({ rawMaterial: null, amount: 0 });
  }

  remove(recipe: { rawMaterial: Article }) {
    this.emit(this.article().recipes.filter(r => r.rawMaterial.id !== recipe.rawMaterial.id));
  }

  setAmount({ recipe, amount }: { recipe: { rawMaterial: Article }; amount: number }) {
    this.emit(this.article().recipes.map(r =>
      r.rawMaterial.id === recipe.rawMaterial.id ? Recipe.createFrom({ ...r, amount }) : r,
    ));
  }

  private emit(list: Recipe[]) {
    this.updated.emit(list);
  }

  protected readonly ArticleCategory = ArticleCategory;
}