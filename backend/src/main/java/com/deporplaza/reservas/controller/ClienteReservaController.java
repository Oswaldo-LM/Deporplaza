package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ReservaResponseDTO;
import com.deporplaza.reservas.exception.InvalidCredentialsException;
import com.deporplaza.reservas.service.ReservaService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.GetMapping;
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


    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>>
    listarMisReservas(

            @AuthenticationPrincipal
            Jwt jwt

    ) {

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


        List<ReservaResponseDTO> reservas =
                reservaService
                        .listarMisReservas(
                                idUsuario
                        );


        return ResponseEntity.ok(
                reservas
        );
    }

}