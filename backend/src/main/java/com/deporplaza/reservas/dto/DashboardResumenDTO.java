package com.deporplaza.reservas.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResumenDTO(

        long reservasHoy,

        long confirmadasHoy,

        long pagosPendientes,

        BigDecimal ingresosHoy,

        List<DashboardProximaReservaDTO> proximasReservas

) {
}