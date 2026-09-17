package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.DiaSemana;
import com.deporplaza.reservas.enums.EstadoHorario;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record HorarioCanchaRequestDTO(

        @NotNull(message = "La cancha es obligatoria")
        Integer idCancha,

        @NotNull(message = "El día de la semana es obligatorio")
        DiaSemana diaSemana,

        @NotNull(message = "La hora de apertura es obligatoria")
        LocalTime horaApertura,

        @NotNull(message = "La hora de cierre es obligatoria")
        LocalTime horaCierre,

        @NotNull(message = "El estado es obligatorio")
        EstadoHorario estado

) {
}