import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatIcon} from '@angular/material/icon';

export interface ActiveFilterItem {
  key: string;
  label: string;
  value: string;
}

@Component({
  selector: 'app-active-filters',
  imports: [MatIcon],
  template: `
    <div class="flex flex-wrap gap-2 items-center">
      @for (item of items; track item.key) {
        <div class="flex items-center gap-1 bg-blue-50 text-blue-700 px-3 py-1.5 rounded-full text-sm border border-blue-200">
          <span class="font-medium">{{ item.label }}:</span>
          <span>{{ item.value }}</span>
          <mat-icon
            class="!w-4 !h-4 text-[16px] cursor-pointer hover:text-blue-900 ml-1 opacity-70 hover:opacity-100 transition-opacity"
            (click)="remove.emit(item.key)"
          >
            close
          </mat-icon>
        </div>
      }
    </div>
  `,
  styles: `:host { @apply block; }`
})
export class ActiveFiltersComponent {
  @Input() items: ActiveFilterItem[] = [];
  @Output() remove = new EventEmitter<string>();
}
