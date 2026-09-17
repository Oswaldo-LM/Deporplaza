package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.OrigenReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaResponseDTO(

        Integer idReserva,

        Integer idCliente,
        String nombreCliente,

        Integer idSede,
        String nombreSede,

        Integer idCancha,
        String nombreCancha,

        LocalDate fechaTurno,

        LocalTime horaInicio,
        LocalTime horaFin,

        Integer cantidadExtras,
        Integer duracionMinutos,

        BigDecimal precioHora,
        BigDecimal total,

        EstadoReserva estado,
        OrigenReserva origen,

        LocalDateTime fechaRegistro,
        LocalDateTime fechaExpiracion

) {
}