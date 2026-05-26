import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  template: `
    <div class="login-page">
      <form [formGroup]="form" (ngSubmit)="submit()" class="login-card">
        <h1>ArenaReserve</h1>
        <label>Email<input formControlName="email" type="email"></label>
        <label>Password<input formControlName="password" type="password"></label>
        <p class="error" *ngIf="error">{{error}}</p>
        <button [disabled]="form.invalid || loading">{{loading ? 'Ingresando...' : 'Ingresar'}}</button>
      </form>
    </div>
  `,
  styles: [`.login-page{min-height:100vh;display:grid;place-items:center;background:linear-gradient(135deg,#10233f,#0f766e)}.login-card{width:min(420px,92vw);background:#fff;border-radius:8px;padding:32px;box-shadow:0 24px 60px #0004;display:grid;gap:16px}h1{margin:0;color:#10233f}label{display:grid;gap:6px;color:#334155}input{height:42px;border:1px solid #cbd5e1;border-radius:6px;padding:0 12px}button{height:44px;background:#0f766e;color:#fff;border:0;border-radius:6px;font-weight:700}.error{color:#b91c1c;margin:0}`]
})
export class LoginComponent {
  loading = false;
  error = '';
  form = this.fb.group({
    email: ['admin@arenareserve.com', [Validators.required, Validators.email]],
    password: ['Admin12345', Validators.required]
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    const { email, password } = this.form.getRawValue();
    this.auth.login(email!, password!).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => { this.error = 'Credenciales invalidas'; this.loading = false; }
    });
  }
}
