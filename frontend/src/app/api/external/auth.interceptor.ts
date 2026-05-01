import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {ServerManagerService} from '../../core/server-manager.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(ServerManagerService).activeServer()?.token;
  if (token) {
    req = req.clone({headers: req.headers.set('Authorization', `Bearer ${token}`)});
  }
  return next(req);
};
