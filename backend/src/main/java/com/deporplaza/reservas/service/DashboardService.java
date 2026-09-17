package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.DashboardProximaReservaDTO;
import com.deporplaza.reservas.dto.DashboardResumenDTO;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.sql.Date;
import java.sql.Time;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import java.util.List;


@Service
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;


    public DashboardService(
            JdbcTemplate jdbcTemplate
    ) {

        this.jdbcTemplate =
                jdbcTemplate;
    }


    @Transactional(readOnly = true)
    public DashboardResumenDTO obtenerResumen() {

        /*
         * El negocio opera en Perú.
         */
        ZoneId zona =
                ZoneId.of(
                        "America/Lima"
                );


        LocalDate hoy =
                LocalDate.now(
                        zona
                );


        LocalTime ahora =
                LocalTime.now(
                        zona
                );


        long reservasHoy =
                obtenerReservasHoy(
                        hoy
                );


        long confirmadasHoy =
                obtenerConfirmadasHoy(
                        hoy
                );


        long pagosPendientes =
                obtenerPagosPendientes();


        BigDecimal ingresosHoy =
                obtenerIngresosHoy(
                        hoy
                );


        List<DashboardProximaReservaDTO>
                proximasReservas =
                obtenerProximasReservas(
                        hoy,
                        ahora
                );


        return new DashboardResumenDTO(

                reservasHoy,

                confirmadasHoy,

                pagosPendientes,

                ingresosHoy,

                proximasReservas

        );
    }


    private long obtenerReservasHoy(
            LocalDate hoy
    ) {

        String sql = """

                SELECT COUNT(
                    DISTINCT rc.reserva_id
                )
                FROM tb_reserva_cancha rc
                INNER JOIN tb_reserva r
                    ON r.id_reserva = rc.reserva_id
                WHERE rc.fecha_turno = ?

                """;


        Long resultado =
                jdbcTemplate.queryForObject(

                        sql,

                        Long.class,

                        Date.valueOf(
                                hoy
                        )

                );


        return resultado != null
                ? resultado
                : 0L;
    }


    private long obtenerConfirmadasHoy(
            LocalDate hoy
    ) {

        String sql = """

                SELECT COUNT(
                    DISTINCT rc.reserva_id
                )
                FROM tb_reserva_cancha rc
                INNER JOIN tb_reserva r
                    ON r.id_reserva = rc.reserva_id
                WHERE rc.fecha_turno = ?
                  AND r.estado = 'CONFIRMADA'

                """;


        Long resultado =
                jdbcTemplate.queryForObject(

                        sql,

                        Long.class,

                        Date.valueOf(
                                hoy
                        )

                );


        return resultado != null
                ? resultado
                : 0L;
    }


    private long obtenerPagosPendientes() {

        String sql = """

                SELECT COUNT(*)
                FROM tb_pago
                WHERE estado = 'PENDIENTE_VALIDACION'

                """;


        Long resultado =
                jdbcTemplate.queryForObject(

                        sql,

                        Long.class

                );


        return resultado != null
                ? resultado
                : 0L;
    }


    private BigDecimal obtenerIngresosHoy(
            LocalDate hoy
    ) {

        String sql = """

                SELECT COALESCE(
                    SUM(monto),
                    0
                )
                FROM tb_pago
                WHERE estado = 'APROBADO'
                  AND DATE(
                      COALESCE(
                          fecha_validacion,
                          fecha_pago
                      )
                  ) = ?

                """;


        BigDecimal resultado =
                jdbcTemplate.queryForObject(

                        sql,

                        BigDecimal.class,

                        Date.valueOf(
                                hoy
                        )

                );


        return resultado != null
                ? resultado
                : BigDecimal.ZERO;
    }


    private List<DashboardProximaReservaDTO>
    obtenerProximasReservas(

            LocalDate hoy,

            LocalTime ahora

    ) {

        String sql = """

                SELECT
                    r.id_reserva,
                    c.nombre_completo,
                    s.nombre AS nombre_sede,
                    ca.nombre AS nombre_cancha,
                    rc.fecha_turno,
                    rc.hora_inicio,
                    rc.hora_fin,
                    r.total,
                    r.origen

                FROM tb_reserva r

                INNER JOIN tb_cliente c
                    ON c.id_cliente = r.cliente_id

                INNER JOIN tb_reserva_cancha rc
                    ON rc.reserva_id = r.id_reserva

                INNER JOIN tb_cancha ca
                    ON ca.id_cancha = rc.cancha_id

                INNER JOIN tb_sede s
                    ON s.id_sede = ca.sede_id

                WHERE r.estado = 'CONFIRMADA'

                  AND (
                        rc.fecha_turno > ?

                        OR (
                            rc.fecha_turno = ?
                            AND rc.hora_fin > ?
                        )
                  )

                ORDER BY
                    rc.fecha_turno ASC,
                    rc.hora_inicio ASC

                LIMIT 5

                """;


        return jdbcTemplate.query(

                sql,

                (
                        rs,
                        rowNum
                ) ->
                        new DashboardProximaReservaDTO(

                                rs.getLong(
                                        "id_reserva"
                                ),

                                rs.getString(
                                        "nombre_completo"
                                ),

                                rs.getString(
                                        "nombre_sede"
                                ),

                                rs.getString(
                                        "nombre_cancha"
                                ),

                                rs.getDate(
                                        "fecha_turno"
                                ).toLocalDate(),

                                rs.getTime(
                                        "hora_inicio"
                                ).toLocalTime(),

                                rs.getTime(
                                        "hora_fin"
                                ).toLocalTime(),

                                rs.getBigDecimal(
                                        "total"
                                ),

                                rs.getString(
                                        "origen"
                                )

                        ),

                Date.valueOf(
                        hoy
                ),

                Date.valueOf(
                        hoy
                ),

                Time.valueOf(
                        ahora
                )

        );
    }

}