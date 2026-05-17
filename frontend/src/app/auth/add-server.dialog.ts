import {Component, inject} from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  template: `
    <h2 mat-dialog-title>Dodaj server</h2>
    <mat-dialog-content>
      <form [formGroup]="form" (ngSubmit)="save()">
        <div class="flex flex-col gap-4 pt-2">
          <mat-form-field class="w-full">
            <mat-label>Naziv</mat-label>
            <input matInput type="text" formControlName="name">
          </mat-form-field>
          <mat-form-field class="w-full">
            <mat-label>Adresa</mat-label>
            <input matInput type="text" formControlName="address">
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button matButton mat-dialog-close>Close</button>
      <button matButton (click)="save()">Sačuvaj</button>
    </mat-dialog-actions>
  `,
})
export class AddServerDialogComponent {
  readonly dialogRef = inject(MatDialogRef<any>);

  readonly form = new FormGroup({
    name: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required]),
  })

  save() {
    if (!this.form.valid) {
      return;
    }

    this.dialogRef.close(this.form.value);
  }
}
