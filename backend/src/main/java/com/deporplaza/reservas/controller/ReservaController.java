package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ReservaResponseDTO;
import com.deporplaza.reservas.dto.ReservaWebRequestDTO;
import com.deporplaza.reservas.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;


    public ReservaController(
            ReservaService reservaService
    ) {

        this.reservaService =
                reservaService;
    }


    @PostMapping("/web")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponseDTO crearReservaWeb(

            @Valid
            @RequestBody
            ReservaWebRequestDTO request,

            @AuthenticationPrincipal
            Jwt jwt

    ) {

        Integer idUsuarioAutenticado =
                null;


        /*
         * Si el usuario está autenticado,
         * obtenemos el userId almacenado
         * dentro del JWT.
         *
         * Si no inició sesión:
         *
         * jwt == null
         *
         * y la reserva continuará
         * funcionando como invitado.
         */
        if (jwt != null) {

            Object userIdClaim =
                    jwt.getClaim(
                            "userId"
                    );


            if (
                    userIdClaim
                    instanceof Number numero
            ) {

                idUsuarioAutenticado =
                        numero.intValue();
            }
        }


        return reservaService
                .crearReservaWeb(
                        request,
                        idUsuarioAutenticado
                );
    }
}