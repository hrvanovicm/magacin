import {Routes} from '@angular/router';
import {UnsavedChangesGuard} from '../core/guards';
import {AccountIndexPage} from './index.page';
import {AccountEditPage} from './edit.page';

export const ACCOUNTS_LINKS = {
  index: () => `/accounts`,
  create: () => `/accounts/new`,
  edit: (id: number) => `/accounts/${id}`,
}

export const ACCOUNT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {path: '', component: AccountIndexPage},
      {path: 'new', component: AccountEditPage, canDeactivate: [UnsavedChangesGuard]},
      {path: ':id', component: AccountEditPage, canDeactivate: [UnsavedChangesGuard]},
    ]
  }
];
