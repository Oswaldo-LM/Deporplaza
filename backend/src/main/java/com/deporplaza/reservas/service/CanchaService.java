package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.CanchaRequestDTO;
import com.deporplaza.reservas.dto.CanchaResponseDTO;
import com.deporplaza.reservas.entity.Cancha;
import com.deporplaza.reservas.entity.Sede;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.CanchaRepository;
import com.deporplaza.reservas.repository.SedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class CanchaService {

    private final CanchaRepository canchaRepository;
    private final SedeRepository sedeRepository;

    public CanchaService(
            CanchaRepository canchaRepository,
            SedeRepository sedeRepository
    ) {
        this.canchaRepository = canchaRepository;
        this.sedeRepository = sedeRepository;
    }


    @Transactional(readOnly = true)
    public List<CanchaResponseDTO> listarTodas() {

        return canchaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public CanchaResponseDTO obtenerPorId(Integer id) {

        Cancha cancha = buscarEntidadPorId(id);

        return convertirADTO(cancha);
    }


    @Transactional(readOnly = true)
    public List<CanchaResponseDTO> listarPorSede(Integer idSede) {

        buscarSedePorId(idSede);

        return canchaRepository
                .findBySedeIdSede(idSede)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    @Transactional
    public CanchaResponseDTO crear(
            CanchaRequestDTO request
    ) {

        Sede sede = buscarSedePorId(request.idSede());

        if (canchaRepository
        .existsBySedeIdSedeAndNombreIgnoreCase(
                request.idSede(),
                request.nombre()
        )) {

        throw new ResourceConflictException(
            "Ya existe una cancha con ese nombre en la sede"
        );
}

        Cancha cancha = new Cancha();

        cancha.setSede(sede);
        cancha.setNombre(request.nombre());
        cancha.setSuperficie(request.superficie());
        cancha.setPrecioHora(request.precioHora());
        cancha.setEstado(request.estado());

        Cancha canchaGuardada =
                canchaRepository.save(cancha);

        return convertirADTO(canchaGuardada);
    }


    @Transactional
    public CanchaResponseDTO actualizar(
            Integer id,
            CanchaRequestDTO request
    ) {

        Cancha cancha = buscarEntidadPorId(id);
        Sede sede = buscarSedePorId(request.idSede());

        cancha.setSede(sede);
        cancha.setNombre(request.nombre());
        cancha.setSuperficie(request.superficie());
        cancha.setPrecioHora(request.precioHora());
        cancha.setEstado(request.estado());

        Cancha canchaActualizada =
                canchaRepository.save(cancha);

        return convertirADTO(canchaActualizada);
    }


    private Cancha buscarEntidadPorId(Integer id) {

        return canchaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe una cancha con id " + id
                        )
                );
    }


    private Sede buscarSedePorId(Integer idSede) {

        return sedeRepository.findById(idSede)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe una sede con id " + idSede
                        )
                );
    }


    private CanchaResponseDTO convertirADTO(
            Cancha cancha
    ) {

        return new CanchaResponseDTO(
                cancha.getIdCancha(),
                cancha.getSede().getIdSede(),
                cancha.getSede().getNombre(),
                cancha.getNombre(),
                cancha.getSuperficie(),
                cancha.getPrecioHora(),
                cancha.getEstado()
        );
    }
}