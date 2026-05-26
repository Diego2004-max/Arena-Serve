export type Rol = 'ADMIN' | 'EMPLEADO';

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  rol: Rol;
}

export interface UsuarioSistema {
  id: number;
  email: string;
  rol: Rol;
  activo: boolean;
}
