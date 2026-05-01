import {Component, inject, input, OnInit, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {DatePipe} from '@angular/common';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatPaginatorModule, PageEvent} from '@angular/material/paginator';
import {ServerManagerService} from '../core/server-manager.service';
import {ActivityLog} from '../api';

@Component({
  selector: 'app-log-tab',
  imports: [
    FormsModule,
    DatePipe,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    MatPaginatorModule,
  ],
  template: `
    <div class="flex flex-col h-full overflow-hidden">

      <div class="p-3 border-b shrink-0">
        <mat-form-field class="w-full">
          <mat-label>Pretraga</mat-label>
          <mat-icon matPrefix>search</mat-icon>
          <input matInput [(ngModel)]="search" (ngModelChange)="onSearchChange()" placeholder="Filtriraj logove..." />
          @if (search) {
            <button matIconButton matSuffix (click)="clearSearch()">
              <mat-icon>close</mat-icon>
            </button>
          }
        </mat-form-field>
      </div>

      <div class="flex-1 overflow-y-auto">
        @if (loading()) {
          <p class="px-4 py-6 text-gray-400 text-sm text-center">Učitavanje...</p>
        } @else if (logs().length === 0) {
          <p class="px-4 py-6 text-gray-400 text-sm text-center">Nema zabilježenih aktivnosti.</p>
        } @else {
          <mat-list>
            @for (log of logs(); track log.id) {
              <mat-list-item>
                <mat-icon matListItemIcon class="text-gray-400">history</mat-icon>
                <span matListItemTitle>{{ log.description }}</span>
                <span matListItemLine class="text-xs text-gray-400">
                  {{ log.createdAt | date:'dd.MM.yyyy HH:mm' }}
                  @if (log.actorUsername) { · {{ log.actorUsername }} }
                </span>
              </mat-list-item>
            }
          </mat-list>
        }
      </div>

      <div class="border-t shrink-0">
        <mat-paginator
          [length]="total()"
          [pageSize]="pageSize"
          [pageIndex]="page() - 1"
          [pageSizeOptions]="[10, 20, 50]"
          (page)="onPage($event)"
          showFirstLastButtons />
      </div>

    </div>
  `,
  styles: `:host { @apply flex flex-col h-full overflow-hidden; }`,
})
export class LogTabComponent implements OnInit {
  readonly subjectType = input.required<string>();
  readonly subjectId = input.required<number>();

  private readonly serverManager = inject(ServerManagerService);

  readonly logs = signal<ActivityLog[]>([]);
  readonly total = signal(0);
  readonly loading = signal(false);
  readonly page = signal(1);
  readonly pageSize = 10;

  search = '';
  private searchDebounce: ReturnType<typeof setTimeout> | null = null;

  async ngOnInit() {
    if (this.subjectId()) await this.load();
  }

  async load() {
    this.loading.set(true);
    try {
      const result = await this.serverManager.activeServer()!.api.activityLog.listPaged({
        subjectType: this.subjectType(),
        subjectId: this.subjectId(),
        search: this.search || undefined,
        page: this.page(),
        limit: this.pageSize,
      });
      this.logs.set(result.content ?? []);
      this.total.set(result.total ?? 0);
    } finally {
      this.loading.set(false);
    }
  }

  onSearchChange() {
    if (this.searchDebounce) clearTimeout(this.searchDebounce);
    this.searchDebounce = setTimeout(() => {
      this.page.set(1);
      this.load();
    }, 300);
  }

  clearSearch() {
    this.search = '';
    this.page.set(1);
    this.load();
  }

  onPage(event: PageEvent) {
    this.page.set(event.pageIndex + 1);
    this.load();
  }
}
