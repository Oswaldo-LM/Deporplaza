package com.deporplaza.reservas.dto;

import java.time.LocalTime;

public record BloqueDisponibilidadDTO(

        LocalTime horaInicio,

        boolean disponible,

        Integer maxExtrasDisponibles,

        Integer duracionMaximaMinutos

) {
}