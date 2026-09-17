package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.EstadoCancha;
import com.deporplaza.reservas.enums.SuperficieCancha;

import java.math.BigDecimal;

public record CanchaResponseDTO(
        Integer idCancha,
        Integer idSede,
        String nombreSede,
        String nombre,
        SuperficieCancha superficie,
        BigDecimal precioHora,
        EstadoCancha estado
) {
}