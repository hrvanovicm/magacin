import {Component, inject, input, OnInit, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DatePipe} from '@angular/common';
import {ServerManagerService} from '../core/server-manager.service';

@Component({
  selector: 'app-note-tab',
  imports: [FormsModule, MatInputModule, MatFormFieldModule, DatePipe],
  template: `
    <div class="flex flex-col gap-4 p-4 h-full">
      <mat-form-field class="w-full flex-1" style="height: calc(100% - 30px)">
        <mat-label>Bilješke</mat-label>
        <textarea matInput [(ngModel)]="content" class="h-full"
                  style="min-height: 200px; resize: none;"
                  placeholder="Unesite bilješku...">
        </textarea>
      </mat-form-field>
      @if (updatedAt()) {
        <span class="text-xs text-gray-400 shrink-0">
          Posljednja izmjena: {{ updatedAt() | date:'dd.MM.yyyy HH:mm' }}
        </span>
      }
    </div>
  `,
  styles: `:host { @apply flex flex-col h-full overflow-hidden; }`,
})
export class NoteTabComponent implements OnInit {
  readonly subjectType = input.required<string>();
  readonly subjectId = input.required<number>();

  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);

  content = '';
  readonly updatedAt = signal<string | null>(null);

  async ngOnInit() {
    if (!this.subjectId()) return;
    try {
      const note = await this.serverManager.activeServer()!.api.notes.get({
        SubjectType: this.subjectType(),
        SubjectID: this.subjectId(),
      });
      this.content = note?.content ?? '';
      this.updatedAt.set(note?.updatedAt ?? null);
    } catch {}
  }

  async save() {
    try {
      await this.serverManager.activeServer()!.api.notes.save({
        subjectType: this.subjectType(),
        subjectId: this.subjectId(),
        content: this.content,
      });
      this.updatedAt.set(new Date().toISOString());
    } catch (error) {
      this.snackbar.open(`❌ Greška pri čuvanju bilješke: ${error}`);
    }
  }
}
