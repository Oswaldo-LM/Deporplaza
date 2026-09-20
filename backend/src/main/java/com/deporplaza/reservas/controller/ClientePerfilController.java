package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ClientePerfilResponseDTO;
import com.deporplaza.reservas.exception.InvalidCredentialsException;
import com.deporplaza.reservas.service.ClientePerfilService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/cliente")
public class ClientePerfilController {

    private final ClientePerfilService clientePerfilService;


    public ClientePerfilController(
            ClientePerfilService clientePerfilService
    ) {

        this.clientePerfilService =
                clientePerfilService;
    }


    @GetMapping("/perfil")
    public ResponseEntity<ClientePerfilResponseDTO>
    obtenerPerfil(

            @AuthenticationPrincipal
            Jwt jwt

    ) {

        if (jwt == null) {

            throw new InvalidCredentialsException(
                    "El usuario no está autenticado"
            );
        }


        Object userIdClaim =
                jwt.getClaim(
                        "userId"
                );


        if (
                !(userIdClaim instanceof Number numero)
        ) {

            throw new InvalidCredentialsException(
                    "El token no contiene un usuario válido"
            );
        }


        Integer idUsuario =
                numero.intValue();


        return ResponseEntity.ok(
                clientePerfilService
                        .obtenerPerfil(
                                idUsuario
                        )
        );
    }
}