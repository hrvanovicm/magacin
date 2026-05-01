import {Component, ElementRef, inject, Input, input, signal, ViewChild} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatFormField, MatInputModule} from '@angular/material/input';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatDividerModule} from '@angular/material/divider';
import {ReadableArticleCategoryPipePipe} from '../../article/pipe';
import {ServerManagerService} from '../../core/server-manager.service';
import {Article, ArticleCategory, ArticleCategoryValues} from '../../api';

@Component({
  selector: 'app-article-autocomplete',
  imports: [MatFormField, MatInputModule, MatAutocompleteModule, ReactiveFormsModule, ReadableArticleCategoryPipePipe, MatDividerModule],
  template: `
    <mat-form-field class="w-full">
      <mat-label>{{ label() }}</mat-label>
      <input #inputEl type="text" matInput [formControl]="control"
             [matAutocomplete]="auto" (input)="filter()" (focus)="load()" />
      <mat-autocomplete requireSelection #auto="matAutocomplete" [displayWith]="displayFn">
        @for (option of filteredOptions(); track option.id; let i = $index) {
          <mat-option [value]="option">
            <span>{{ option.name }} ({{ option.code }})</span>
            <br />
            <small>{{ option.category | ReadableArticleCategoryPipe }}</small>
          </mat-option>
          @if (i < filteredOptions().length - 1) { <mat-divider /> }
        }
      </mat-autocomplete>
    </mat-form-field>
  `,
})
export class ArticleAutocompleteComponent {
  @ViewChild('inputEl') inputEl!: ElementRef<HTMLInputElement>;

  private readonly serverManager = inject(ServerManagerService);

  readonly label = input.required<string>();
  @Input() control!: FormControl<Article | null>;
  @Input() excludes: Article[] = [];
  @Input() includeCategories: ArticleCategory[] = ArticleCategoryValues;

  private allOptions: Article[] = [];
  readonly filteredOptions = signal<Article[]>([]);

  async load() {
    if (!this.allOptions.length) {
      this.allOptions = await this.serverManager.activeServer()!.api.article.list({
        categories: this.includeCategories,
      } as any);
    }
    this.filter();
  }

  readonly displayFn = (option: any): string =>
    option && 'name' in option ? option.name : (option ?? '');

  filter(): void {
    const query = this.inputEl.nativeElement.value.toLowerCase();
    this.filteredOptions.set(
      this.allOptions.filter(o =>
        !this.excludes.some(e => e.id === o.id) &&
        (o.name.toLowerCase().includes(query) || o.code?.toLowerCase().includes(query))
      ),
    );
  }
}
