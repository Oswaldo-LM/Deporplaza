package com.deporplaza.reservas.scheduler;

import com.deporplaza.reservas.service.FinalizacionReservaService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FinalizacionReservaScheduler {

    private final FinalizacionReservaService
            finalizacionReservaService;


    public FinalizacionReservaScheduler(
            FinalizacionReservaService finalizacionReservaService
    ) {
        this.finalizacionReservaService =
                finalizacionReservaService;
    }


    @Scheduled(fixedDelay = 60000)
    public void completarReservas() {

        int cantidad =
                finalizacionReservaService
                        .completarFinalizadas();

        if (cantidad > 0) {

            System.out.println(
                    "Reservas completadas automáticamente: "
                            + cantidad
            );
        }
    }
}