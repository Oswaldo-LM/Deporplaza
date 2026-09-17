package com.deporplaza.reservas.repository;

import com.deporplaza.reservas.entity.Sede;
import com.deporplaza.reservas.enums.EstadoSede;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository
        extends JpaRepository<Sede, Integer> {

    List<Sede> findByEstado(
            EstadoSede estado
    );
}