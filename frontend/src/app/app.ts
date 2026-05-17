import {
  ApplicationConfig,
  Component,
  importProvidersFrom,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter, RouterOutlet, Routes } from '@angular/router';
import { authInterceptor } from './api/external/auth.interceptor';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MAT_DATE_LOCALE, MatNativeDateModule } from '@angular/material/core';
import localeBs from '@angular/common/locales/bs';
import { registerLocaleData } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { MainLayout } from './shared/main.layout';
import { GuestLayout } from './shared/guest.layout';
import { ServerManagerService } from './core/server-manager.service';
import { HasActiveServer, HasRoleGuard, AdminGuard } from './core/guards';

registerLocaleData(localeBs);

export const APP_INFO = {
  name: 'Magacin',
  version: '2.0.0-BETA',
}

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    canActivate: [HasActiveServer, HasRoleGuard],
    children: [
      { path: 'articles', loadChildren: () => import('./article/config').then(m => m.ARTICLE_ROUTES) },
      { path: 'reports', loadChildren: () => import('./report/config').then(m => m.REPORT_ROUTES) },
      { path: 'accounts', loadChildren: () => import('./accounts/config').then(m => m.ACCOUNT_ROUTES), canActivate: [AdminGuard] },
      { path: 'settings', loadChildren: () => import('./settings/config').then(m => m.SETTINGS_ROUTES), canActivate: [AdminGuard] }
    ],
  },
  {
    path: '',
    component: GuestLayout,
    children: [
      { path: 'auth', loadChildren: () => import('./auth/config').then(m => m.AUTH_ROUTES) },
    ]
  },
  {
    path: '**',
    redirectTo: 'auth/sign-in',
  }
];

export const appConfig: ApplicationConfig = {
  providers: [
    // Ng core
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),

    // App core
    ServerManagerService,

    // Material
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        subscriptSizing: 'dynamic',
        appearance: 'outline',
      },
    },
    importProvidersFrom(MatDatepickerModule, MatNativeDateModule),
    {
      provide: MAT_SNACK_BAR_DEFAULT_OPTIONS,
      useValue: {
        duration: 2000,
      },
    },

    { provide: LOCALE_ID, useValue: 'bs-BA' },
    { provide: MAT_DATE_LOCALE, useValue: 'bs-BA' },
  ],
};

@Component({
  selector: 'app-root',
  template: `
    <router-outlet></router-outlet> `,
  styles: `
    :host {
      @apply flex flex-col h-full w-full overflow-hidden;
    }
  `,
  imports: [RouterOutlet],
})
export class App {
}
