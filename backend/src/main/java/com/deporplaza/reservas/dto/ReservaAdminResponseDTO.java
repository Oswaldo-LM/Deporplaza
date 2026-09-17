package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservaAdminResponseDTO(

        Integer idReserva,
        EstadoReserva estado,
        OrigenReserva origen,

        LocalDateTime fechaRegistro,
        LocalDateTime fechaExpiracion,
        BigDecimal total,

        Integer idCliente,
        String nombreCliente,
        TipoDocumento tipoDocumento,
        String numDocumento,
        String emailCliente,
        String telefonoCliente,

        Integer idSede,
        String nombreSede,

        Integer idCancha,
        String nombreCancha,
        SuperficieCancha superficie,

        LocalDate fechaTurno,
        LocalTime horaInicio,
        LocalTime horaFin,
        Integer cantidadExtras,
        BigDecimal precioHora,
        BigDecimal subtotal,

        Integer idPago,
        MetodoPago metodoPago,
        EstadoPago estadoPago,
        LocalDateTime fechaPago

) {
}