package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.PagoComprobanteRequestDTO;
import com.deporplaza.reservas.dto.PagoResponseDTO;
import com.deporplaza.reservas.service.PagoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PagoController {

    private final PagoService pagoService;


    public PagoController(
            PagoService pagoService
    ) {
        this.pagoService = pagoService;
    }


    @PostMapping(
            value = "/reservas/{idReserva}/pago",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public PagoResponseDTO registrarComprobante(
            @PathVariable Integer idReserva,

            @Valid
            @ModelAttribute
            PagoComprobanteRequestDTO request
    ) {

        return pagoService.registrarComprobante(
                idReserva,
                request
        );
    }
}