package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.ReporteReservaDTO;

import com.deporplaza.reservas.entity.Cancha;
import com.deporplaza.reservas.entity.Cliente;
import com.deporplaza.reservas.entity.Pago;
import com.deporplaza.reservas.entity.Reserva;
import com.deporplaza.reservas.entity.ReservaCancha;
import com.deporplaza.reservas.entity.Sede;

import com.deporplaza.reservas.enums.EstadoReserva;

import com.deporplaza.reservas.exception.BusinessRuleException;

import com.deporplaza.reservas.repository.PagoRepository;
import com.deporplaza.reservas.repository.ReservaCanchaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;


@Service
public class ReporteService {

    private final ReservaCanchaRepository
            reservaCanchaRepository;

    private final PagoRepository
            pagoRepository;


    private static final DateTimeFormatter
            FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );


    private static final DateTimeFormatter
            FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
            );


    private static final DateTimeFormatter
            FORMATO_HORA =
            DateTimeFormatter.ofPattern(
                    "HH:mm"
            );


    public ReporteService(

            ReservaCanchaRepository
                    reservaCanchaRepository,

            PagoRepository
                    pagoRepository

    ) {

        this.reservaCanchaRepository =
                reservaCanchaRepository;

        this.pagoRepository =
                pagoRepository;
    }


    // =========================================================
    // CONSULTAR REPORTE
    // =========================================================

    @Transactional(readOnly = true)
    public List<ReporteReservaDTO>
    obtenerReporteReservas(

            LocalDate desde,

            LocalDate hasta,

            Integer idSede,

            EstadoReserva estado

    ) {

        validarFechas(
                desde,
                hasta
        );


        List<ReservaCancha> detalles =
                reservaCanchaRepository
                        .buscarReporteReservas(

                                desde,

                                hasta,

                                idSede,

                                estado
                        );


        return detalles
                .stream()
                .map(
                        this::convertirADTO
                )
                .toList();
    }


    // =========================================================
    // CONVERTIR A DTO
    // =========================================================

    private ReporteReservaDTO convertirADTO(
            ReservaCancha detalle
    ) {

        Reserva reserva =
                detalle.getReserva();


        Cliente cliente =
                reserva.getCliente();


        Cancha cancha =
                detalle.getCancha();


        Sede sede =
                cancha.getSede();


        Optional<Pago> pago =
                pagoRepository
                        .findByReservaIdReserva(
                                reserva.getIdReserva()
                        );


        String metodoPago =
                pago
                        .map(item ->
                                item.getMetodoPago()
                                        .name()
                        )
                        .orElse(
                                null
                        );


        String estadoPago =
                pago
                        .map(item ->
                                item.getEstado()
                                        .name()
                        )
                        .orElse(
                                null
                        );


        return new ReporteReservaDTO(

                reserva.getIdReserva(),

                reserva.getFechaRegistro(),

                detalle.getFechaTurno(),

                detalle.getHoraInicio(),

                detalle.getHoraFin(),

                cliente.getNombreCompleto(),

                cliente.getTipoDocumento()
                        .name(),

                cliente.getNumDocumento(),

                sede.getNombre(),

                cancha.getNombre(),

                cancha.getSuperficie()
                        .name(),

                reserva.getOrigen()
                        .name(),

                reserva.getEstado()
                        .name(),

                metodoPago,

                estadoPago,

                reserva.getTotal()
        );
    }


    // =========================================================
    // EXPORTAR CSV
    // =========================================================

    @Transactional(readOnly = true)
    public byte[] exportarReservasCsv(

            LocalDate desde,

            LocalDate hasta,

            Integer idSede,

            EstadoReserva estado

    ) {

        List<ReporteReservaDTO> datos =
                obtenerReporteReservas(

                        desde,

                        hasta,

                        idSede,

                        estado
                );


        StringBuilder csv =
                new StringBuilder();


        /*
         * BOM UTF-8.
         *
         * Ayuda a que Excel reconozca
         * correctamente tildes, ñ, etc.
         */
        csv.append(
                '\uFEFF'
        );


        csv.append(
                "ID Reserva;"
        );

        csv.append(
                "Fecha registro;"
        );

        csv.append(
                "Fecha turno;"
        );

        csv.append(
                "Hora inicio;"
        );

        csv.append(
                "Hora fin;"
        );

        csv.append(
                "Cliente;"
        );

        csv.append(
                "Tipo documento;"
        );

        csv.append(
                "Numero documento;"
        );

        csv.append(
                "Sede;"
        );

        csv.append(
                "Cancha;"
        );

        csv.append(
                "Superficie;"
        );

        csv.append(
                "Origen;"
        );

        csv.append(
                "Estado reserva;"
        );

        csv.append(
                "Metodo pago;"
        );

        csv.append(
                "Estado pago;"
        );

        csv.append(
                "Total"
        );

        csv.append(
                "\r\n"
        );


        for (
                ReporteReservaDTO item :
                datos
        ) {

            agregarCampo(
                    csv,
                    item.idReserva()
            );

            agregarCampo(
                    csv,
                    item.fechaRegistro()
                            == null
                            ? ""
                            : item.fechaRegistro()
                                    .format(
                                            FORMATO_FECHA_HORA
                                    )
            );

            agregarCampo(
                    csv,
                    item.fechaTurno()
                            == null
                            ? ""
                            : item.fechaTurno()
                                    .format(
                                            FORMATO_FECHA
                                    )
            );

            agregarCampo(
                    csv,
                    item.horaInicio()
                            == null
                            ? ""
                            : item.horaInicio()
                                    .format(
                                            FORMATO_HORA
                                    )
            );

            agregarCampo(
                    csv,
                    item.horaFin()
                            == null
                            ? ""
                            : item.horaFin()
                                    .format(
                                            FORMATO_HORA
                                    )
            );

            agregarCampo(
                    csv,
                    item.cliente()
            );

            agregarCampo(
                    csv,
                    item.tipoDocumento()
            );

            agregarCampo(
                    csv,
                    item.numDocumento()
            );

            agregarCampo(
                    csv,
                    item.sede()
            );

            agregarCampo(
                    csv,
                    item.cancha()
            );

            agregarCampo(
                    csv,
                    item.superficie()
            );

            agregarCampo(
                    csv,
                    item.origen()
            );

            agregarCampo(
                    csv,
                    item.estadoReserva()
            );

            agregarCampo(
                    csv,
                    item.metodoPago()
            );

            agregarCampo(
                    csv,
                    item.estadoPago()
            );


            /*
             * El último campo no necesita
             * punto y coma al final.
             */
            csv.append(
                    escaparCsv(
                            item.total() == null
                                    ? ""
                                    : item.total()
                                            .toPlainString()
                    )
            );


            csv.append(
                    "\r\n"
            );
        }


        return csv
                .toString()
                .getBytes(
                        StandardCharsets.UTF_8
                );
    }


    // =========================================================
    // CAMPO CSV
    // =========================================================

    private void agregarCampo(

            StringBuilder csv,

            Object valor

    ) {

        csv.append(
                escaparCsv(
                        valor == null
                                ? ""
                                : valor.toString()
                )
        );


        csv.append(
                ';'
        );
    }


    // =========================================================
    // ESCAPAR CSV
    // =========================================================

    private String escaparCsv(
            String valor
    ) {

        if (
                valor == null
        ) {

            return "";
        }


        String limpio =
                valor.replace(
                        "\"",
                        "\"\""
                );


        /*
         * Ponemos todos los valores entre
         * comillas.
         *
         * Esto evita problemas si nombres,
         * sedes o canchas contienen:
         *
         * ;
         * "
         * saltos de línea
         */
        return "\""
                + limpio
                + "\"";
    }


    // =========================================================
    // VALIDACIÓN FECHAS
    // =========================================================

    private void validarFechas(

            LocalDate desde,

            LocalDate hasta

    ) {

        if (
                desde != null
                &&
                hasta != null
                &&
                desde.isAfter(
                        hasta
                )
        ) {

            throw new BusinessRuleException(
                    "La fecha desde no puede ser posterior a la fecha hasta"
            );
        }
    }

}