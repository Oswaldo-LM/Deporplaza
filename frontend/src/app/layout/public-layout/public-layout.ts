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
  selector: 'app-public-layout',

  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet
  ],

  templateUrl: './public-layout.html',

  styleUrl: './public-layout.scss'
})
export class PublicLayout {

  readonly authService =
    inject(AuthService);

  private readonly router =
    inject(Router);


  cerrarSesionCliente(): void {

    this.authService
      .logout();


    this.router.navigate([
      '/'
    ]);
  }

}