import {Routes} from '@angular/router';
import {SigninPage} from './signin.page';

export const AUTH_LINKS = {
  signIn: () => `/auth/signin`,
}

export const AUTH_ROUTES: Routes = [
  {
    path: '',
    children: [
      {path: 'sign-in', component: SigninPage},
    ]
  }
];
