package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.EstadoSede;

public record SedeResponseDTO(
        Integer idSede,
        String nombre,
        String direccion,
        String telefono,
        EstadoSede estado
) {
}