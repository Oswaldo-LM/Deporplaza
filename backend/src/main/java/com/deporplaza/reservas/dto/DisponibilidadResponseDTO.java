package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.DiaSemana;

import java.time.LocalDate;
import java.util.List;

public record DisponibilidadResponseDTO(

        Integer idSede,
        String nombreSede,

        LocalDate fecha,
        DiaSemana diaSemana,

        List<CanchaDisponibilidadDTO> canchas

) {
}