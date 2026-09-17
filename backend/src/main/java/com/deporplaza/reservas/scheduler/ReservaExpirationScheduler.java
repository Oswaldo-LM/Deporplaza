package com.deporplaza.reservas.scheduler;

import com.deporplaza.reservas.service.ExpiracionReservaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaExpirationScheduler {

    private final ExpiracionReservaService expiracionReservaService;

    public ReservaExpirationScheduler(
            ExpiracionReservaService expiracionReservaService
    ) {
        this.expiracionReservaService =
                expiracionReservaService;
    }


    @Scheduled(fixedDelay = 10000)
    public void revisarReservasExpiradas() {

        int cantidadExpiradas =
                expiracionReservaService
                        .expirarReservasVencidas();

        if (cantidadExpiradas > 0) {

            System.out.println(
                    "Reservas expiradas automáticamente: "
                            + cantidadExpiradas
            );
        }
    }
}