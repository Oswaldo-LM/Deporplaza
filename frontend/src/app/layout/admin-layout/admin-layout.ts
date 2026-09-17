import {
  Component,
  inject
} from '@angular/core';

import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';

import {
  AuthService
} from '../../core/services/auth';


@Component({
  selector: 'app-admin-layout',

  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet
  ],

  templateUrl: './admin-layout.html',

  styleUrl: './admin-layout.scss'
})
export class AdminLayout {

  private readonly authService =
    inject(AuthService);

  private readonly router =
    inject(Router);


  usuario =
    this.authService.obtenerUsuario();


  cerrarSesion(): void {

    this.authService.logout();

    this.router.navigate([
      '/admin/login'
    ]);

  }

}