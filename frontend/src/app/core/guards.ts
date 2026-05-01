import {CanActivateFn, CanDeactivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {ServerManagerService} from './server-manager.service';
import {AUTH_LINKS, AUTH_ROUTES} from '../auth/config';

export interface CanDeactivateComponent {
  canDeactivate(): boolean;
}

export const UnsavedChangesGuard: CanDeactivateFn<CanDeactivateComponent> = (component) =>{
  return true;
}

export const HasActiveServer: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const serverManager = inject(ServerManagerService);

  if (!serverManager.hasActiveServer()) {
    return router.navigate([AUTH_LINKS.signIn()]);
  }

  return true;
}
