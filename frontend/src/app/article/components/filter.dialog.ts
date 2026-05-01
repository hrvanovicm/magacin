import {Component, inject, signal} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatButton} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {ReadableArticleCategoryPipePipe} from '../pipe';
import {ArticleCategory, ArticleCategoryValues} from '../../api';

export interface ArticleFilterReq {
  search?: string;
  categories?: ArticleCategory[];
  isLowInStock?: boolean;
}

@Component({
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatButton,
    FormsModule,
    ReadableArticleCategoryPipePipe,
  ],
  template: `
    <h2 mat-dialog-title>Filter</h2>
    <mat-dialog-content>
      <div class="flex flex-col gap-4 pt-2">
        <mat-form-field class="w-full">
          <mat-label>Pretraga</mat-label>
          <input matInput [ngModel]="filter().search" (ngModelChange)="patch({ search: $event })"/>
        </mat-form-field>
        <mat-form-field class="w-full">
          <mat-label>Kategorija</mat-label>
          <mat-select multiple [ngModel]="filter().categories" (ngModelChange)="patch({ categories: $event })">
            @for (cat of categoryValues; track cat) {
              <mat-option [value]="cat">{{ cat | ReadableArticleCategoryPipe }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
        <mat-slide-toggle
          [ngModel]="filter().isLowInStock ?? false"
          (ngModelChange)="patch({ isLowInStock: $event || undefined })">
          Stanje pri kraju
        </mat-slide-toggle>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button matButton (click)="reset()">Resetuj</button>
      <button matButton (click)="dialogRef.close()">Odustani</button>
      <button matButton color="primary" (click)="apply()">Primjeni</button>
    </mat-dialog-actions>
  `,
})
export class ArticleFilterDialog {
  readonly dialogRef = inject(MatDialogRef<ArticleFilterDialog>);
  private readonly data: ArticleFilterReq = inject(MAT_DIALOG_DATA) ?? {};

  filter = signal<ArticleFilterReq>({...this.data});

  patch(partial: Partial<ArticleFilterReq>) {
    this.filter.update(prev => ({...prev, ...partial}));
  }

  reset() {
    this.filter.set({});
  }

  apply() {
    this.dialogRef.close(this.filter());
  }

  protected readonly categoryValues = ArticleCategoryValues;
}
