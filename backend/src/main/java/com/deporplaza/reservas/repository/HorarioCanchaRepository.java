package com.deporplaza.reservas.repository;

import com.deporplaza.reservas.entity.HorarioCancha;
import com.deporplaza.reservas.enums.DiaSemana;
import com.deporplaza.reservas.enums.EstadoHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HorarioCanchaRepository
        extends JpaRepository<HorarioCancha, Integer> {

    List<HorarioCancha> findByCanchaIdCancha(
            Integer idCancha
    );

    List<HorarioCancha> findByCanchaIdCanchaAndEstado(
            Integer idCancha,
            EstadoHorario estado
    );

    boolean existsByCanchaIdCanchaAndDiaSemana(
            Integer idCancha,
            DiaSemana diaSemana
    );

    boolean existsByCanchaIdCanchaAndDiaSemanaAndIdHorarioCanchaNot(
            Integer idCancha,
            DiaSemana diaSemana,
            Integer idHorarioCancha
    );

    Optional<HorarioCancha>
    findByCanchaIdCanchaAndDiaSemanaAndEstado(
            Integer idCancha,
            DiaSemana diaSemana,
            EstadoHorario estado
    );

    List<HorarioCancha>
    findByCanchaIdCanchaInAndDiaSemanaAndEstado(
        Collection<Integer> idsCancha,
        DiaSemana diaSemana,
        EstadoHorario estado
);
}