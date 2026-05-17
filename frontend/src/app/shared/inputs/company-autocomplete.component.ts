import { Component, ElementRef, inject, Input, input, OnInit, ViewChild, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Company } from '../../api';
import { ServerManagerService } from '../../core/server-manager.service';

@Component({
  selector: 'app-company-autocomplete',
  imports: [MatChipsModule, MatFormFieldModule, MatInputModule, MatAutocompleteModule, ReactiveFormsModule],
  template: `
    <mat-form-field class="w-full">
      <mat-label>{{ label() }}</mat-label>
      <input #inputEl type="text" matInput [formControl]="control"
             [matAutocomplete]="auto" (input)="filter()" (focus)="load()" />
      <mat-autocomplete #auto="matAutocomplete" [displayWith]="displayFn">
        @for (option of options(); track option.id) {
          <mat-option [value]="option.name">
            {{ option.name }}
            @if (option.inHouseProduction) {
              <mat-chip class="ml-3">Vlastita proizvodnja</mat-chip>
            }
          </mat-option>
        }
      </mat-autocomplete>
    </mat-form-field>
  `,
})
export class CompanyAutocompleteComponent implements OnInit {
  @ViewChild('inputEl') inputEl!: ElementRef<HTMLInputElement>;

  private readonly serverManager = inject(ServerManagerService);
  private all: Company[] = [];

  readonly label = input.required<string>();
  @Input() control!: FormControl<Company | string | null>;

  readonly options = signal<Company[]>([]);

  async ngOnInit(): Promise<void> {
    await this.load();
    const v = this.control.value;
    if (v && typeof v === 'string' && v.length > 0) {
      this.control.setValue(this.resolve(v));
    }
    this.control.valueChanges.subscribe(val => {
      if (typeof val === 'string') {
        this.control.setValue(this.resolve(val), { emitEvent: false });
      }
    });
  }

  async load(): Promise<void> {
    if (!this.all.length) {
      this.all = await this.serverManager.activeServer()!.api.company.list({});
    }
    this.filter();
  }

  private resolve(name: string): Company {
    const match = this.all.find(o => o.name?.toLowerCase() === name.toLowerCase());
    return Company.createFrom({
      name: match ? match.name : name.toLowerCase(),
      inHouseProduction: match?.inHouseProduction ?? false,
    });
  }

  readonly displayFn = (value: any): string => {
    if (!value) return '';
    if (typeof value === 'string') return value;
    const n = value.name ?? '';
    return n.charAt(0).toUpperCase() + n.slice(1).toLowerCase();
  };

  filter(): void {
    const q = this.inputEl?.nativeElement.value.toLowerCase() ?? '';
    this.options.set(this.all.filter(o => o.name?.toLowerCase().includes(q)));
  }
}
