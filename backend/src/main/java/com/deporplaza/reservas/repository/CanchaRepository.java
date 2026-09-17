package com.deporplaza.reservas.repository;



import com.deporplaza.reservas.entity.Cancha;
import com.deporplaza.reservas.enums.EstadoCancha;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface CanchaRepository
        extends JpaRepository<Cancha, Integer> {

    List<Cancha> findBySedeIdSede(Integer idSede);

    List<Cancha> findBySedeIdSedeAndEstado(
            Integer idSede,
            EstadoCancha estado
    );

    boolean existsBySedeIdSedeAndNombreIgnoreCase(
        Integer idSede,
        String nombre
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT c
        FROM Cancha c
        WHERE c.idCancha = :idCancha
        """)
     Optional<Cancha> findByIdForUpdate(
        @Param("idCancha") Integer idCancha
     );
}