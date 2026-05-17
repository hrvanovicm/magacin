import {Component, inject} from '@angular/core';
import {Router} from '@angular/router';
import {ServerManagerService} from '../core/server-manager.service';
import {PagedPage} from '../shared/page/paged.page';
import {TableComponent} from './table.component';
import {ACCOUNTS_LINKS} from './config';

@Component({
  imports: [PagedPage, TableComponent],
  template: `
    <app-paged-page
      title="Korisnici"
      [loadCallback]="serverManager.activeServer()!.api.account.listPaged"
      (createClickCallback)="router.navigate([ACCOUNTS_LINKS.create()])"
    >
      <app-account-table table
                         (rowClick)="router.navigate([ACCOUNTS_LINKS.edit($event.id)], {state: {account: $event}})"/>
    </app-paged-page>
  `,
  styles: `:host {
    @apply flex flex-col h-full w-full overflow-hidden;
  }`,
})
export class AccountIndexPage {
  readonly serverManager = inject(ServerManagerService);
  readonly router = inject(Router);

  protected readonly ACCOUNTS_LINKS = ACCOUNTS_LINKS;
}
