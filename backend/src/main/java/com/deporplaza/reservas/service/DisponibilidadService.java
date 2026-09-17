package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.BloqueDisponibilidadDTO;
import com.deporplaza.reservas.dto.CanchaDisponibilidadDTO;
import com.deporplaza.reservas.dto.DisponibilidadResponseDTO;

import com.deporplaza.reservas.entity.Cancha;
import com.deporplaza.reservas.entity.HorarioCancha;
import com.deporplaza.reservas.entity.ReservaCancha;
import com.deporplaza.reservas.entity.Sede;

import com.deporplaza.reservas.enums.*;

import com.deporplaza.reservas.exception.BusinessRuleException;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;

import com.deporplaza.reservas.repository.CanchaRepository;
import com.deporplaza.reservas.repository.HorarioCanchaRepository;
import com.deporplaza.reservas.repository.ReservaCanchaRepository;
import com.deporplaza.reservas.repository.SedeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DisponibilidadService {

    private final SedeRepository sedeRepository;
    private final CanchaRepository canchaRepository;
    private final HorarioCanchaRepository horarioRepository;
    private final ReservaCanchaRepository reservaCanchaRepository;


    public DisponibilidadService(
            SedeRepository sedeRepository,
            CanchaRepository canchaRepository,
            HorarioCanchaRepository horarioRepository,
            ReservaCanchaRepository reservaCanchaRepository
    ) {
        this.sedeRepository = sedeRepository;
        this.canchaRepository = canchaRepository;
        this.horarioRepository = horarioRepository;
        this.reservaCanchaRepository = reservaCanchaRepository;
    }


    @Transactional(readOnly = true)
    public DisponibilidadResponseDTO consultar(
            Integer idSede,
            LocalDate fecha
    ) {

        LocalDateTime ahora =
                LocalDateTime.now();


        // =====================================================
        // VALIDAR FECHA
        // =====================================================

        validarFecha(
                fecha,
                ahora.toLocalDate()
        );


        // =====================================================
        // VALIDAR SEDE
        // =====================================================

        Sede sede =
                sedeRepository
                        .findById(idSede)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe una sede con id "
                                                + idSede
                                )
                        );


        if (sede.getEstado()
                != EstadoSede.ACTIVA) {

            throw new ResourceConflictException(
                    "La sede no está disponible"
            );
        }


        // =====================================================
        // CANCHAS ACTIVAS
        // =====================================================

        List<Cancha> canchas =
                canchaRepository
                        .findBySedeIdSedeAndEstado(
                                idSede,
                                EstadoCancha.ACTIVA
                        )
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        Cancha::getNombre
                                )
                        )
                        .toList();


        if (canchas.isEmpty()) {

            return new DisponibilidadResponseDTO(
                    sede.getIdSede(),
                    sede.getNombre(),
                    fecha,
                    convertirDiaSemana(fecha),
                    List.of()
            );
        }


        List<Integer> idsCancha =
                canchas.stream()
                        .map(Cancha::getIdCancha)
                        .toList();


        DiaSemana diaSemana =
                convertirDiaSemana(fecha);


        // =====================================================
        // HORARIOS ACTIVOS
        // =====================================================

        Map<Integer, HorarioCancha> horarios =
                horarioRepository
                        .findByCanchaIdCanchaInAndDiaSemanaAndEstado(
                                idsCancha,
                                diaSemana,
                                EstadoHorario.ACTIVO
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        horario ->
                                                horario.getCancha()
                                                        .getIdCancha(),

                                        Function.identity()
                                )
                        );


        // =====================================================
        // RESERVAS QUE BLOQUEAN
        // =====================================================

        List<EstadoReserva> estadosQueBloquean =
                List.of(
                        EstadoReserva.PENDIENTE_PAGO,
                        EstadoReserva.PENDIENTE_CONFIRMACION,
                        EstadoReserva.CONFIRMADA
                );


        List<ReservaCancha> reservas =
                reservaCanchaRepository
                        .buscarBloqueosPorFecha(
                                idsCancha,
                                fecha,
                                estadosQueBloquean
                        );


        Map<Integer, List<ReservaCancha>>
                reservasPorCancha =
                reservas.stream()
                        .collect(
                                Collectors.groupingBy(
                                        rc ->
                                                rc.getCancha()
                                                        .getIdCancha()
                                )
                        );


        // =====================================================
        // CREAR RESPUESTA
        // =====================================================

        List<CanchaDisponibilidadDTO>
                disponibilidadCanchas =
                canchas.stream()
                        .map(cancha ->
                                construirDisponibilidadCancha(
                                        cancha,
                                        horarios.get(
                                                cancha.getIdCancha()
                                        ),
                                        reservasPorCancha
                                                .getOrDefault(
                                                        cancha.getIdCancha(),
                                                        List.of()
                                                ),
                                        fecha,
                                        ahora
                                )
                        )
                        .toList();


        return new DisponibilidadResponseDTO(
                sede.getIdSede(),
                sede.getNombre(),
                fecha,
                diaSemana,
                disponibilidadCanchas
        );
    }


    // =========================================================
    // DISPONIBILIDAD DE UNA CANCHA
    // =========================================================

    private CanchaDisponibilidadDTO
    construirDisponibilidadCancha(
            Cancha cancha,
            HorarioCancha horario,
            List<ReservaCancha> reservas,
            LocalDate fecha,
            LocalDateTime ahora
    ) {

        /*
         * La cancha existe y está ACTIVA,
         * pero ese día no tiene horario activo.
         */
        if (horario == null) {

            return new CanchaDisponibilidadDTO(
                    cancha.getIdCancha(),
                    cancha.getNombre(),
                    cancha.getSuperficie(),
                    cancha.getPrecioHora(),

                    false,

                    null,
                    null,

                    List.of()
            );
        }


        List<BloqueDisponibilidadDTO> bloques =
                generarBloques(
                        horario,
                        reservas,
                        fecha,
                        ahora
                );


        return new CanchaDisponibilidadDTO(
                cancha.getIdCancha(),
                cancha.getNombre(),
                cancha.getSuperficie(),
                cancha.getPrecioHora(),

                true,

                horario.getHoraApertura(),
                horario.getHoraCierre(),

                bloques
        );
    }


    // =========================================================
    // GENERAR HORAS DE INICIO
    // =========================================================

    private List<BloqueDisponibilidadDTO>
    generarBloques(
            HorarioCancha horario,
            List<ReservaCancha> reservas,
            LocalDate fecha,
            LocalDateTime ahora
    ) {

        List<BloqueDisponibilidadDTO> bloques =
                new ArrayList<>();


        LocalTime inicio =
                horario.getHoraApertura();


        LocalTime cierre =
                horario.getHoraCierre();


        /*
         * Una reserva necesita como mínimo 1 hora.
         *
         * Generamos inicios cada 30 minutos:
         *
         * 08:00
         * 08:30
         * 09:00
         * 09:30
         * ...
         */

        while (!inicio
                .plusHours(1)
                .isAfter(cierre)) {


            BloqueDisponibilidadDTO bloque =
                    evaluarBloque(
                            inicio,
                            cierre,
                            reservas,
                            fecha,
                            ahora
                    );


            bloques.add(bloque);


            inicio =
                    inicio.plusMinutes(30);
        }


        return bloques;
    }


    // =========================================================
    // EVALUAR UN INICIO
    // =========================================================

    private BloqueDisponibilidadDTO
    evaluarBloque(
            LocalTime horaInicio,
            LocalTime horaCierre,
            List<ReservaCancha> reservas,
            LocalDate fecha,
            LocalDateTime ahora
    ) {

        /*
         * Si estamos consultando hoy, no permitimos
         * horarios que ya pasaron.
         */
        if (fecha.equals(ahora.toLocalDate())
                && !horaInicio.isAfter(
                        ahora.toLocalTime()
                )) {

            return new BloqueDisponibilidadDTO(
                    horaInicio,
                    false,
                    null,
                    null
            );
        }


        int maxExtras =
                -1;


        /*
         * Probamos:
         *
         * extras 0 → 1 hora
         * extras 1 → 1h30
         * ...
         * extras 4 → 3 horas
         */

        for (int extras = 0;
             extras <= 4;
             extras++) {


            int duracionMinutos =
                    60 + (extras * 30);


            LocalTime horaFin =
                    horaInicio.plusMinutes(
                            duracionMinutos
                    );


            /*
             * Evita cruzar medianoche y exceder
             * el horario de cierre.
             */
            if (!horaFin.isAfter(horaInicio)
                    || horaFin.isAfter(
                    horaCierre
            )) {

                break;
            }


            if (haySolapamiento(
                    horaInicio,
                    horaFin,
                    reservas
            )) {

                break;
            }


            maxExtras =
                    extras;
        }


        if (maxExtras < 0) {

            return new BloqueDisponibilidadDTO(
                    horaInicio,
                    false,
                    null,
                    null
            );
        }


        int duracionMaxima =
                60 + (maxExtras * 30);


        return new BloqueDisponibilidadDTO(
                horaInicio,
                true,
                maxExtras,
                duracionMaxima
        );
    }


    // =========================================================
    // SOLAPAMIENTO
    // =========================================================

    private boolean haySolapamiento(
            LocalTime nuevoInicio,
            LocalTime nuevoFin,
            List<ReservaCancha> reservas
    ) {

        return reservas.stream()
                .anyMatch(reserva ->

                        nuevoInicio.isBefore(
                                reserva.getHoraFin()
                        )

                                &&

                        nuevoFin.isAfter(
                                reserva.getHoraInicio()
                        )
                );
    }


    // =========================================================
    // VALIDAR FECHA
    // =========================================================

    private void validarFecha(
            LocalDate fecha,
            LocalDate hoy
    ) {

        if (fecha == null) {

            throw new BusinessRuleException(
                    "La fecha es obligatoria"
            );
        }


        if (fecha.isBefore(hoy)) {

            throw new BusinessRuleException(
                    "No se puede consultar disponibilidad para una fecha pasada"
            );
        }


        if (fecha.isAfter(
                hoy.plusMonths(1)
        )) {

            throw new BusinessRuleException(
                    "Solo se puede consultar hasta un mes de anticipación"
            );
        }
    }


    // =========================================================
    // CONVERTIR DÍA
    // =========================================================

    private DiaSemana convertirDiaSemana(
            LocalDate fecha
    ) {

        return switch (
                fecha.getDayOfWeek()
        ) {

            case MONDAY ->
                    DiaSemana.LUNES;

            case TUESDAY ->
                    DiaSemana.MARTES;

            case WEDNESDAY ->
                    DiaSemana.MIERCOLES;

            case THURSDAY ->
                    DiaSemana.JUEVES;

            case FRIDAY ->
                    DiaSemana.VIERNES;

            case SATURDAY ->
                    DiaSemana.SABADO;

            case SUNDAY ->
                    DiaSemana.DOMINGO;
        };
    }
}