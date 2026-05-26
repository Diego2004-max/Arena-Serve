import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('arenareserve_token');
  const authReq = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(authReq).pipe(tap({
    error: error => {
      if (error?.status === 401) {
        localStorage.removeItem('arenareserve_token');
        localStorage.removeItem('arenareserve_role');
        router.navigate(['/login']);
      }
    }
  }));
};
