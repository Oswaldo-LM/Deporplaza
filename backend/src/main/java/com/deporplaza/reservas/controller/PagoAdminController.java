package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ComprobanteArchivoDTO;
import com.deporplaza.reservas.dto.PagoAdminResponseDTO;
import com.deporplaza.reservas.service.PagoAdminService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pagos")
public class PagoAdminController {

    private final PagoAdminService pagoAdminService;


    public PagoAdminController(
            PagoAdminService pagoAdminService
    ) {
        this.pagoAdminService = pagoAdminService;
    }


    // =========================================================
    // LISTAR PAGOS PENDIENTES
    // =========================================================

    @GetMapping("/pendientes")
    public List<PagoAdminResponseDTO> listarPendientes() {

        return pagoAdminService.listarPendientes();
    }


    // =========================================================
    // VER COMPROBANTE
    // =========================================================

    @GetMapping("/{idPago}/comprobante")
    public ResponseEntity<Resource> verComprobante(
            @PathVariable Integer idPago
    ) {

        ComprobanteArchivoDTO archivo =
                pagoAdminService.obtenerComprobante(idPago);


        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(
                                archivo.contentType()
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + archivo.nombreArchivo()
                                + "\""
                )
                .body(
                        archivo.resource()
                );
    }


    // =========================================================
    // APROBAR PAGO
    // =========================================================

    @PatchMapping("/{idPago}/aprobar")
    public PagoAdminResponseDTO aprobar(
            @PathVariable Integer idPago,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Integer idUsuarioValidador =
                obtenerIdUsuario(jwt);


        return pagoAdminService.aprobar(
                idPago,
                idUsuarioValidador
        );
    }


    // =========================================================
    // RECHAZAR PAGO
    // =========================================================

    @PatchMapping("/{idPago}/rechazar")
    public PagoAdminResponseDTO rechazar(
            @PathVariable Integer idPago,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Integer idUsuarioValidador =
                obtenerIdUsuario(jwt);


        return pagoAdminService.rechazar(
                idPago,
                idUsuarioValidador
        );
    }


    // =========================================================
    // OBTENER ID DEL USUARIO DESDE EL JWT
    // =========================================================

    private Integer obtenerIdUsuario(
            Jwt jwt
    ) {

        Number idUsuario =
                jwt.getClaim("userId");


        if (idUsuario == null) {
            throw new IllegalStateException(
                    "El token no contiene el identificador del usuario"
            );
        }


        return idUsuario.intValue();
    }
}