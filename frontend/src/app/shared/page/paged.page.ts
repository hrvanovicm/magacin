import {AfterContentInit, Component, ContentChild, DestroyRef, EventEmitter, Input, Output, inject, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatToolbar, MatToolbarRow} from '@angular/material/toolbar';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as api from '../../api';
import {MatPaginatorIntl, MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {MatInputModule} from '@angular/material/input';
import {Sort} from '@angular/material/sort';
import {PagedTableBase} from './paged-table.base';
import {ActivatedRoute, Router} from '@angular/router';

const defaultPageSizes = [100, 500, 1000];

type LoadState = 'idle' | 'loading' | 'empty' | 'error';

export function bsPaginatorIntl(): MatPaginatorIntl {
  const intl = new MatPaginatorIntl();
  intl.itemsPerPageLabel = 'Stavki po stranici:';
  intl.nextPageLabel = 'Sljedeća stranica';
  intl.previousPageLabel = 'Prethodna stranica';
  intl.firstPageLabel = 'Prva stranica';
  intl.lastPageLabel = 'Posljednja stranica';
  intl.getRangeLabel = (page, pageSize, length) => {
    if (length === 0) return 'Nema rezultata';
    const start = page * pageSize + 1;
    const end = Math.min((page + 1) * pageSize, length);
    return `${start} – ${end} od ${length}`;
  };
  return intl;
}

@Component({
  selector: 'app-paged-page',
  providers: [{provide: MatPaginatorIntl, useFactory: bsPaginatorIntl}],
  imports: [
    MatButton,
    MatIcon,
    MatToolbar,
    MatToolbarRow,
    MatProgressBarModule,
    ReactiveFormsModule,
    MatPaginatorModule,
    MatInputModule,
    MatIconButton,
    FormsModule,
  ],
  template: `
    <mat-toolbar>
      <mat-toolbar-row class="flex justify-between flex-wrap gap-2">
        <div class="flex items-center gap-3 flex-wrap">
          <h4 class="!text-xl">{{ title }}</h4>
          <span>|</span>
          <mat-form-field class="w-[250px] sm:w-[350px]">
            <mat-label>Pretraga</mat-label>
            <input matInput type="text" [ngModel]="req().search" (ngModelChange)="onSearch($event)">
          </mat-form-field>
          <button matIconButton (click)="load()">
            <mat-icon>refresh</mat-icon>
          </button>
          @if (filterClickCallback.observers.length > 0) {
            <button matIconButton (click)="filterClickCallback.emit(req())">
              <mat-icon>filter_alt</mat-icon>
            </button>
          }
        </div>
        <div class="flex gap-1">
          @if (createClickCallback.observers.length > 0) {
            <button matButton (click)="createClickCallback.emit($event)">
              Kreiraj <mat-icon>add</mat-icon>
            </button>
          }
          @if (exportClickCallback.observers.length > 0) {
            <button matButton (click)="exportClickCallback.emit(req())">
              Preuzmi <mat-icon>download</mat-icon>
            </button>
          }
        </div>
      </mat-toolbar-row>
    </mat-toolbar>

    <div class="px-4 py-3 border-b border-gray-200 bg-gray-50/50 empty:hidden">
      <ng-content select="[filters]"/>
    </div>

    <mat-progress-bar mode="indeterminate" [class.invisible]="state() !== 'loading'"/>

    <div class="relative w-full flex-1 min-h-0 overflow-y-hidden">
      <ng-content select="[table]"/>

      @if (state() === 'empty') {
        <div class="absolute inset-0 flex flex-col items-center justify-center gap-2 text-gray-400">
          <mat-icon class="!text-5xl !w-12 !h-12">inbox</mat-icon>
          <span class="text-lg">Nema rezultata</span>
        </div>
      }

      @if (state() === 'error') {
        <div class="absolute inset-0 flex flex-col items-center justify-center gap-3 text-gray-500">
          <mat-icon class="!text-5xl !w-12 !h-12 text-red-400">error_outline</mat-icon>
          <span class="text-lg">Došlo je do greške</span>
          <button matButton (click)="load()">
            <mat-icon>refresh</mat-icon>
            Pokušaj ponovo
          </button>
        </div>
      }
    </div>

    <mat-paginator
      [length]="data.total"
      [pageSize]="data.limit"
      [pageSizeOptions]="defaultPageSizes"
      (page)="onPageChange($event)"
      aria-label="Odaberi stranicu">
    </mat-paginator>
  `,
  styles: `
    :host { @apply flex flex-col h-full w-full overflow-hidden; }
    mat-form-field {
      --mat-form-field-container-height: 35px !important;
      --mat-form-field-container-vertical-padding: 5px !important;
    }
  `
})
export class PagedPage implements AfterContentInit {
  @Input() title!: string;
  @Output() createClickCallback = new EventEmitter<any>();
  @Output() exportClickCallback = new EventEmitter<any>();
  @Output() filterClickCallback = new EventEmitter<any>();
  @Input() loadCallback?: (req: any) => Promise<api.Paged<any>>;

  @Input() set filter(value: Record<string, any> | null) {
    if (value !== null && this.loadCallback != null) {
      this.req.update(prev => ({...prev, ...value, page: 1}));
      this.syncUrl();
      this.load();
    }
  }

  @Input() set reloadTrigger(count: number) {
    if (count > 0 && this.loadCallback != null) this.load();
  }

  @ContentChild(PagedTableBase) tableChild!: PagedTableBase;

  req = signal<any>({page: 1, limit: 100, order_by: 'id DESC'});

  data: api.Paged<any> = {page: 1, content: [], limit: 100, total: 0};

  state = signal<LoadState>('idle');
  private loadingTimer: ReturnType<typeof setTimeout> | null = null;
  private destroyRef = inject(DestroyRef);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  async load() {
    if (!this.loadCallback) return;
    this.loadingTimer = setTimeout(() => {
      this.state.set('loading');
      this.loadingTimer = null;
    }, 1000);
    try {
      this.data = await this.loadCallback(this.req());
      if (this.loadingTimer) {
        clearTimeout(this.loadingTimer);
        this.loadingTimer = null;
      }
      this.tableChild.setData(this.data.content);
      this.state.set(this.data.content.length === 0 ? 'empty' : 'idle');
    } catch {
      if (this.loadingTimer) {
        clearTimeout(this.loadingTimer);
        this.loadingTimer = null;
      }
      this.tableChild.setData([]);
      this.state.set('error');
    }
  }

  ngAfterContentInit() {
    const qp = this.route.snapshot.queryParams;
    if (Object.keys(qp).length > 0) {
      const initial: Record<string, any> = {};
      for (const [k, v] of Object.entries(qp)) {
        initial[k] = (k === 'page' || k === 'limit') ? Number(v) : v;
      }
      this.req.update(prev => ({...prev, ...initial}));
    }

    this.tableChild.sorted
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((sort: Sort) => {
        const order = sort.direction
          ? `${sort.active} ${sort.direction.toUpperCase()}`
          : 'id DESC';
        this.req.update(prev => ({...prev, order_by: order, page: 1}));
        this.syncUrl();
        this.load();
      });

    this.load();
  }

  onSearch(query: string) {
    this.req.update(prev => ({...prev, search: query, page: 1}));
    this.syncUrl();
    this.load();
  }

  onPageChange(event: PageEvent) {
    this.req.update(prev => ({...prev, page: event.pageIndex + 1, limit: event.pageSize}));
    this.syncUrl();
    this.load();
  }

  private syncUrl() {
    const qp: Record<string, any> = {};
    for (const [k, v] of Object.entries(this.req())) {
      if (v != null && v !== '') qp[k] = v;
    }
    this.router.navigate([], {relativeTo: this.route, queryParams: qp, replaceUrl: true});
  }

  protected readonly defaultPageSizes = defaultPageSizes;
}
