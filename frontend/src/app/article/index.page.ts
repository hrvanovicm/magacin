import {Component, inject, signal} from '@angular/core';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {lastValueFrom} from 'rxjs';
import {ServerManagerService} from '../core/server-manager.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PagedPage} from '../shared/page/paged.page';
import {ArticleTableComponent} from './components/table.component';
import {ArticleFilterDialog, ArticleFilterReq} from './components/filter.dialog';
import {ConfirmDialog} from '../shared/confirm-dialog';
import {Article} from '../api';
import {ARTICLE_LINKS} from './config';

@Component({
  imports: [PagedPage, ArticleTableComponent],
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
      <app-article-table table
        (rowClick)="navigate($event)"
        (deleteClick)="onDeleteClick($event)"/>
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

  navigate(article?: Article) {
    if (!article) {
      this.router.navigate([ARTICLE_LINKS.create()]);
      return;
    }

    this.router.navigate([ARTICLE_LINKS.edit(article!.id)], {state: {article: article ?? null}});
  }

  onExportClick(state: ArticleFilterReq) {
    this.serverManager.activeServer()?.api.article.export(state);
  }

  async onFilterClick(state: ArticleFilterReq) {
    const result = await lastValueFrom(
      this.dialog.open(ArticleFilterDialog, {width: '400px', data: state}).afterClosed()
    );
    if (result != undefined) this.filter.set(result);
  }

  async onDeleteClick(article: Article) {
    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: {title: 'Obriši artikal', message: `Da li sigurno želite obrisati "${article.name}"?`, confirmLabel: 'Obriši'},
      }).afterClosed()
    );
    if (!confirmed) return;

    try {
      await this.serverManager.activeServer()!.api.article.delete({ID: article.id as any});
      this.reloadTrigger.update(n => n + 1);
    } catch (error) {
      this.snackbar.open(`❌ Greška pri brisanju: ${error}`);
    }
  }
}
