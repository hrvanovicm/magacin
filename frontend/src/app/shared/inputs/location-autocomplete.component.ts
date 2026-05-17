import { Component, ElementRef, inject, Input, input, signal, ViewChild } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { ServerManagerService } from '../../core/server-manager.service';

@Component({
  selector: 'app-location-autocomplete',
  imports: [MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule],
  template: `
    <mat-form-field class="w-full">
      <mat-label>{{ label() }}</mat-label>
      <input #inputEl type="text" matInput [formControl]="control"
             [matAutocomplete]="auto" (input)="filter()" (focus)="load()" />
      <mat-autocomplete #auto="matAutocomplete">
        @for (option of options(); track option) {
          <mat-option [value]="option">{{ option }}</mat-option>
        }
      </mat-autocomplete>
    </mat-form-field>
  `,
})
export class LocationAutocompleteComponent {
  @ViewChild('inputEl') inputEl!: ElementRef<HTMLInputElement>;

  private readonly serverManager = inject(ServerManagerService);
  private all: string[] = [];

  readonly label = input.required<string>();
  @Input() control!: FormControl<string>;

  readonly options = signal<string[]>([]);

  async load(): Promise<void> {
    if (!this.all.length) {
      this.all = await this.serverManager.activeServer()!.api.report.listPublicLocations();
    }
    this.filter();
  }

  filter(): void {
    const q = this.inputEl.nativeElement.value.toLowerCase();
    this.options.set(this.all.filter(o => o.toLowerCase().includes(q)));
  }
}
