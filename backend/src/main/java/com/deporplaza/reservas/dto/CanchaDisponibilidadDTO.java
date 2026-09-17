package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.SuperficieCancha;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record CanchaDisponibilidadDTO(

        Integer idCancha,
        String nombre,
        SuperficieCancha superficie,
        BigDecimal precioHora,

        boolean tieneHorario,

        LocalTime horaApertura,
        LocalTime horaCierre,

        List<BloqueDisponibilidadDTO> bloques

) {
}