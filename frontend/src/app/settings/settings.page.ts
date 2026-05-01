import {Component, inject, OnInit, signal} from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatButtonModule} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatIconModule} from '@angular/material/icon';
import {FormsModule} from '@angular/forms';
import {
  GetCompanyLogo,
  GetLocalServerConfig,
  RemoveCompanyLogo,
  SaveServerLocalConfig,
  UploadCompanyLogo,
} from '../../../wailsjs/go/app/WailsApp';
import {server} from '../../../wailsjs/go/models';
import LocalConfig = server.LocalConfig;
import {MatToolbar} from '@angular/material/toolbar';
import {Location} from '@angular/common';
import {MatCard, MatCardContent} from '@angular/material/card';

@Component({
  imports: [
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    MatToolbar,
    MatCard,
    MatCardContent,
  ],
  template: `
    <mat-toolbar color="surface">
      <button mat-icon-button (click)="goBack()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <span class="ml-2 text-xl">Postavke</span>
    </mat-toolbar>

    <div class="flex-1 overflow-y-auto pb-10">
      <div class="flex justify-center w-full mt-10">
        <mat-card class="w-[480px] px-4">
          <mat-card-content>
            <mat-tab-group dynamicHeight> <mat-tab label="Opće">
              <div class="flex flex-col gap-6 pt-6">
                <mat-form-field appearance="outline">
                  <mat-label>Naziv kompanije</mat-label>
                  <input matInput [ngModel]="config().company_name" (ngModelChange)="patch({ company_name: $event })" />
                </mat-form-field>

                <div class="flex row justify-between items-center p-4 border rounded-lg bg-gray-50/50">
                  <div class="flex flex-col gap-2">
                    <span class="text-sm text-gray-600 font-medium">Logo kompanije</span>
                    @if (logoUrl()) {
                      <img [src]="logoUrl()" alt="Logo" class="h-16 max-w-[150px] object-contain border rounded bg-white p-1" />
                    }
                  </div>
                  <div class="flex flex-col gap-1">
                    <button mat-button (click)="uploadLogo()">
                      <mat-icon>upload</mat-icon>
                      {{ logoUrl() ? 'Zamijeni' : 'Postavi' }}
                    </button>
                    @if (logoUrl()) {
                      <button mat-button color="warn" (click)="removeLogo()">
                        <mat-icon>delete</mat-icon>
                        Ukloni
                      </button>
                    }
                  </div>
                </div>

                <div class="flex justify-end mt-4">
                  <button mat-flat-button (click)="saveGeneral()">Sačuvaj</button>
                </div>
              </div>
            </mat-tab>

              <mat-tab label="Server">
                <div class="flex flex-col gap-6 pt-6">
                  <mat-form-field appearance="outline">
                    <mat-label>Naziv servera</mat-label>
                    <input matInput [ngModel]="config().name" (ngModelChange)="patch({ name: $event })" />
                  </mat-form-field>

                  <div class="py-2">
                    <mat-slide-toggle
                      [ngModel]="config().is_public"
                      (ngModelChange)="patch({ is_public: $event })">
                      Javni server
                    </mat-slide-toggle>
                  </div>

                  <div class="flex justify-end mt-4">
                    <button mat-flat-button (click)="saveServer()">Sačuvaj</button>
                  </div>
                </div>
              </mat-tab>
            </mat-tab-group>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: `:host { @apply flex flex-col h-full w-full overflow-hidden; }`,
})
export class SettingsPage implements OnInit {
  private readonly snackbar = inject(MatSnackBar);
  private readonly location = inject(Location);

  config = signal<LocalConfig>(LocalConfig.createFrom({}));
  logoUrl = signal<string>('');

  goBack() { this.location.back(); }

  async ngOnInit() {
    const [cfg, logo] = await Promise.all([
      GetLocalServerConfig(),
      GetCompanyLogo(),
    ]);
    this.config.set(cfg);
    this.logoUrl.set(logo ?? '');
  }

  patch(partial: Partial<LocalConfig>) {
    this.config.update(prev => LocalConfig.createFrom({...prev, ...partial}));
  }

  async saveGeneral() {
    try {
      await SaveServerLocalConfig(this.config());
      this.snackbar.open('✅ Postavke sačuvane!');
    } catch (error) {
      this.snackbar.open(`❌ Greška: ${error}`);
    }
  }

  async saveServer() {
    try {
      await SaveServerLocalConfig(this.config());
      this.snackbar.open('✅ Postavke sačuvane!');
    } catch (error) {
      this.snackbar.open(`❌ Greška: ${error}`);
    }
  }

  async uploadLogo() {
    try {
      await UploadCompanyLogo();
      const logo = await GetCompanyLogo();
      this.logoUrl.set(logo ?? '');
    } catch (error) {
      this.snackbar.open(`❌ Greška: ${error}`);
    }
  }

  async removeLogo() {
    try {
      await RemoveCompanyLogo();
      this.logoUrl.set('');
    } catch (error) {
      this.snackbar.open(`❌ Greška: ${error}`);
    }
  }
}
