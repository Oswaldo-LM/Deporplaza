package com.deporplaza.reservas.dto;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public record ReporteReservaDTO(

        Integer idReserva,

        LocalDateTime fechaRegistro,

        LocalDate fechaTurno,

        LocalTime horaInicio,

        LocalTime horaFin,

        String cliente,

        String tipoDocumento,

        String numDocumento,

        String sede,

        String cancha,

        String superficie,

        String origen,

        String estadoReserva,

        String metodoPago,

        String estadoPago,

        BigDecimal total

) {
}