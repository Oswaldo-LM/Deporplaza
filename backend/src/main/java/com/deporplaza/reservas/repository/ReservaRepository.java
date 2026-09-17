package com.deporplaza.reservas.repository;

import com.deporplaza.reservas.entity.Reserva;
import com.deporplaza.reservas.enums.EstadoReserva;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;


public interface ReservaRepository
        extends JpaRepository<Reserva, Integer> {


    /*
     * Buscar reservas de un cliente.
     *
     * Lo mantenemos porque puede estar siendo
     * utilizado actualmente en otros servicios.
     */
    List<Reserva> findByClienteIdCliente(
            Integer idCliente
    );


    /*
     * MIS RESERVAS
     *
     * Devuelve las reservas del cliente
     * comenzando por la más reciente.
     */
    List<Reserva>
    findByClienteIdClienteOrderByFechaRegistroDesc(
            Integer idCliente
    );


    List<Reserva> findByEstado(
            EstadoReserva estado
    );


    @Lock(
            LockModeType.PESSIMISTIC_WRITE
    )
    @Query("""
            SELECT r
            FROM Reserva r
            WHERE r.idReserva = :idReserva
            """)
    Optional<Reserva> findByIdForUpdate(
            @Param("idReserva")
            Integer idReserva
    );


    @Lock(
            LockModeType.PESSIMISTIC_WRITE
    )
    @Query("""
            SELECT r
            FROM Reserva r
            WHERE r.estado = :estado
              AND r.fechaExpiracion IS NOT NULL
              AND r.fechaExpiracion <= :ahora
            """)
    List<Reserva> findVencidasForUpdate(
            @Param("estado")
            EstadoReserva estado,

            @Param("ahora")
            LocalDateTime ahora
    );

}