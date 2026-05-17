import { Component, computed, effect, EventEmitter, Input, input, OnChanges, Output, signal, SimpleChanges, untracked } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-amount-input',
  imports: [MatFormFieldModule, MatInputModule, ReactiveFormsModule, MatSelectModule],
  template: `
    <div [class.compact]="compact()">
      <mat-form-field class="w-full">
        <mat-label>{{ label() }}</mat-label>
        <input matInput [value]="displayValue"
               (input)="onInput($event)"
               (blur)="onBlur($event)"
               [disabled]="disabled" />
        @if (allConversions().length > 0) {
          <mat-select matSuffix [value]="selectedUnitId()" (selectionChange)="selectedUnitId.set($event.value)" class="mr-3 text-sm text-secondary w-20">
            <mat-option [value]="unitMeasure()?.id">{{ unitMeasure()?.name ?? '' }}</mat-option>
            @for (conv of allConversions(); track conv.toUnitMeasureId) {
              <mat-option [value]="conv.toUnitMeasureId">{{ conv.toUnitMeasure?.name ?? '' }}</mat-option>
            }
          </mat-select>
        } @else {
          <div matSuffix class="mr-3 text-sm text-secondary">{{ unitMeasure()?.name ?? '' }}</div>
        }
      </mat-form-field>
    </div>
  `,
})
export class AmountInputComponent implements OnChanges {
  readonly label = input.required<string>();
  readonly compact = input<boolean>(false);
  readonly unitMeasure = input<any>(null);
  readonly conversions = input<any[]>([]);
  readonly globalConversions = input<any[]>([]);

  readonly selectedUnitId = signal<number | null>(null);

  readonly allConversions = computed(() => {
    const article = this.conversions();
    const global = this.globalConversions();
    const seen = new Set(article.map(c => c.toUnitMeasureId));
    return [...article, ...global.filter(g => !seen.has(g.toUnitMeasureId))];
  });

  @Input() control: FormControl<any> = new FormControl(0);
  @Input() initValue?: number;
  @Input() disabled = false;

  @Output() onValueChange = new EventEmitter<number>();

  constructor() {
    effect(() => {
      const um = this.unitMeasure();
      untracked(() => this.selectedUnitId.set(um?.id ?? null));
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['initValue'] && this.initValue !== undefined && this.control.value !== this.initValue) {
      this.control.setValue(this.initValue, { emitEvent: false });
    }
    if ('disabled' in changes) {
      this.disabled ? this.control.disable() : this.control.enable();
    }
  }

  get displayValue(): number {
    const val = this.control.value || 0;
    const selectedId = this.selectedUnitId();
    if (!selectedId || selectedId === this.unitMeasure()?.id) return val;
    const conv = this.allConversions().find(c => c.toUnitMeasureId === selectedId);
    return conv ? val / conv.factor : val;
  }

  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;
    if (value === '' || value === '-') return;
    if (!/^-?[0-9]*[.,]?[0-9]*$/.test(value)) {
      input.value = String(this.displayValue);
      return;
    }
    const num = parseFloat(value.replace(/,/g, '.'));
    if (isNaN(num)) return;
    const base = this.toBase(num);
    this.control.setValue(base, { emitEvent: false });
    this.onValueChange.emit(base);
  }

  onBlur(event: FocusEvent): void {
    const value = (event.target as HTMLInputElement).value;
    if (!value || value === '-') {
      this.control.setValue(0);
      this.onValueChange.emit(0);
      return;
    }
    const base = this.toBase(parseFloat(value.replace(/,/g, '.')) || 0);
    this.control.setValue(base);
    this.onValueChange.emit(base);
  }

  private toBase(num: number): number {
    const selectedId = this.selectedUnitId();
    if (!selectedId || selectedId === this.unitMeasure()?.id) return num;
    const conv = this.allConversions().find(c => c.toUnitMeasureId === selectedId);
    return conv ? num * conv.factor : num;
  }
}
