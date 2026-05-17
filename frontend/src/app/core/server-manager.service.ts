import {computed, inject, Injectable, signal} from '@angular/core';
import {Api} from '../api';
import {LocalArticleService} from '../api/local/local-article.service';
import {LocalCompanyService} from '../api/local/local-company.service';
import {LocalReportService} from '../api/local/local-report.service';
import {LocalAccountService} from '../api/local/local-account.service';
import {LocalUmService} from '../api/local/local-um.service';
import {LocalNotesService} from '../api/local/local-notes.service';
import {LocalActivityLogService} from '../api/local/local-activitylog.service';
import {OnlineStatusService} from './online-status.service';

export interface Server {
  name: string;
  address: string;
  isExternal: boolean;
  token: string;
  api: Api;
}

@Injectable({providedIn: 'root'})
export class ServerManagerService {
  private readonly ws = inject(OnlineStatusService);
  private readonly _activeServer = signal<Server | null>({
    name: 'Lokal',
    address: 'http://localhost:8080',
    isExternal: false,
    token: '',
    api: {
      article: new LocalArticleService(),
      company: new LocalCompanyService(),
      report: new LocalReportService(),
      account: new LocalAccountService(),
      um: new LocalUmService(),
      notes: new LocalNotesService(),
      activityLog: new LocalActivityLogService(),
    },
  });
  private readonly _currentUsername = signal<string>('');
  private readonly _currentRole = signal<string | null>(null);

  readonly activeServer = computed(() => this._activeServer());
  readonly hasActiveServer = computed(() => this._activeServer() !== null);
  readonly currentUsername = computed(() => this._currentUsername());
  readonly currentRole = computed(() => this._currentRole());
  readonly isAdmin = computed(() => this._currentRole() === 'ADMIN');

  setActiveServer(server: Server, username: string, role?: string | null): void {
    this._activeServer.set(server);
    this._currentUsername.set(username);
    this._currentRole.set(role ?? null);
    this.ws.connectWebSocket(server.address, server.token);
  }

  setLocalServer(token: string, username: string, role?: string | null): void {
    this._currentUsername.set(username);
    this._currentRole.set(role ?? null);
    this._activeServer.set({
      name: 'Lokal',
      address: 'http://localhost:8080',
      isExternal: false,
      token,
      api: {
        article: new LocalArticleService(),
        company: new LocalCompanyService(),
        report: new LocalReportService(),
        account: new LocalAccountService(),
        um: new LocalUmService(),
        notes: new LocalNotesService(),
        activityLog: new LocalActivityLogService(),
      },
    });
    this.ws.startLocalPolling();
  }

  removeServer(): void {
    this._activeServer.set(null);
    this._currentRole.set(null);
    this.ws.disconnect();
  }
}
