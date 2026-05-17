import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { lastValueFrom } from 'rxjs';
import { ServerManagerService } from '../core/server-manager.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PagedPage } from '../shared/page/paged.page';
import { ArticleTableComponent } from './components/table.component';
import { ArticleFilterDialog, ArticleFilterReq } from './components/filter.dialog';
import { ActiveFiltersComponent, ActiveFilterItem } from '../shared/components/active-filters.component';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { Article } from '../api';
import { ARTICLE_LINKS } from './config';

@Component({
  imports: [PagedPage, ArticleTableComponent, ActiveFiltersComponent],
  template: `
    <app-paged-page
      title="Roba"
      [loadCallback]="serverManager.activeServer()!.api.article.listPaged"
      [filter]="filter()"
      [reloadTrigger]="reloadTrigger()"
      (createClickCallback)="navigate()"
      (exportClickCallback)="onExportClick($event)"
      (filterClickCallback)="onFilterClick($event)"
    >
      <app-active-filters
        filters
        [items]="activeFilters()"
        (remove)="onRemoveFilter($event)"
      />
      <app-article-table table
        (rowClick)="navigate($event)">
      </app-article-table>
    </app-paged-page>
  `,
  styles: `:host { @apply flex flex-col h-full w-full overflow-hidden; }`,
})
export class ProductIndexPage {
  readonly serverManager = inject(ServerManagerService);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackbar = inject(MatSnackBar);

  filter = signal<Record<string, any> | null>(null);
  reloadTrigger = signal(0);

  activeFilters = computed<ActiveFilterItem[]>(() => {
    const categoryMap: Record<string, string> = {
      'PRODUCT': 'Proizvod',
      'COMMERCIAL': 'Komercijala',
      'RAW_MATERIAL': 'Sirovina',
    };
    const f = this.filter() || {};
    const items: ActiveFilterItem[] = [];
    if (f['categories']?.length) {
      const mapped = f['categories'].map((c: string) => categoryMap[c] || c);
      items.push({ key: 'categories', label: 'Kategorije', value: mapped.join(', ') });
    }
    if (f['is_low_in_stock']) {
      items.push({ key: 'is_low_in_stock', label: 'Stanje pri kraju', value: 'Da' });
    }
    return items;
  });

  onRemoveFilter(key: string) {
    this.filter.update(prev => ({ ...prev, [key]: undefined }));
  }

  navigate(article?: Article) {
    if (!article) {
      this.router.navigate([ARTICLE_LINKS.create()]);
      return;
    }

    this.router.navigate([ARTICLE_LINKS.edit(article!.id)], { state: { article: article ?? null } });
  }

  onExportClick(state: ArticleFilterReq) {
    this.serverManager.activeServer()?.api.article.export(state);
  }

  async onFilterClick(reqState: any) {
    const state: ArticleFilterReq = {
      search: reqState.search,
      categories: typeof reqState.categories === 'string' ? [reqState.categories] : reqState.categories,
      isLowInStock: reqState.is_low_in_stock,
    };
    const result = await lastValueFrom(
      this.dialog.open(ArticleFilterDialog, { width: '400px', data: state }).afterClosed()
    );
    if (result != undefined) {
      this.filter.set({
        categories: result.categories?.length ? result.categories : undefined,
        is_low_in_stock: result.isLowInStock || undefined,
        search: result.search || undefined,
      });
    }
  }
}
