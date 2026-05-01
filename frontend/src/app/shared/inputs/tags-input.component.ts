import {Component, inject, Input, input, OnInit, signal} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {MatChipInputEvent, MatChipsModule} from '@angular/material/chips';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-tags-input',
  imports: [MatButtonModule, MatFormFieldModule, MatChipsModule, ReactiveFormsModule, MatIconModule],
  template: `
    <mat-form-field class="w-full">
      <mat-label>{{ label() }}</mat-label>
      <mat-chip-grid #chipGrid>
        @for (tag of keywords(); track tag) {
          <mat-chip-row (removed)="remove(tag)">
            {{ tag }}
            <button matChipRemove [attr.aria-label]="'ukloni ' + tag">
              <mat-icon>cancel</mat-icon>
            </button>
          </mat-chip-row>
        }
      </mat-chip-grid>
      <input [matChipInputFor]="chipGrid" (matChipInputTokenEnd)="add($event)" />
    </mat-form-field>
  `,
})
export class TagsInputComponent implements OnInit {
  private readonly announcer = inject(LiveAnnouncer);

  readonly label = input.required<string>();
  @Input() control!: FormControl<string[] | null>;

  readonly keywords = signal<string[]>([]);

  ngOnInit(): void {
    const v = this.control?.value;
    if (Array.isArray(v) && v.length > 0) {
      this.keywords.set([...(v as string[])]);
    }
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      this.keywords.update(kws => [...kws, value]);
      this.sync();
    }
    event.chipInput!.clear();
  }

  remove(tag: string): void {
    this.keywords.update(kws => {
      const idx = kws.indexOf(tag);
      if (idx < 0) return kws;
      this.announcer.announce(`removed ${tag}`);
      return kws.filter((_, i) => i !== idx);
    });
    this.sync();
  }

  private sync(): void {
    this.control.setValue([...this.keywords()]);
    this.control.markAsDirty();
  }
}
