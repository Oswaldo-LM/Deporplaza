package com.deporplaza.reservas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record DashboardProximaReservaDTO(

        Long idReserva,

        String nombreCliente,

        String nombreSede,

        String nombreCancha,

        LocalDate fechaTurno,

        LocalTime horaInicio,

        LocalTime horaFin,

        BigDecimal total,

        String origen

) {
}