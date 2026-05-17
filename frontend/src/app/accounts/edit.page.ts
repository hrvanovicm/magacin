import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {Location} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators} from '@angular/forms';
import {map} from 'rxjs';
import {MatToolbar} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatTabsModule} from '@angular/material/tabs';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ServerManagerService} from '../core/server-manager.service';
import {Account} from '../api';
import {ACCOUNTS_LINKS} from './config';

const ROLES = ['ADMIN', 'MODERATOR', 'GUEST'];

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const pw = control.get('newPassword')?.value ?? control.get('password')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return pw && confirm && pw !== confirm ? {passwordMismatch: true} : null;
}

@Component({
  imports: [
    ReactiveFormsModule,
    MatToolbar,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTabsModule,
  ],
  template: `
    <mat-toolbar>
      <button matIconButton (click)="goBack()"><mat-icon>arrow_back</mat-icon></button>
      <span class="ml-2 text-xl">{{ isNew ? 'Novi korisnik' : account()?.username }}</span>
      <span class="flex-1"></span>
      @if (!isNew) {
        <button matButton color="warn" class="mr-2" (click)="delete()">
          <mat-icon>delete</mat-icon>
          Obriši
        </button>
      }
      <button matButton="filled" (click)="save()" [disabled]="!isFormValid()">Sačuvaj</button>
    </mat-toolbar>

    <div class="flex flex-1 min-h-0 gap-0 overflow-hidden">

      <div class="w-1/2 overflow-y-auto p-6 border-r">
        <form class="flex flex-col gap-5 max-w-[420px] mx-auto" [formGroup]="form">
          <mat-form-field class="w-full">
            <mat-label>Korisničko ime</mat-label>
            <input matInput formControlName="username"/>
          </mat-form-field>

          <mat-form-field class="w-full">
            <mat-label>Uloga</mat-label>
            <mat-select formControlName="role">
              <mat-option [value]="null">—</mat-option>
              @for (role of roles; track role) {
                <mat-option [value]="role">{{ role }}</mat-option>
              }
            </mat-select>
          </mat-form-field>
        </form>
      </div>

      <div class="flex-1 min-w-0 flex flex-col overflow-hidden p-6">
        <mat-tab-group class="flex-1 overflow-hidden">

          <mat-tab label="Sigurnost">
            @if (isNew) {
              <form class="flex flex-col gap-4 pt-4 max-w-[420px]" [formGroup]="initialPasswordForm">
                <mat-form-field class="w-full">
                  <mat-label>Lozinka</mat-label>
                  <input matInput type="password" formControlName="password"/>
                </mat-form-field>
                <mat-form-field class="w-full">
                  <mat-label>Potvrdi lozinku</mat-label>
                  <input matInput type="password" formControlName="confirmPassword"/>
                  @if (initialPasswordForm.hasError('passwordMismatch') && initialPasswordForm.get('confirmPassword')?.touched) {
                    <mat-error>Lozinke se ne poklapaju</mat-error>
                  }
                </mat-form-field>
              </form>
            } @else if (isAdmin()) {
              <form class="flex flex-col gap-4 pt-4 max-w-[420px]" [formGroup]="adminPasswordForm">
                <mat-form-field class="w-full">
                  <mat-label>Nova lozinka</mat-label>
                  <input matInput type="password" formControlName="newPassword"/>
                </mat-form-field>
                <mat-form-field class="w-full">
                  <mat-label>Potvrdi novu lozinku</mat-label>
                  <input matInput type="password" formControlName="confirmPassword"/>
                  @if (adminPasswordForm.hasError('passwordMismatch') && adminPasswordForm.get('confirmPassword')?.touched) {
                    <mat-error>Lozinke se ne poklapaju</mat-error>
                  }
                </mat-form-field>
                <div>
                  <button matButton="filled" (click)="adminChangePassword()" [disabled]="!adminPasswordForm.valid">
                    Postavi lozinku
                  </button>
                </div>
              </form>
            } @else {
              <form class="flex flex-col gap-4 pt-4 max-w-[420px]" [formGroup]="changePasswordForm">
                <mat-form-field class="w-full">
                  <mat-label>Trenutna lozinka</mat-label>
                  <input matInput type="password" formControlName="currentPassword"/>
                </mat-form-field>
                <mat-form-field class="w-full">
                  <mat-label>Nova lozinka</mat-label>
                  <input matInput type="password" formControlName="newPassword"/>
                </mat-form-field>
                <mat-form-field class="w-full">
                  <mat-label>Potvrdi novu lozinku</mat-label>
                  <input matInput type="password" formControlName="confirmPassword"/>
                  @if (changePasswordForm.hasError('passwordMismatch') && changePasswordForm.get('confirmPassword')?.touched) {
                    <mat-error>Lozinke se ne poklapaju</mat-error>
                  }
                </mat-form-field>
                <div>
                  <button matButton="filled" (click)="changePassword()" [disabled]="!changePasswordForm.valid">
                    Promijeni lozinku
                  </button>
                </div>
              </form>
            }
          </mat-tab>

        </mat-tab-group>
      </div>

    </div>
  `,
  styles: `:host { @apply flex flex-col h-full w-full overflow-hidden; }`,
})
export class AccountEditPage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly serverManager = inject(ServerManagerService);
  private readonly snackbar = inject(MatSnackBar);

  isNew = true;
  readonly account = signal<Account | null>(null);
  readonly isAdmin = computed(() => this.serverManager.isAdmin());

  readonly form = new FormGroup({
    username: new FormControl('', Validators.required),
    role: new FormControl<string | null>(null),
  });

  readonly initialPasswordForm = new FormGroup({
    password: new FormControl(''),
    confirmPassword: new FormControl(''),
  }, {validators: passwordMatchValidator});

  readonly changePasswordForm = new FormGroup({
    currentPassword: new FormControl('', Validators.required),
    newPassword: new FormControl('', Validators.required),
    confirmPassword: new FormControl('', Validators.required),
  }, {validators: passwordMatchValidator});

  readonly adminPasswordForm = new FormGroup({
    newPassword: new FormControl('', Validators.required),
    confirmPassword: new FormControl('', Validators.required),
  }, {validators: passwordMatchValidator});

  readonly isFormValid = toSignal(
    this.form.statusChanges.pipe(map(s => s === 'VALID')),
    {initialValue: this.form.valid},
  );

  async ngOnInit() {
    this.route.paramMap.subscribe(async params => {
      const idStr = params.get('id');
      this.isNew = idStr === null;
      if (this.isNew) return;

      const id = Number(idStr);
      const acc = await this.serverManager.activeServer()!.api.account.get({ID: id as any});

      if (!acc) return;
      this.account.set(acc);
      this.form.patchValue({
        username: acc.username,
        role: acc.role ?? null,
      });
    });
  }

  async save() {
    if (!this.form.valid) return;
    const v = this.form.getRawValue();

    const password = this.isNew && this.initialPasswordForm.value.password
      ? this.initialPasswordForm.value.password
      : undefined;

    try {
      const savedId = await this.serverManager.activeServer()!.api.account.save({
        id: this.isNew ? 0 : this.account()!.id,
        username: v.username!,
        role: v.role ?? undefined,
        password,
      });
      this.snackbar.open(`Uspješno sačuvan korisnik ${v.username}!`);
      
      if (this.isNew && typeof savedId === 'number') {
        this.router.navigate([ACCOUNTS_LINKS.edit(savedId)], {replaceUrl: true});
      } else if (!this.isNew) {
        const acc = await this.serverManager.activeServer()!.api.account.get({ID: this.account()!.id as any});
        if (acc) this.account.set(acc);
      }
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    }
  }

  async changePassword() {
    if (!this.changePasswordForm.valid || this.changePasswordForm.hasError('passwordMismatch')) return;
    const v = this.changePasswordForm.getRawValue();
    try {
      await this.serverManager.activeServer()!.api.account.changePassword({
        id: this.account()!.id,
        current_password: v.currentPassword!,
        new_password: v.newPassword!,
      });
      this.snackbar.open('Lozinka uspješno promijenjena!');
      this.changePasswordForm.reset();
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    }
  }

  async adminChangePassword() {
    if (!this.adminPasswordForm.valid || this.adminPasswordForm.hasError('passwordMismatch')) return;
    const v = this.adminPasswordForm.getRawValue();
    try {
      await this.serverManager.activeServer()!.api.account.adminChangePassword({
        id: this.account()!.id,
        new_password: v.newPassword!,
      });
      this.snackbar.open('Lozinka uspješno promijenjena!');
      this.adminPasswordForm.reset();
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    }
  }

  async delete() {
    if (!confirm(`Da li sigurno želite obrisati korisnika ${this.account()?.username}?`)) return;
    try {
      await this.serverManager.activeServer()!.api.account.delete({ID: this.account()!.id as any});
      this.snackbar.open('Korisnik obrisan.');
      this.router.navigate([ACCOUNTS_LINKS.index()]);
    } catch (error) {
      this.snackbar.open(`Greška: ${error}`);
    }
  }

  goBack() { this.location.back(); }

  protected readonly roles = ROLES;
}
