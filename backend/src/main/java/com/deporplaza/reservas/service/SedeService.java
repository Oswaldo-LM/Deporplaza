package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.SedeRequestDTO;
import com.deporplaza.reservas.dto.SedeResponseDTO;
import com.deporplaza.reservas.entity.Sede;
import com.deporplaza.reservas.enums.EstadoSede;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.SedeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SedeService {

    private final SedeRepository sedeRepository;

    public SedeService(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }


    @Transactional(readOnly = true)
    public List<SedeResponseDTO> listarTodas() {

        return sedeRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public SedeResponseDTO obtenerPorId(Integer id) {

        Sede sede = buscarEntidadPorId(id);

        return convertirADTO(sede);
    }


    @Transactional
    public SedeResponseDTO crear(SedeRequestDTO request) {

        Sede sede = new Sede();

        sede.setNombre(request.nombre());
        sede.setDireccion(request.direccion());
        sede.setTelefono(request.telefono());
        sede.setEstado(request.estado());

        Sede sedeGuardada = sedeRepository.save(sede);

        return convertirADTO(sedeGuardada);
    }


    @Transactional
    public SedeResponseDTO actualizar(
            Integer id,
            SedeRequestDTO request
    ) {

        Sede sede = buscarEntidadPorId(id);

        sede.setNombre(request.nombre());
        sede.setDireccion(request.direccion());
        sede.setTelefono(request.telefono());
        sede.setEstado(request.estado());

        Sede sedeActualizada = sedeRepository.save(sede);

        return convertirADTO(sedeActualizada);
    }


    private Sede buscarEntidadPorId(Integer id) {

        return sedeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe una sede con id " + id
                        )
                );
    }


    private SedeResponseDTO convertirADTO(Sede sede) {

        return new SedeResponseDTO(
                sede.getIdSede(),
                sede.getNombre(),
                sede.getDireccion(),
                sede.getTelefono(),
                sede.getEstado()
        );
    }

    @Transactional(readOnly = true)
    public List<SedeResponseDTO> listarActivas() {

    return sedeRepository
            .findByEstado(
                    EstadoSede.ACTIVA
            )
            .stream()
            .map(this::convertirADTO)
            .toList();
    }

    
}