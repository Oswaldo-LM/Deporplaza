package com.deporplaza.reservas.repository;

import com.deporplaza.reservas.entity.ReservaCancha;
import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.OrigenReserva;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface ReservaCanchaRepository
        extends JpaRepository<ReservaCancha, Integer> {

    List<ReservaCancha> findByReservaIdReserva(
            Integer idReserva
    );

    List<ReservaCancha> findByCanchaIdCancha(
            Integer idCancha
    );


    @Query("""
            SELECT COUNT(rc)
            FROM ReservaCancha rc
            WHERE rc.cancha.idCancha = :idCancha
              AND rc.fechaTurno = :fechaTurno
              AND rc.reserva.estado IN :estados
              AND :horaInicio < rc.horaFin
              AND :horaFin > rc.horaInicio
            """)
    long contarSolapamientos(
            @Param("idCancha") Integer idCancha,
            @Param("fechaTurno") LocalDate fechaTurno,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estados") Collection<EstadoReserva> estados
    );

    @Query("""
        SELECT rc
        FROM ReservaCancha rc
        JOIN FETCH rc.reserva r
        WHERE rc.cancha.idCancha IN :idsCancha
          AND rc.fechaTurno = :fechaTurno
          AND r.estado IN :estados
        ORDER BY rc.horaInicio
        """)
    List<ReservaCancha> buscarBloqueosPorFecha(
        @Param("idsCancha")
        Collection<Integer> idsCancha,

        @Param("fechaTurno")
        LocalDate fechaTurno,

        @Param("estados")
        Collection<EstadoReserva> estados
     );

     @Query("""
        SELECT rc
        FROM ReservaCancha rc
        JOIN FETCH rc.reserva r
        JOIN FETCH r.cliente cl
        JOIN FETCH rc.cancha c
        JOIN FETCH c.sede s
        WHERE (:estado IS NULL OR r.estado = :estado)
          AND (:origen IS NULL OR r.origen = :origen)
          AND (:fechaTurno IS NULL OR rc.fechaTurno = :fechaTurno)
          AND (:idSede IS NULL OR s.idSede = :idSede)
          AND (:idCancha IS NULL OR c.idCancha = :idCancha)
        ORDER BY rc.fechaTurno DESC, rc.horaInicio DESC
        """)
     List<ReservaCancha> buscarParaAdmin(
        @Param("estado") EstadoReserva estado,
        @Param("origen") OrigenReserva origen,
        @Param("fechaTurno") LocalDate fechaTurno,
        @Param("idSede") Integer idSede,
        @Param("idCancha") Integer idCancha
     );

     @Query("""
        SELECT rc
        FROM ReservaCancha rc
        JOIN FETCH rc.reserva r
        WHERE r.estado = :estado
          AND (
                rc.fechaTurno < :hoy
                OR (
                    rc.fechaTurno = :hoy
                    AND rc.horaFin <= :horaActual
                )
          )
        """)
     List<ReservaCancha> buscarReservasFinalizadas(
        @Param("estado")
        EstadoReserva estado,

        @Param("hoy")
        LocalDate hoy,

        @Param("horaActual")
        LocalTime horaActual
     );
 
@Query("""
        SELECT rc
        FROM ReservaCancha rc
        JOIN FETCH rc.reserva r
        JOIN FETCH r.cliente cl
        JOIN FETCH rc.cancha c
        JOIN FETCH c.sede s
        WHERE cl.idCliente = :idCliente
        ORDER BY rc.fechaTurno DESC,
                 rc.horaInicio DESC
        """)
List<ReservaCancha> buscarPorCliente(
        @Param("idCliente")
        Integer idCliente
);

// =========================================================
// REPORTE DE RESERVAS
// =========================================================

@Query("""
        SELECT rc
        FROM ReservaCancha rc
        JOIN FETCH rc.reserva r
        JOIN FETCH r.cliente cl
        JOIN FETCH rc.cancha c
        JOIN FETCH c.sede s
        WHERE (:desde IS NULL OR rc.fechaTurno >= :desde)
          AND (:hasta IS NULL OR rc.fechaTurno <= :hasta)
          AND (:idSede IS NULL OR s.idSede = :idSede)
          AND (:estado IS NULL OR r.estado = :estado)
        ORDER BY rc.fechaTurno DESC,
                 rc.horaInicio DESC
        """)
List<ReservaCancha> buscarReporteReservas(

        @Param("desde")
        LocalDate desde,

        @Param("hasta")
        LocalDate hasta,

        @Param("idSede")
        Integer idSede,

        @Param("estado")
        EstadoReserva estado

);

}