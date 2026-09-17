package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ReservaAdminResponseDTO;
import com.deporplaza.reservas.dto.ReservaPresencialRequestDTO;
import com.deporplaza.reservas.dto.ReservaResponseDTO;

import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.OrigenReserva;

import com.deporplaza.reservas.service.ReservaAdminService;
import com.deporplaza.reservas.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reservas")
public class ReservaAdminController {

    private final ReservaAdminService
            reservaAdminService;

    private final ReservaService
            reservaService;


    public ReservaAdminController(
            ReservaAdminService reservaAdminService,
            ReservaService reservaService
    ) {
        this.reservaAdminService =
                reservaAdminService;

        this.reservaService =
                reservaService;
    }


    @GetMapping
    public List<ReservaAdminResponseDTO> listar(
            @RequestParam(required = false)
            EstadoReserva estado,

            @RequestParam(required = false)
            OrigenReserva origen,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fecha,

            @RequestParam(required = false)
            Integer sedeId,

            @RequestParam(required = false)
            Integer canchaId
    ) {

        return reservaAdminService.listar(
                estado,
                origen,
                fecha,
                sedeId,
                canchaId
        );
    }


    @GetMapping("/{idReserva}")
    public ReservaAdminResponseDTO obtener(
            @PathVariable Integer idReserva
    ) {

        return reservaAdminService
                .obtenerPorId(idReserva);
    }


    @PostMapping("/presencial")
    public ReservaResponseDTO crearPresencial(
            @Valid
            @RequestBody
            ReservaPresencialRequestDTO request,

            @AuthenticationPrincipal
            Jwt jwt
    ) {

        return reservaService
                .crearReservaPresencial(
                        request,
                        obtenerIdUsuario(jwt)
                );
    }


    @PatchMapping("/{idReserva}/cancelar")
    public ReservaAdminResponseDTO cancelar(
            @PathVariable Integer idReserva,

            @AuthenticationPrincipal
            Jwt jwt
    ) {

        return reservaAdminService.cancelar(
                idReserva,
                obtenerIdUsuario(jwt)
        );
    }


    private Integer obtenerIdUsuario(
            Jwt jwt
    ) {

        Number id =
                jwt.getClaim("userId");

        if (id == null) {

            throw new IllegalStateException(
                    "El token no contiene userId"
            );
        }

        return id.intValue();
    }
}