import {Component, EventEmitter, Input, input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';

@Component({
  selector: 'app-amount-input',
  imports: [MatFormFieldModule, MatInputModule, ReactiveFormsModule],
  template: `
    <div [class.compact]="compact()">
      <mat-form-field class="w-full">
        <mat-label>{{ label() }}</mat-label>
        <input matInput [formControl]="control"
               (keypress)="onKeyPress($event)"
               (blur)="onBlur($event)" />
        <div matSuffix class="mr-3 text-sm text-secondary">{{ unitMeasure()?.name ?? '' }}</div>
      </mat-form-field>
    </div>
  `,
})
export class AmountInputComponent implements OnChanges {
  readonly label = input.required<string>();
  readonly compact = input<boolean>(false);
  readonly unitMeasure = input<any>(null);


  @Input() initValue?: number;
  @Input() control: FormControl<any> = new FormControl(0);
  @Input() disabled = false;

  @Output() onValueChange = new EventEmitter<number>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['initValue'] && this.initValue !== undefined) {
      this.control.setValue(String(this.initValue), {emitEvent: false});
    }
    if ('disabled' in changes) {
      this.disabled ? this.control.disable() : this.control.enable();
    }
  }

  onKeyPress(event: KeyboardEvent): boolean {
    if (!/[0-9.,-]/.test(String.fromCharCode(event.charCode))) {
      event.preventDefault();
      return false;
    }
    return true;
  }

  onBlur(event: FocusEvent): void {
    let value = (event.target as HTMLInputElement).value;
    if (!value) {
      this.control.setValue('0');
      this.onValueChange.emit(0);
      return;
    }
    const num = parseFloat(value.replace(/,/g, '.')) || 0;
    const str = Number.isInteger(num) ? String(num) : String(num);
    this.control.setValue(str);
    this.onValueChange.emit(num);
  }
}
