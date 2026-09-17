package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.ClienteRegistroRequestDTO;
import com.deporplaza.reservas.dto.LoginRequestDTO;
import com.deporplaza.reservas.dto.LoginResponseDTO;

import com.deporplaza.reservas.entity.Cliente;
import com.deporplaza.reservas.entity.Usuario;

import com.deporplaza.reservas.enums.EstadoUsuario;
import com.deporplaza.reservas.enums.RolUsuario;

import com.deporplaza.reservas.exception.InvalidCredentialsException;
import com.deporplaza.reservas.exception.ResourceConflictException;

import com.deporplaza.reservas.repository.ClienteRepository;
import com.deporplaza.reservas.repository.UsuarioRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthService {

    private final AuthenticationManager
            authenticationManager;

    private final UsuarioRepository
            usuarioRepository;

    private final ClienteRepository
            clienteRepository;

    private final PasswordEncoder
            passwordEncoder;

    private final JwtService
            jwtService;


    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {

        this.authenticationManager =
                authenticationManager;

        this.usuarioRepository =
                usuarioRepository;

        this.clienteRepository =
                clienteRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }


    /*
     * LOGIN ANTIGUO
     *
     * Lo dejamos temporalmente para no romper
     * nada mientras terminamos de cambiar Angular.
     *
     * Ahora se comporta como login administrativo.
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO login(
            LoginRequestDTO request
    ) {

        return loginAdmin(
                request
        );
    }


    /*
     * LOGIN ADMINISTRADOR
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO loginAdmin(
            LoginRequestDTO request
    ) {

        Usuario usuario =
                autenticarUsuario(
                        request
                );


        if (
                usuario.getRol()
                        != RolUsuario.ADMIN
        ) {

            throw new InvalidCredentialsException(
                    "Email o contraseña de administrador incorrectos"
            );
        }


        return generarRespuestaLogin(
                usuario
        );
    }


    /*
     * LOGIN CLIENTE
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO loginCliente(
            LoginRequestDTO request
    ) {

        Usuario usuario =
                autenticarUsuario(
                        request
                );


        if (
                usuario.getRol()
                        != RolUsuario.CLIENTE
        ) {

            throw new InvalidCredentialsException(
                    "Email o contraseña de cliente incorrectos"
            );
        }


        return generarRespuestaLogin(
                usuario
        );
    }


    /*
     * REGISTRO DE CLIENTE
     *
     * El cliente puede haberse registrado previamente
     * como invitado en TB_CLIENTE.
     *
     * Si encontramos el mismo documento, reutilizamos
     * ese registro y lo vinculamos a TB_USUARIO.
     */
    @Transactional
    public LoginResponseDTO registrarCliente(
            ClienteRegistroRequestDTO request
    ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();


        String documento =
                request.numDocumento()
                        .trim();


        /*
         * No puede existir otra cuenta
         * con el mismo correo.
         */
        if (
                usuarioRepository
                        .existsByEmail(
                                email
                        )
        ) {

            throw new ResourceConflictException(
                    "Ya existe una cuenta registrada con este correo."
            );
        }


        /*
         * Buscamos al cliente por documento.
         *
         * Esto permite recuperar las reservas
         * realizadas anteriormente como invitado.
         */
        Cliente cliente =
                clienteRepository
                        .findByTipoDocumentoAndNumDocumento(
                                request.tipoDocumento(),
                                documento
                        )
                        .orElse(
                                null
                        );


        /*
         * Si ese cliente ya tiene un usuario,
         * no puede registrar una segunda cuenta.
         */
        if (
                cliente != null
                &&
                cliente.getUsuario() != null
        ) {

            throw new ResourceConflictException(
                    "Este documento ya está asociado a una cuenta."
            );
        }


        /*
         * Crear usuario.
         */
        Usuario usuario =
                new Usuario();


        usuario.setNombre(
                request.nombreCompleto()
                        .trim()
        );


        usuario.setEmail(
                email
        );


        usuario.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );


        usuario.setRol(
                RolUsuario.CLIENTE
        );


        usuario.setEstado(
                EstadoUsuario.ACTIVO
        );


        usuario =
                usuarioRepository.save(
                        usuario
                );


        /*
         * Si nunca había reservado anteriormente,
         * creamos un nuevo registro TB_CLIENTE.
         */
        if (
                cliente == null
        ) {

            cliente =
                    new Cliente();


            cliente.setTipoDocumento(
                    request.tipoDocumento()
            );


            cliente.setNumDocumento(
                    documento
            );
        }


        /*
         * Actualizamos la información del cliente.
         *
         * Esto sirve tanto para un cliente nuevo
         * como para uno que antes reservó
         * sin tener una cuenta.
         */
        cliente.setNombreCompleto(
                request.nombreCompleto()
                        .trim()
        );


        cliente.setEmail(
                email
        );


        cliente.setTelefono(
                request.telefono()
                        .trim()
        );


        /*
         * Vincular:
         *
         * TB_CLIENTE
         *      ↓
         * TB_USUARIO
         */
        cliente.setUsuario(
                usuario
        );


        clienteRepository.save(
                cliente
        );


        /*
         * Login automático después
         * del registro.
         */
        return generarRespuestaLogin(
                usuario
        );
    }


    /*
     * MÉTODO INTERNO PARA AUTENTICAR
     *
     * Se utiliza tanto para ADMIN
     * como para CLIENTE.
     */
    private Usuario autenticarUsuario(
            LoginRequestDTO request
    ) {

        String email =
                request.email()
                        .trim()
                        .toLowerCase();


        try {

            authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(

                            email,

                            request.password()

                    )
            );

        } catch (
                AuthenticationException ex
        ) {

            throw new InvalidCredentialsException(
                    "Email o contraseña incorrectos"
            );
        }


        return usuarioRepository
                .findByEmail(
                        email
                )
                .orElseThrow(
                        () ->
                                new InvalidCredentialsException(
                                        "Email o contraseña incorrectos"
                                )
                );
    }


    /*
     * MÉTODO INTERNO PARA CREAR JWT
     * Y CONSTRUIR LA RESPUESTA.
     */
    private LoginResponseDTO generarRespuestaLogin(
            Usuario usuario
    ) {

        String token =
                jwtService.generarToken(
                        usuario
                );


        return new LoginResponseDTO(

                token,

                "Bearer",

                usuario.getIdUsuario(),

                usuario.getNombre(),

                usuario.getEmail(),

                usuario.getRol()

        );
    }

}