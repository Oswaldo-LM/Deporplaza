package com.deporplaza.reservas.service;

import com.deporplaza.reservas.entity.Reserva;
import com.deporplaza.reservas.entity.ReservaCancha;

import com.deporplaza.reservas.enums.EstadoReserva;

import com.deporplaza.reservas.repository.ReservaCanchaRepository;
import com.deporplaza.reservas.repository.ReservaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinalizacionReservaService {

    private final ReservaCanchaRepository
            reservaCanchaRepository;

    private final ReservaRepository
            reservaRepository;


    public FinalizacionReservaService(
            ReservaCanchaRepository reservaCanchaRepository,
            ReservaRepository reservaRepository
    ) {
        this.reservaCanchaRepository =
                reservaCanchaRepository;

        this.reservaRepository =
                reservaRepository;
    }


    @Transactional
    public int completarFinalizadas() {

        LocalDateTime ahora =
                LocalDateTime.now();


        List<ReservaCancha> finalizadas =
                reservaCanchaRepository
                        .buscarReservasFinalizadas(
                                EstadoReserva.CONFIRMADA,
                                ahora.toLocalDate(),
                                ahora.toLocalTime()
                        );


        int cantidad = 0;


        for (ReservaCancha detalle : finalizadas) {

            Integer idReserva =
                    detalle.getReserva()
                            .getIdReserva();


            Reserva reserva =
                    reservaRepository
                            .findByIdForUpdate(idReserva)
                            .orElse(null);


            if (reserva != null
                    && reserva.getEstado()
                    == EstadoReserva.CONFIRMADA) {

                reserva.setEstado(
                        EstadoReserva.COMPLETADA
                );

                cantidad++;
            }
        }


        return cantidad;
    }
}