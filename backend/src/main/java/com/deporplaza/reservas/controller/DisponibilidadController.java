package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.DisponibilidadResponseDTO;
import com.deporplaza.reservas.service.DisponibilidadService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/disponibilidad")
public class DisponibilidadController {

    private final DisponibilidadService
            disponibilidadService;


    public DisponibilidadController(
            DisponibilidadService disponibilidadService
    ) {
        this.disponibilidadService =
                disponibilidadService;
    }


    @GetMapping
    public DisponibilidadResponseDTO consultar(
            @RequestParam Integer sedeId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fecha
    ) {

        return disponibilidadService.consultar(
                sedeId,
                fecha
        );
    }

    
}