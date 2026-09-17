import {
  ChangeDetectorRef,
  Component,
  inject
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  Router,
  RouterLink
} from '@angular/router';

import {
  ClienteRegistroRequest
} from '../../core/models/auth.model';

import {
  AuthService
} from '../../core/services/auth';


@Component({
  selector: 'app-cliente-registro',

  imports: [
    ReactiveFormsModule,
    RouterLink
  ],

  templateUrl: './cliente-registro.html',

  styleUrl: './cliente-registro.scss'
})
export class ClienteRegistro {

  private readonly fb =
    inject(FormBuilder);

  private readonly authService =
    inject(AuthService);

  private readonly router =
    inject(Router);

  private readonly cdr =
    inject(ChangeDetectorRef);


  cargando =
    false;

  errorMensaje =
    '';


  readonly tiposDocumento = [
    'DNI',
    'CE',
    'PASAPORTE'
  ] as const;


  form =
    this.fb.nonNullable.group({

      nombreCompleto: [
        '',
        [
          Validators.required,
          Validators.maxLength(150)
        ]
      ],

      tipoDocumento: [
        'DNI',
        [
          Validators.required
        ]
      ],

      numDocumento: [
        '',
        [
          Validators.required,
          Validators.maxLength(20)
        ]
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email,
          Validators.maxLength(150)
        ]
      ],

      telefono: [
        '',
        [
          Validators.required,
          Validators.maxLength(20)
        ]
      ],

      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(100)
        ]
      ],

      confirmarPassword: [
        '',
        [
          Validators.required
        ]
      ]

    });


  registrar(): void {

    if (
      this.form.invalid
    ) {

      this.form.markAllAsTouched();

      return;
    }


    const datos =
      this.form.getRawValue();


    if (
      datos.password !==
      datos.confirmarPassword
    ) {

      this.errorMensaje =
        'Las contraseñas no coinciden.';

      this.cdr
        .markForCheck();

      return;
    }


    const request:
      ClienteRegistroRequest = {

      nombreCompleto:
        datos.nombreCompleto
          .trim(),

      tipoDocumento:
        datos.tipoDocumento as
          'DNI'
          | 'CE'
          | 'PASAPORTE',

      numDocumento:
        datos.numDocumento
          .trim(),

      email:
        datos.email
          .trim()
          .toLowerCase(),

      telefono:
        datos.telefono
          .trim(),

      password:
        datos.password

    };


    this.cargando =
      true;

    this.errorMensaje =
      '';


    this.authService
      .registrarCliente(
        request
      )
      .subscribe({

        next: respuesta => {

          this.cargando =
            false;


          if (
            respuesta.rol !==
            'CLIENTE'
          ) {

            this.authService
              .logout();


            this.errorMensaje =
              'No se pudo crear la cuenta de cliente.';


            this.cdr
              .markForCheck();

            return;
          }


          /*
           * El backend devuelve el JWT
           * después del registro.
           *
           * Por eso el cliente queda
           * autenticado automáticamente.
           *
           * Por ahora lo enviamos al inicio.
           */
          this.router.navigate([
            '/cliente/mis-reservas'
          ]);

        },


        error: (
          error: HttpErrorResponse
        ) => {

          this.cargando =
            false;


          if (
            error.status === 409
          ) {

            this.errorMensaje =
              error.error?.message
              ?? 'Ya existe una cuenta con esos datos.';

          } else if (
            error.status === 400
          ) {

            this.errorMensaje =
              error.error?.message
              ?? 'Revisa los datos ingresados.';

          } else {

            this.errorMensaje =
              error.error?.message
              ?? 'No se pudo crear la cuenta.';
          }


          this.cdr
            .markForCheck();
        }

      });
  }

}