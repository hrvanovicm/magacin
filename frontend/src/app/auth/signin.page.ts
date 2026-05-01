import {Component, inject, OnInit, signal} from '@angular/core';
import {MatCardModule} from '@angular/material/card';
import {MatTabsModule} from '@angular/material/tabs';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatDivider} from '@angular/material/divider';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTooltipModule} from '@angular/material/tooltip';
import {AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ServerManagerService} from '../core/server-manager.service';
import {server} from '../../../wailsjs/go/models';
import {
  HasAdminAccounts,
  ListServers,
  RegisterFirstAdmin,
  ScanAndSaveServers,
  SignIn,
  UpsertServerLastUsed,
} from '../../../wailsjs/go/app/WailsApp';
import {ExternalApiFactory} from '../api/external/external-api';
import {ARTICLE_LINKS} from '../article/config';

type LocalMode = 'loading' | 'register' | 'login';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const pw = control.get('password')?.value;
  const confirm = control.get('confirmPassword')?.value;
  return pw && confirm && pw !== confirm ? {passwordMismatch: true} : null;
}

@Component({
  imports: [
    MatCardModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatIconModule,
    MatButton,
    MatIconButton,
    MatDivider,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    ReactiveFormsModule,
  ],
  template: `
    <div class="flex justify-center items-start w-full mt-12">
      <mat-card class="w-[520px]">
        <mat-card-content class="!p-0">
          <mat-tab-group>

            <mat-tab label="Moj računar">

              @if (localMode() === 'loading') {
                <div class="flex justify-center p-10">
                  <mat-spinner diameter="36"/>
                </div>

              } @else if (localMode() === 'register') {
                <div class="flex flex-col gap-4 p-6">
                  <p class="text-gray-500 text-sm">
                    Nije pronađen nijedan administratorski nalog. Kreirajte prvi nalog.
                  </p>
                  <form [formGroup]="registerForm" (ngSubmit)="register()" class="flex flex-col gap-3">
                    <mat-form-field>
                      <mat-label>Korisničko ime</mat-label>
                      <input matInput formControlName="username" autocomplete="off"/>
                      <mat-icon matPrefix>person</mat-icon>
                    </mat-form-field>
                    <mat-form-field>
                      <mat-label>Lozinka</mat-label>
                      <input matInput type="password" formControlName="password" autocomplete="new-password"/>
                      <mat-icon matPrefix>lock</mat-icon>
                    </mat-form-field>
                    <mat-form-field>
                      <mat-label>Potvrdi lozinku</mat-label>
                      <input matInput type="password" formControlName="confirmPassword" autocomplete="new-password"/>
                      @if (registerForm.hasError('passwordMismatch') && registerForm.get('confirmPassword')?.touched) {
                        <mat-error>Lozinke se ne poklapaju</mat-error>
                      }
                    </mat-form-field>
                    @if (localError()) {
                      <p class="text-red-500 text-sm">{{ localError() }}</p>
                    }
                    <button matButton="filled" type="submit" class="self-end"
                      [disabled]="!registerForm.valid || localLoading()">
                      Kreiraj admin nalog
                    </button>
                  </form>
                </div>

              } @else {
                <div class="flex flex-col gap-4 p-6">
                  <form [formGroup]="loginForm" (ngSubmit)="localLogin()" class="flex flex-col gap-3">
                    <mat-form-field>
                      <mat-label>Korisničko ime</mat-label>
                      <input matInput formControlName="username" autocomplete="username"/>
                      <mat-icon matPrefix>person</mat-icon>
                    </mat-form-field>
                    <mat-form-field>
                      <mat-label>Lozinka</mat-label>
                      <input matInput type="password" formControlName="password" autocomplete="current-password"/>
                      <mat-icon matPrefix>lock</mat-icon>
                    </mat-form-field>
                    @if (localError()) {
                      <p class="text-red-500 text-sm">{{ localError() }}</p>
                    }
                    <button matButton="filled" type="submit" class="self-end"
                      [disabled]="localLoading()">
                      Prijavi se
                    </button>
                  </form>
                </div>
              }
            </mat-tab>

            <mat-tab label="Server na mreži">

              @if (!externalLoginTarget()) {
                <div class="flex flex-col">

                  @if (storedServers().length > 0) {
                    <mat-list>
                      @for (s of storedServers(); track s.id; let last = $last) {
                        <mat-list-item>
                          <mat-icon matListItemIcon>dns</mat-icon>
                          <span matListItemTitle class="flex items-center gap-2">
                            {{ s.name }}
                            <span
                              class="w-2 h-2 rounded-full inline-block"
                              [class.bg-green-500]="onlineStatus()[s.address]"
                              [class.bg-gray-400]="!onlineStatus()[s.address]"
                              [matTooltip]="onlineStatus()[s.address] ? 'Online' : 'Offline'">
                            </span>
                          </span>
                          <span matListItemLine class="text-gray-500">{{ s.address }}</span>
                          <div matListItemMeta>
                            <button matButton
                              [disabled]="!onlineStatus()[s.address]"
                              (click)="selectExternalServer(s)">
                              Konektuj se
                            </button>
                          </div>
                        </mat-list-item>
                        @if (!last) { <mat-divider/> }
                      }
                    </mat-list>
                    <mat-divider/>
                  }

                  <div class="p-4 flex items-center gap-3">
                    <button matButton (click)="scanNetwork()" [disabled]="scanning()">
                      <mat-icon>wifi_find</mat-icon>
                      Skeniraj mrežu
                    </button>
                    @if (scanning()) {
                      <span class="text-sm text-gray-400 animate-pulse">Skeniranje u toku...</span>
                    }
                  </div>

                  @if (scanning()) {
                    <mat-progress-bar mode="indeterminate"/>
                  }

                  @if (!scanning() && scanRun() && storedServers().length === 0) {
                    <p class="px-4 pb-4 text-sm text-gray-400 text-center">
                      Nema dostupnih servera na mreži.
                    </p>
                  }

                </div>

              } @else {
                <div class="flex flex-col">
                  <div class="flex items-center gap-2 px-4 py-2 border-b">
                    <button matIconButton (click)="externalLoginTarget.set(null)">
                      <mat-icon>arrow_back</mat-icon>
                    </button>
                    <div class="flex flex-col">
                      <span class="font-medium">{{ externalLoginTarget()!.name }}</span>
                      <span class="text-xs text-gray-400">{{ externalLoginTarget()!.address }}</span>
                    </div>
                  </div>
                  <div class="flex flex-col gap-4 p-6">
                    <form [formGroup]="externalLoginForm" (ngSubmit)="externalLogin()" class="flex flex-col gap-3">
                      <mat-form-field>
                        <mat-label>Korisničko ime</mat-label>
                        <input matInput formControlName="username" autocomplete="username"/>
                        <mat-icon matPrefix>person</mat-icon>
                      </mat-form-field>
                      <mat-form-field>
                        <mat-label>Lozinka</mat-label>
                        <input matInput type="password" formControlName="password" autocomplete="current-password"/>
                        <mat-icon matPrefix>lock</mat-icon>
                      </mat-form-field>
                      @if (externalError()) {
                        <p class="text-red-500 text-sm">{{ externalError() }}</p>
                      }
                      <button matButton="filled" type="submit" class="self-end"
                        [disabled]="externalLoading()">
                        Prijavi se
                      </button>
                    </form>
                  </div>
                </div>
              }

            </mat-tab>

          </mat-tab-group>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: `
    :host { @apply flex h-full w-full overflow-hidden; }
    ::ng-deep .mat-mdc-tab-body-content { overflow: hidden; }
  `,
})
export class SigninPage implements OnInit {
  readonly router = inject(Router);
  readonly serverManager = inject(ServerManagerService);
  private readonly apiFactory = inject(ExternalApiFactory);

  readonly localMode = signal<LocalMode>('loading');
  readonly localLoading = signal(false);
  readonly localError = signal<string | null>(null);

  readonly loginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  readonly registerForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    confirmPassword: new FormControl('', Validators.required),
  }, {validators: passwordMatchValidator});

  readonly scanning = signal(false);
  readonly scanRun = signal(false);
  readonly storedServers = signal<server.Server[]>([]);
  readonly externalLoginTarget = signal<server.Server | null>(null);
  readonly externalLoading = signal(false);
  readonly externalError = signal<string | null>(null);
  readonly onlineStatus = signal<Record<string, boolean>>({});

  readonly externalLoginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  async ngOnInit() {
    try {
      const [hasAdmins, stored] = await Promise.all([HasAdminAccounts(), ListServers()]);
      this.localMode.set(hasAdmins ? 'login' : 'register');
      this.storedServers.set(stored ?? []);
      this.checkAllOnline(stored ?? []);
    } catch {
      this.localMode.set('login');
    }
  }

  private checkAllOnline(servers: server.Server[]) {
    for (const s of servers) {
      this.checkOnline(s.address);
    }
  }

  private async checkOnline(address: string) {
    try {
      await fetch(`${address}/api/health`, {method: 'HEAD', signal: AbortSignal.timeout(3000)});
      this.onlineStatus.update(s => ({...s, [address]: true}));
    } catch {
      this.onlineStatus.update(s => ({...s, [address]: false}));
    }
  }

  async localLogin() {
    if (!this.loginForm.valid) return;
    const {username, password} = this.loginForm.getRawValue();
    this.localLoading.set(true);
    this.localError.set(null);
    try {
      const result = await SignIn({username: username!, password: password!});
      this.serverManager.setLocalServer(result.Token, result.Account.username, result.Account.role);
      this.router.navigate([ARTICLE_LINKS.index()]);
    } catch (e: any) {
      this.localError.set(e?.message ?? 'Pogrešno korisničko ime ili lozinka');
    } finally {
      this.localLoading.set(false);
    }
  }

  async register() {
    if (!this.registerForm.valid || this.registerForm.hasError('passwordMismatch')) return;
    const {username, password} = this.registerForm.getRawValue();
    this.localLoading.set(true);
    this.localError.set(null);
    try {
      await RegisterFirstAdmin({id: 0, username: username!, password: password ?? undefined, role: undefined});
      const result = await SignIn({username: username!, password: password!});
      this.serverManager.setLocalServer(result.Token, result.Account.username, result.Account.role);
      this.router.navigate([ARTICLE_LINKS.index()]);
    } catch (e: any) {
      this.localError.set(e?.message ?? 'Greška pri kreiranju naloga');
    } finally {
      this.localLoading.set(false);
    }
  }

  async scanNetwork() {
    this.scanning.set(true);
    this.storedServers.set([]);
    this.onlineStatus.set({});
    try {
      const servers = await ScanAndSaveServers();
      this.storedServers.set(servers ?? []);
      this.checkAllOnline(servers ?? []);
    } finally {
      this.scanning.set(false);
      this.scanRun.set(true);
    }
  }

  selectExternalServer(s: server.Server) {
    this.externalLoginTarget.set(s);
    this.externalError.set(null);
    this.externalLoginForm.reset({username: s.lastUsedUsername ?? '', password: ''});
  }

  async externalLogin() {
    const target = this.externalLoginTarget();
    if (!target || !this.externalLoginForm.valid) return;

    const {username, password} = this.externalLoginForm.getRawValue();
    this.externalLoading.set(true);
    this.externalError.set(null);

    try {
      const res = await fetch(`${target.address}/api/auth/sign-in`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username, password}),
      });

      if (!res.ok) {
        const err = await res.json().catch(() => ({error: res.statusText}));
        throw new Error(err.error ?? res.statusText);
      }

      const signInResult: {Account: any; Token: string} = await res.json();
      const api = this.apiFactory.create(target.address);

      this.serverManager.setActiveServer({
        name: target.name,
        address: target.address,
        isExternal: true,
        token: signInResult.Token,
        api,
      }, signInResult.Account?.username ?? '', signInResult.Account?.role ?? null);

      await UpsertServerLastUsed({
        ServerName: target.name, 
        ServerAddress: target.address, 
        Username: username!
      }).catch(() => {});

      this.router.navigate([ARTICLE_LINKS.index()]);
    } catch (e: any) {
      this.externalError.set(e?.message ?? 'Greška pri prijavi');
    } finally {
      this.externalLoading.set(false);
    }
  }
}
