package com.deporplaza.reservas.repository;

import com.deporplaza.reservas.entity.Pago;
import com.deporplaza.reservas.enums.EstadoPago;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PagoRepository
        extends JpaRepository<Pago, Integer> {
                


    Optional<Pago> findByReservaIdReserva(
            Integer idReserva
    );
    

    boolean existsByReservaIdReserva(
            Integer idReserva
    );

    List<Pago> findByEstado(
            EstadoPago estado
    );

    


    @Query("""
            SELECT p
            FROM Pago p
            JOIN FETCH p.reserva r
            JOIN FETCH r.cliente c
            WHERE p.estado = :estado
            ORDER BY p.fechaPago ASC
            """)
    List<Pago> buscarPorEstadoConDetalle(
            @Param("estado") EstadoPago estado
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Pago p
            WHERE p.idPago = :idPago
            """)
    Optional<Pago> findByIdForUpdate(
            @Param("idPago") Integer idPago
    );

    @Query("""
        SELECT p
        FROM Pago p
        LEFT JOIN FETCH p.usuarioValidador
        WHERE p.reserva.idReserva IN :idsReserva
        """)
    List<Pago> buscarPorReservas(
        @Param("idsReserva")
        Collection<Integer> idsReserva
    );
}