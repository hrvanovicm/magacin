import {Component, ElementRef, inject, Input, input, signal, ViewChild} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {ServerManagerService} from '../../core/server-manager.service';

@Component({
  selector: 'app-user-autocomplete',
  imports: [MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule],
  template: `
    <mat-form-field class="w-full">
      <mat-label>{{ label() }}</mat-label>
      <input #inputEl type="text" matInput [formControl]="control"
             [matAutocomplete]="auto" (input)="filter()" (focus)="load()" />
      <mat-autocomplete #auto="matAutocomplete">
        @for (option of filteredOptions(); track option) {
          <mat-option [value]="option">{{ option }}</mat-option>
        }
      </mat-autocomplete>
    </mat-form-field>
  `,
})
export class UserAutocompleteComponent {
  @ViewChild('inputEl') inputEl!: ElementRef<HTMLInputElement>;

  private readonly serverManager = inject(ServerManagerService);

  readonly label = input.required<string>();
  @Input() control!: FormControl<string>;

  private allOptions: string[] = [];
  readonly filteredOptions = signal<string[]>([]);

  async load(): Promise<void> {
    if (!this.allOptions.length) {
      this.allOptions = await this.serverManager.activeServer()!.api.report.listSignUsers();
    }
    this.filter();
  }

  filter(): void {
    const query = this.inputEl.nativeElement.value.toLowerCase();
    this.filteredOptions.set(
      this.allOptions.filter(o => o.toLowerCase().includes(query)),
    );
  }
}
