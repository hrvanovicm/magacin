import { Component, ElementRef, inject, Input, input, signal, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormField, MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ServerManagerService } from '../../core/server-manager.service';

@Component({
  selector: 'app-unit-measure-autocomplete',
  imports: [MatFormField, MatInputModule, MatAutocompleteModule, ReactiveFormsModule],
  template: `
    <mat-form-field class="w-full">
      <mat-label>{{ label() }}</mat-label>
      <input #inputEl type="text" matInput [formControl]="control"
             [matAutocomplete]="auto" (input)="filter()" (focus)="load()" />
      <mat-autocomplete requireSelection #auto="matAutocomplete" [displayWith]="displayFn">
        @for (option of options(); track option.id) {
          <mat-option [value]="option">{{ option.name }}</mat-option>
        }
      </mat-autocomplete>
    </mat-form-field>
  `,
})
export class UnitMeasureAutocompleteComponent {
  @ViewChild('inputEl') inputEl!: ElementRef<HTMLInputElement>;

  private readonly serverManager = inject(ServerManagerService);
  private all: any[] = [];

  readonly label = input.required<string>();
  @Input() control!: FormControl<any | null>;

  readonly options = signal<any[]>([]);

  async load() {
    if (!this.all.length) {
      this.all = await this.serverManager.activeServer()!.api.um.list({});
    }
    this.filter();
  }

  readonly displayFn = (opt: any): string => opt && 'name' in opt ? opt.name : (opt ?? '');

  filter(): void {
    const q = this.inputEl.nativeElement.value.toLowerCase();
    this.options.set(this.all.filter(o => o.name.toLowerCase().includes(q)));
  }
}
