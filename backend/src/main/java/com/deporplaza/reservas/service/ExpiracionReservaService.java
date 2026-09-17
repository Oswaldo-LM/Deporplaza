package com.deporplaza.reservas.service;

import com.deporplaza.reservas.entity.Reserva;
import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpiracionReservaService {

    private final ReservaRepository reservaRepository;

    public ExpiracionReservaService(
            ReservaRepository reservaRepository
    ) {
        this.reservaRepository = reservaRepository;
    }


    @Transactional
    public int expirarReservasVencidas() {

        LocalDateTime ahora = LocalDateTime.now();

        List<Reserva> reservasVencidas =
                reservaRepository.findVencidasForUpdate(
                        EstadoReserva.PENDIENTE_PAGO,
                        ahora
                );

        for (Reserva reserva : reservasVencidas) {
            reserva.setEstado(
                    EstadoReserva.EXPIRADA
            );
        }

        return reservasVencidas.size();
    }
}