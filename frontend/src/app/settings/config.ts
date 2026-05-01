import {Routes} from '@angular/router';
import {SettingsPage} from './settings.page';

export const SETTINGS_LINKS = {
  index: () => `/settings`,
}

export const SETTINGS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {path: '', component: SettingsPage},
    ]
  }
];
