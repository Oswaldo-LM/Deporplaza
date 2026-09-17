package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.EstadoPago;
import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoAdminResponseDTO(

        Integer idPago,
        Integer idReserva,

        Integer idCliente,
        String nombreCliente,

        MetodoPago metodoPago,
        BigDecimal monto,
        String numOperacion,

        EstadoPago estadoPago,
        EstadoReserva estadoReserva,

        LocalDateTime fechaPago,
        LocalDateTime fechaValidacion,

        Integer idUsuarioValidador,
        String nombreUsuarioValidador,

        String urlComprobante

) {
}