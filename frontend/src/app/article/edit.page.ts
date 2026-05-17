import { Component, computed, inject, signal, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { ActivatedRoute, Router } from '@angular/router';
import { lastValueFrom } from 'rxjs';
import { article } from '../../../wailsjs/go/models';
import { ArticleCategory, ArticleCategoryValues } from '../api';
import { ServerManagerService } from '../core/server-manager.service';
import { BackToolbarComponent } from '../shared/back-toolbar';
import { ConfirmDialog } from '../shared/confirm-dialog';
import { AmountInputComponent, TagsInputComponent, UnitMeasureAutocompleteComponent } from '../shared/inputs';
import { LogTabComponent } from '../shared/log-tab.component';
import { NoteTabComponent } from '../shared/note-tab.component';
import { ArticleAnalyticsTabComponent } from './components/analytics-tab.component';
import { ArticleRecipeTabComponent } from './components/recipe-tab.component';
import { ArticleConversionsTabComponent } from './components/um-conversions-tab.component';
import { ARTICLE_LINKS } from './config';
import { ReadableArticleCategoryPipePipe } from './pipe';

import Article = article.Article;
import Recipe = article.Recipe;

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
    NoteTabComponent,
    LogTabComponent,
    ReadableArticleCategoryPipePipe,
    AmountInputComponent,
    UnitMeasureAutocompleteComponent,
    TagsInputComponent,
    ArticleAnalyticsTabComponent,
    ArticleRecipeTabComponent,
    ArticleConversionsTabComponent,
    BackToolbarComponent,
  ],
  template: `
    <app-back-toolbar [title]="isNew() ? 'Kreirajte novi artikal' : data()!.name">
      <div actions class="flex items-center gap-3">
        @if (!isNew() && serverManager.currentRole() !== 'GUEST') {
          <button matIconButton color="warn" (click)="delete()" [disabled]="!isDeletable()">
            <mat-icon>delete</mat-icon>
          </button>
        }
        @if (serverManager.currentRole() !== 'GUEST') {
          <button matButton="filled" (click)="save()" [disabled]="!form.valid">Sačuvaj</button>
        }
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
                            [conversions]="data()?.conversions || []"
                            [globalConversions]="filteredConversions()"
                            [unitMeasure]="form.controls.unitMeasure.value"/>

          <app-amount-input class="w-full" label="Min. količina na stanju"
                            [control]="form.controls.inStockWarningAmount"
                            [conversions]="data()?.conversions || []"
                            [globalConversions]="filteredConversions()"
                            [unitMeasure]="form.controls.unitMeasure.value"/>

          <mat-divider/>

          <app-tags-input label="Oznake" [control]="form.controls.tags"/>
        </form>
      </div>

      <div class="flex-1 min-w-0 flex flex-col overflow-hidden p-6">
        <mat-tab-group class="flex-1 overflow-hidden">

          <mat-tab label="Promet">
            @if (!isNew()) {
              <app-article-analytics-tab [article]="data()!"/>
            } @else {
              <p class="p-6 text-sm text-gray-500">Analitika je dostupna tek nakon kreiranja artikla.</p>
            }
          </mat-tab>

          <mat-tab [label]="'Receptura (' + (data()?.recipes?.length ?? 0) + ')'">
            <app-article-recipe-tab class="w-full" [article]="data()!" (updated)="setRecipes($event)" />
          </mat-tab>

          <mat-tab label="Konverzije Mjera">
            @if (!isNew()) {
              <app-article-conversions-tab [article]="data()!" (updated)="setConversions($event)" />
            } @else {
              <p class="p-6 text-sm text-gray-500">Konverzije mjera su dostupne tek nakon kreiranja artikla.</p>
            }
          </mat-tab>

          <mat-tab label="Bilješke">
            @if (!isNew()) {
              <app-note-tab #noteTab subjectType="ARTICLE" [subjectId]="data()!.id"/>
            } @else {
              <p class="p-6 text-sm text-gray-500">Bilješke su dostupne tek nakon kreiranja artikla.</p>
            }
          </mat-tab>

          <mat-tab label="Logovi">
            @if (!isNew()) {
              <app-log-tab #logTab subjectType="ARTICLE" [subjectId]="data()!.id"/>
            } @else {
              <p class="p-6 text-sm text-gray-500">Logovi su dostupni tek nakon kreiranja artikla.</p>
            }
          </mat-tab>
        </mat-tab-group>
      </div>
    </div>
  `,
  styles: `:host { @apply flex flex-col h-full w-full overflow-hidden; }`,
})
export class EditPage {
  @ViewChild('noteTab') private noteTab?: NoteTabComponent;
  @ViewChild('logTab') private logTab?: LogTabComponent;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackbar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);
  readonly serverManager = inject(ServerManagerService);

  readonly data = signal<Article | null>(null);
  readonly isNew = computed(() => !this.data());
  readonly isDeletable = signal(true);
  readonly globalConversions = signal<any[]>([]);

  readonly filteredConversions = computed(() => {
    const umId = this.data()?.unitMeasure?.id ?? this.form.controls.unitMeasure.value?.id;
    if (!umId) return [];
    return this.globalConversions().filter(c => c.fromUnitMeasureId === umId);
  });

  readonly form = new FormGroup({
    category: new FormControl<ArticleCategory | null>(null, Validators.required),
    name: new FormControl('', Validators.required),
    code: new FormControl(''),
    unitMeasure: new FormControl<any>(null),
    inStockAmount: new FormControl<number>(0),
    inStockWarningAmount: new FormControl<number>(0),
    tags: new FormControl<string[]>([]),
  });

  constructor() {
    this.route.paramMap.subscribe(async params => {
      const id = params.get('id');
      if (id) await this.load(Number(id));
      if (this.serverManager.currentRole() === 'GUEST') this.form.disable();
    });

    this.form.controls.category.valueChanges.subscribe(category => {
      if (!category) return;
      this.data.update(a => a ? Article.createFrom({ ...a, category }) : a);
    });

    this.loadGlobalConversions();
  }

  setRecipes(recipes: Recipe[]) {
    this.data.update(a => a ? Article.createFrom({ ...a, recipes }) : a);
  }

  setConversions(conversions: any[]) {
    this.data.update(a => a ? Article.createFrom({ ...a, conversions }) : a);
  }

  async save() {
    const v = this.form.getRawValue();
    const current = this.data();

    const art = Article.createFrom({
      ...(current ?? { id: 0, recipes: [], conversions: [] }),
      ...v,
      code: v.code?.trim() || undefined,
      tags: (v.tags ?? []).join(','),
      unitMeasureID: v.unitMeasure?.id ?? undefined,
      inStockAmount: parseFloat(String(v.inStockAmount)),
      inStockWarningAmount: parseFloat(String(v.inStockWarningAmount)),
    });

    const id = await this.serverManager.activeServer()!.api.article.save(art);

    if (this.isNew() && typeof id === 'number') {
      if (this.noteTab?.content) {
        await this.serverManager.activeServer()!.api.notes.save({
          subjectType: 'ARTICLE',
          subjectId: id,
          content: this.noteTab.content,
        });
      }
      this.snackbar.open(`✅ Uspješno kreiran artikal ${art.name}!`);
      this.router.navigate([ARTICLE_LINKS.edit(id)], { replaceUrl: true });
      return;
    }

    await this.noteTab?.save();
    await this.load(current!.id);
    this.snackbar.open(`✅ Uspješno ažuriran artikal ${art.name}!`);
  }

  async delete() {
    const art = this.data();
    if (!art) return;

    const confirmed = await lastValueFrom(
      this.dialog.open(ConfirmDialog, {
        data: {
          title: 'Obriši artikal',
          message: `Da li sigurno želite obrisati "${art.name}"?`,
          confirmLabel: 'Obriši',
        },
      }).afterClosed()
    );

    if (!confirmed) return;
    await this.serverManager.activeServer()!.api.article.delete({ ID: art.id });
    this.router.navigate([ARTICLE_LINKS.index()]);
  }

  private async load(id: number) {
    const art = await this.serverManager.activeServer()!.api.article.get({ ID: id });
    if (!art) throw new Error(`${id} not found`);

    this.data.set(art);
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

    const paged = await this.serverManager.activeServer()!.api.report.listPaged({
      article_name: art.name,
      page: 1,
      limit: 1,
    } as any);
    const deletable = paged.total === 0;
    this.isDeletable.set(deletable);
    deletable ? this.form.controls.unitMeasure.enable() : this.form.controls.unitMeasure.disable();

    this.logTab?.load();
    this.noteTab?.load();
  }

  private async loadGlobalConversions() {
    try {
      const convs = await this.serverManager.activeServer()!.api.um.listConversions({});
      this.globalConversions.set(convs ?? []);
    } catch { }
  }

  protected readonly categoryValues = ArticleCategoryValues;
}
