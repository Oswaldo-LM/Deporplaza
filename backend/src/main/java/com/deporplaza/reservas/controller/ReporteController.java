package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ReporteReservaDTO;

import com.deporplaza.reservas.enums.EstadoReserva;

import com.deporplaza.reservas.service.ReporteService;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import java.util.List;


@RestController
@RequestMapping(
        "/api/admin/reportes"
)
public class ReporteController {

    private final ReporteService
            reporteService;


    public ReporteController(
            ReporteService reporteService
    ) {

        this.reporteService =
                reporteService;
    }


    // =========================================================
    // CONSULTAR REPORTE
    // =========================================================

    @GetMapping(
            "/reservas"
    )
    public ResponseEntity<
            List<ReporteReservaDTO>
            >
    obtenerReporte(

            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso =
                    DateTimeFormat.ISO.DATE
            )
            LocalDate desde,


            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso =
                    DateTimeFormat.ISO.DATE
            )
            LocalDate hasta,


            @RequestParam(
                    required = false
            )
            Integer idSede,


            @RequestParam(
                    required = false
            )
            EstadoReserva estado

    ) {

        return ResponseEntity.ok(

                reporteService
                        .obtenerReporteReservas(

                                desde,

                                hasta,

                                idSede,

                                estado
                        )
        );
    }


    // =========================================================
    // EXPORTAR CSV
    // =========================================================

    @GetMapping(
            value =
                    "/reservas/exportar",
            produces =
                    "text/csv"
    )
    public ResponseEntity<byte[]>
    exportarReservas(

            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso =
                    DateTimeFormat.ISO.DATE
            )
            LocalDate desde,


            @RequestParam(
                    required = false
            )
            @DateTimeFormat(
                    iso =
                    DateTimeFormat.ISO.DATE
            )
            LocalDate hasta,


            @RequestParam(
                    required = false
            )
            Integer idSede,


            @RequestParam(
                    required = false
            )
            EstadoReserva estado

    ) {

        byte[] archivo =
                reporteService
                        .exportarReservasCsv(

                                desde,

                                hasta,

                                idSede,

                                estado
                        );


        String nombreArchivo =
                "reporte_reservas_"
                + LocalDate.now()
                + ".csv";


        return ResponseEntity
                .ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,

                        "attachment; filename=\""
                        + nombreArchivo
                        + "\""
                )

                .contentType(
                        MediaType.parseMediaType(
                                "text/csv; charset=UTF-8"
                        )
                )

                .body(
                        archivo
                );
    }

}