package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.DashboardResumenDTO;

import com.deporplaza.reservas.service.DashboardService;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService
            dashboardService;


    public DashboardController(
            DashboardService dashboardService
    ) {

        this.dashboardService =
                dashboardService;
    }


    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO>
    obtenerResumen() {

        return ResponseEntity.ok(
                dashboardService
                        .obtenerResumen()
        );
    }

}