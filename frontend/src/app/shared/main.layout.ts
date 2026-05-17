import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { Component, computed, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UnitMeasureIndexDialog } from '../unit-measure/index.dialog';
import { CompanyIndexDialog } from '../company/company-index.dialog';
import { MatToolbar } from '@angular/material/toolbar';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { ServerManagerService } from '../core/server-manager.service';
import { OnlineStatusService } from '../core/online-status.service';
import { APP_INFO } from '../app';
import { ARTICLE_LINKS } from '../article/config';
import { REPORT_LINKS } from '../report/config';
import { ACCOUNTS_LINKS } from '../accounts/config';
import { SETTINGS_LINKS } from '../settings/config';
import { MatMenuModule } from '@angular/material/menu';
import { AUTH_LINKS } from '../auth/config';

@Component({
  imports: [MatToolbar, MatButton, RouterOutlet, MatDivider, RouterLink, MatIcon, MatMenuModule],
  template: `
    <mat-toolbar class="shrink-0">
      <span>{{ APP_INFO.name }}</span>

      <span class="flex-1 basis-auto"></span>

      <button matButton [routerLink]="[ARTICLE_LINKS.index()]">Roba</button>
      <button matButton [routerLink]="[REPORT_LINKS.index()]">Izvještaji</button>
      @if (serverManager.isAdmin()) {
        <button matButton [routerLink]="[ACCOUNTS_LINKS.index()]">Korisnici</button>
      }
      <button matButton (click)="openCompanyDialog()">Kompanije</button>
      <button matButton (click)="openUnitMeasureDialog()">Mjerne jedinice</button>

      <div class="mx-2"></div>

      <button matButton color="filled" class="!bg-gray-500" [matMenuTriggerFor]="menu">
        <div class="flex items-center text-white gap-3">
          @if (serverManager.activeServer()?.isExternal) {
            <mat-icon>cloud</mat-icon>
            {{ serverManager.activeServer()!.name }}
          } @else {
            <mat-icon>computer_2</mat-icon>
            Lokalni server
          }
        </div>
      </button>

      <mat-menu #menu="matMenu">
        @if (serverManager.isAdmin()) {
          <button mat-menu-item [routerLink]="[SETTINGS_LINKS.index()]">
            <mat-icon>settings</mat-icon>
            <span>Postavke</span>
          </button>
        }
        <button mat-menu-item (click)="logout()">
          <mat-icon>logout</mat-icon>
          <span>Odjavi se</span>
        </button>
      </mat-menu>
    </mat-toolbar>

    <main class="flex flex-1 min-h-0 w-full overflow-hidden flex-col">
      <mat-divider></mat-divider>
      <router-outlet></router-outlet>
    </main>

    <footer class="flex items-center gap-3 px-4 h-7 text-xs text-gray-800 bg-gray-50 border-t border-gray-200 shrink-0">
      <span>v{{ version }}</span>
      <mat-divider vertical/>
      @if (otherUsers().length > 0) {
        <span>Koristiš softver sa {{ formatUsers(otherUsers()) }}</span>
      } @else {
        <span>Nema drugih aktivnih korisnika</span>
      }
    </footer>
  `,
  styles: `
    :host {
      @apply flex flex-col h-full w-full overflow-hidden;
    }
  `
})
export class MainLayout {
  readonly dialog = inject(MatDialog);
  readonly router = inject(Router);
  readonly serverManager = inject(ServerManagerService);
  readonly ws = inject(OnlineStatusService);
  readonly version = APP_INFO.version;
  readonly otherUsers = computed(() =>
    this.ws.connectedUsers().filter(u => u !== this.serverManager.currentUsername())
  );

  formatUsers(users: string[]): string {
    if (users.length === 0) return '';
    if (users.length === 1) return users[0];
    return users.slice(0, -1).join(', ') + ' i ' + users[users.length - 1];
  }

  openUnitMeasureDialog() {
    this.dialog.open(UnitMeasureIndexDialog, {
      width: '600px',
      maxWidth: '95vw',
      height: '600px',
    });
  }

  openCompanyDialog() {
    this.dialog.open(CompanyIndexDialog, {
      width: '1200px',
      height: '600px',
    });
  }

  logout() {
    this.serverManager.removeServer();
    this.router.navigate([AUTH_LINKS.signIn()]);
  }

  protected readonly APP_INFO = APP_INFO;
  protected readonly ARTICLE_LINKS = ARTICLE_LINKS;
  protected readonly REPORT_LINKS = REPORT_LINKS;
  protected readonly ACCOUNTS_LINKS = ACCOUNTS_LINKS;
  protected readonly SETTINGS_LINKS = SETTINGS_LINKS;
}
