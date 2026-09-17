package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.CanchaRequestDTO;
import com.deporplaza.reservas.dto.CanchaResponseDTO;
import com.deporplaza.reservas.service.CanchaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canchas")
public class CanchaController {

    private final CanchaService canchaService;

    public CanchaController(
            CanchaService canchaService
    ) {
        this.canchaService = canchaService;
    }


    @GetMapping
    public List<CanchaResponseDTO> listar() {
        return canchaService.listarTodas();
    }


    @GetMapping("/{id}")
    public CanchaResponseDTO obtenerPorId(
            @PathVariable Integer id
    ) {
        return canchaService.obtenerPorId(id);
    }


    @GetMapping("/sede/{idSede}")
    public List<CanchaResponseDTO> listarPorSede(
            @PathVariable Integer idSede
    ) {
        return canchaService.listarPorSede(idSede);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanchaResponseDTO crear(
            @Valid @RequestBody CanchaRequestDTO request
    ) {
        return canchaService.crear(request);
    }


    @PutMapping("/{id}")
    public CanchaResponseDTO actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CanchaRequestDTO request
    ) {
        return canchaService.actualizar(id, request);
    }
}