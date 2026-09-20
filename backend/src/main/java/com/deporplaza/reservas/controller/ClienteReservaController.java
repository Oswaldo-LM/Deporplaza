package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ReservaResponseDTO;
import com.deporplaza.reservas.exception.InvalidCredentialsException;
import com.deporplaza.reservas.service.ReservaService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/cliente")
public class ClienteReservaController {

    private final ReservaService
            reservaService;


    public ClienteReservaController(
            ReservaService reservaService
    ) {

        this.reservaService =
                reservaService;
    }


    // =========================================================
    // LISTAR MIS RESERVAS
    // =========================================================

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>>
    listarMisReservas(

            @AuthenticationPrincipal
            Jwt jwt

    ) {

        Integer idUsuario =
                obtenerIdUsuario(
                        jwt
                );


        return ResponseEntity.ok(
                reservaService
                        .listarMisReservas(
                                idUsuario
                        )
        );
    }


    // =========================================================
    // OBTENER UNA RESERVA
    // =========================================================

    @GetMapping("/mis-reservas/{idReserva}")
    public ResponseEntity<ReservaResponseDTO>
    obtenerMiReserva(

            @PathVariable
            Integer idReserva,

            @AuthenticationPrincipal
            Jwt jwt

    ) {

        Integer idUsuario =
                obtenerIdUsuario(
                        jwt
                );


        return ResponseEntity.ok(
                reservaService
                        .obtenerMiReserva(
                                idUsuario,
                                idReserva
                        )
        );
    }


    // =========================================================
    // EXTRAER USER ID DEL JWT
    // =========================================================

    private Integer obtenerIdUsuario(
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


        return numero.intValue();
    }

}