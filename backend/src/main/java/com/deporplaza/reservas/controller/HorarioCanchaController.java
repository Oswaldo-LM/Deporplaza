package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.HorarioCanchaRequestDTO;
import com.deporplaza.reservas.dto.HorarioCanchaResponseDTO;
import com.deporplaza.reservas.service.HorarioCanchaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioCanchaController {

    private final HorarioCanchaService horarioService;

    public HorarioCanchaController(
            HorarioCanchaService horarioService
    ) {
        this.horarioService = horarioService;
    }


    @GetMapping
    public List<HorarioCanchaResponseDTO> listar() {
        return horarioService.listarTodos();
    }


    @GetMapping("/{id}")
    public HorarioCanchaResponseDTO obtenerPorId(
            @PathVariable Integer id
    ) {
        return horarioService.obtenerPorId(id);
    }


    @GetMapping("/cancha/{idCancha}")
    public List<HorarioCanchaResponseDTO> listarPorCancha(
            @PathVariable Integer idCancha
    ) {
        return horarioService.listarPorCancha(idCancha);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioCanchaResponseDTO crear(
            @Valid @RequestBody HorarioCanchaRequestDTO request
    ) {
        return horarioService.crear(request);
    }


    @PutMapping("/{id}")
    public HorarioCanchaResponseDTO actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody HorarioCanchaRequestDTO request
    ) {
        return horarioService.actualizar(id, request);
    }
}