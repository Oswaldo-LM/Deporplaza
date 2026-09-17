package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.DiaSemana;
import com.deporplaza.reservas.enums.EstadoHorario;

import java.time.LocalTime;

public record HorarioCanchaResponseDTO(
        Integer idHorarioCancha,
        Integer idCancha,
        String nombreCancha,
        DiaSemana diaSemana,
        LocalTime horaApertura,
        LocalTime horaCierre,
        EstadoHorario estado
) {
}