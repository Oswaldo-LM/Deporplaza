package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ReservaResponseDTO;
import com.deporplaza.reservas.dto.ReservaWebRequestDTO;
import com.deporplaza.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;


    public ReservaController(
            ReservaService reservaService
    ) {
        this.reservaService = reservaService;
    }


    @PostMapping("/web")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponseDTO crearReservaWeb(
            @Valid
            @RequestBody
            ReservaWebRequestDTO request
    ) {

        return reservaService
                .crearReservaWeb(request);
    }
}