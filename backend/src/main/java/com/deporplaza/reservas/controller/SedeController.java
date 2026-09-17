package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.SedeRequestDTO;
import com.deporplaza.reservas.dto.SedeResponseDTO;
import com.deporplaza.reservas.service.SedeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeService sedeService;

    public SedeController(SedeService sedeService) {
        this.sedeService = sedeService;
    }


    @GetMapping
    public List<SedeResponseDTO> listar() {
        return sedeService.listarTodas();
    }


    @GetMapping("/{id}")
    public SedeResponseDTO obtenerPorId(
            @PathVariable Integer id
    ) {
        return sedeService.obtenerPorId(id);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SedeResponseDTO crear(
            @Valid @RequestBody SedeRequestDTO request
    ) {
        return sedeService.crear(request);
    }


    @PutMapping("/{id}")
    public SedeResponseDTO actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody SedeRequestDTO request
    ) {
        return sedeService.actualizar(id, request);
    }

    @GetMapping("/activas")
    public List<SedeResponseDTO> listarActivas() {

    return sedeService
            .listarActivas();
    }
}