package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.HorarioCanchaRequestDTO;
import com.deporplaza.reservas.dto.HorarioCanchaResponseDTO;
import com.deporplaza.reservas.entity.Cancha;
import com.deporplaza.reservas.entity.HorarioCancha;
import com.deporplaza.reservas.exception.BusinessRuleException;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.CanchaRepository;
import com.deporplaza.reservas.repository.HorarioCanchaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class HorarioCanchaService {

    private final HorarioCanchaRepository horarioRepository;
    private final CanchaRepository canchaRepository;

    public HorarioCanchaService(
            HorarioCanchaRepository horarioRepository,
            CanchaRepository canchaRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.canchaRepository = canchaRepository;
    }


    @Transactional(readOnly = true)
    public List<HorarioCanchaResponseDTO> listarTodos() {

        return horarioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public HorarioCanchaResponseDTO obtenerPorId(Integer id) {

        return convertirADTO(
                buscarEntidadPorId(id)
        );
    }


    @Transactional(readOnly = true)
    public List<HorarioCanchaResponseDTO> listarPorCancha(
            Integer idCancha
    ) {

        buscarCanchaPorId(idCancha);

        return horarioRepository
                .findByCanchaIdCancha(idCancha)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    @Transactional
    public HorarioCanchaResponseDTO crear(
            HorarioCanchaRequestDTO request
    ) {

        validarHoras(
                request.horaApertura(),
                request.horaCierre()
        );

        Cancha cancha =
                buscarCanchaPorId(request.idCancha());

        if (horarioRepository
                .existsByCanchaIdCanchaAndDiaSemana(
                        request.idCancha(),
                        request.diaSemana()
                )) {

            throw new ResourceConflictException(
                    "La cancha ya tiene un horario registrado para "
                            + request.diaSemana()
            );
        }

        HorarioCancha horario = new HorarioCancha();

        horario.setCancha(cancha);
        horario.setDiaSemana(request.diaSemana());
        horario.setHoraApertura(request.horaApertura());
        horario.setHoraCierre(request.horaCierre());
        horario.setEstado(request.estado());

        HorarioCancha guardado =
                horarioRepository.save(horario);

        return convertirADTO(guardado);
    }


    @Transactional
    public HorarioCanchaResponseDTO actualizar(
            Integer id,
            HorarioCanchaRequestDTO request
    ) {

        validarHoras(
                request.horaApertura(),
                request.horaCierre()
        );

        HorarioCancha horario =
                buscarEntidadPorId(id);

        Cancha cancha =
                buscarCanchaPorId(request.idCancha());

        if (horarioRepository
                .existsByCanchaIdCanchaAndDiaSemanaAndIdHorarioCanchaNot(
                        request.idCancha(),
                        request.diaSemana(),
                        id
                )) {

            throw new ResourceConflictException(
                    "La cancha ya tiene un horario registrado para "
                            + request.diaSemana()
            );
        }

        horario.setCancha(cancha);
        horario.setDiaSemana(request.diaSemana());
        horario.setHoraApertura(request.horaApertura());
        horario.setHoraCierre(request.horaCierre());
        horario.setEstado(request.estado());

        HorarioCancha actualizado =
                horarioRepository.save(horario);

        return convertirADTO(actualizado);
    }


    private HorarioCancha buscarEntidadPorId(Integer id) {

        return horarioRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe un horario con id " + id
                        )
                );
    }


    private Cancha buscarCanchaPorId(Integer idCancha) {

        return canchaRepository.findById(idCancha)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe una cancha con id " + idCancha
                        )
                );
    }


    private void validarHoras(
            LocalTime horaApertura,
            LocalTime horaCierre
    ) {

        if (!horaApertura.isBefore(horaCierre)) {

            throw new BusinessRuleException(
                    "La hora de apertura debe ser anterior a la hora de cierre"
            );
        }
    }


    private HorarioCanchaResponseDTO convertirADTO(
            HorarioCancha horario
    ) {

        return new HorarioCanchaResponseDTO(
                horario.getIdHorarioCancha(),
                horario.getCancha().getIdCancha(),
                horario.getCancha().getNombre(),
                horario.getDiaSemana(),
                horario.getHoraApertura(),
                horario.getHoraCierre(),
                horario.getEstado()
        );
    }
}