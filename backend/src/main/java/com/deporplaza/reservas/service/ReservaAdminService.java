package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.ReservaAdminResponseDTO;
import com.deporplaza.reservas.entity.*;
import com.deporplaza.reservas.enums.*;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReservaAdminService {

    private final ReservaRepository reservaRepository;
    private final ReservaCanchaRepository reservaCanchaRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;


    public ReservaAdminService(
            ReservaRepository reservaRepository,
            ReservaCanchaRepository reservaCanchaRepository,
            PagoRepository pagoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.reservaCanchaRepository = reservaCanchaRepository;
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Transactional(readOnly = true)
    public List<ReservaAdminResponseDTO> listar(
            EstadoReserva estado,
            OrigenReserva origen,
            LocalDate fecha,
            Integer sedeId,
            Integer canchaId
    ) {

        List<ReservaCancha> detalles =
                reservaCanchaRepository.buscarParaAdmin(
                        estado,
                        origen,
                        fecha,
                        sedeId,
                        canchaId
                );

        if (detalles.isEmpty()) {
            return List.of();
        }

        List<Integer> idsReserva =
                detalles.stream()
                        .map(rc ->
                                rc.getReserva().getIdReserva()
                        )
                        .distinct()
                        .toList();


        Map<Integer, Pago> pagos =
                pagoRepository
                        .buscarPorReservas(idsReserva)
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        p -> p.getReserva().getIdReserva(),
                                        Function.identity()
                                )
                        );


        return detalles.stream()
                .map(detalle ->
                        convertirADTO(
                                detalle,
                                pagos.get(
                                        detalle.getReserva()
                                                .getIdReserva()
                                )
                        )
                )
                .toList();
    }


    @Transactional(readOnly = true)
    public ReservaAdminResponseDTO obtenerPorId(
            Integer idReserva
    ) {

        ReservaCancha detalle =
                reservaCanchaRepository
                        .findByReservaIdReserva(idReserva)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe una reserva con id "
                                                + idReserva
                                )
                        );


        Pago pago =
                pagoRepository
                        .findByReservaIdReserva(idReserva)
                        .orElse(null);


        return convertirADTO(
                detalle,
                pago
        );
    }


    @Transactional
    public ReservaAdminResponseDTO cancelar(
            Integer idReserva,
            Integer idUsuarioAdmin
    ) {

        Reserva reserva =
                reservaRepository
                        .findByIdForUpdate(idReserva)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe una reserva con id "
                                                + idReserva
                                )
                        );


        if (reserva.getEstado()
                == EstadoReserva.CANCELADA
                || reserva.getEstado()
                == EstadoReserva.EXPIRADA
                || reserva.getEstado()
                == EstadoReserva.COMPLETADA) {

            throw new ResourceConflictException(
                    "La reserva ya no puede ser cancelada"
            );
        }


        Usuario admin =
                usuarioRepository
                        .findById(idUsuarioAdmin)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el usuario administrador"
                                )
                        );


        reserva.setEstado(
                EstadoReserva.CANCELADA
        );


        Pago pago =
                pagoRepository
                        .findByReservaIdReserva(idReserva)
                        .orElse(null);


        /*
         * Si estaba esperando validación, la cancelación
         * también cierra ese comprobante.
         *
         * Si ya estaba APROBADO lo dejamos APROBADO como
         * registro histórico. Reembolsos quedan fuera del
         * alcance actual.
         */
        if (pago != null
                && pago.getEstado()
                == EstadoPago.PENDIENTE_VALIDACION) {

            pago.setEstado(
                    EstadoPago.RECHAZADO
            );

            pago.setFechaValidacion(
                    LocalDateTime.now()
            );

            pago.setUsuarioValidador(
                    admin
            );
        }


        ReservaCancha detalle =
                reservaCanchaRepository
                        .findByReservaIdReserva(idReserva)
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "La reserva no tiene detalle de cancha"
                                )
                        );


        return convertirADTO(
                detalle,
                pago
        );
    }


    private ReservaAdminResponseDTO convertirADTO(
            ReservaCancha detalle,
            Pago pago
    ) {

        Reserva reserva =
                detalle.getReserva();

        Cliente cliente =
                reserva.getCliente();

        Cancha cancha =
                detalle.getCancha();

        Sede sede =
                cancha.getSede();


        return new ReservaAdminResponseDTO(
                reserva.getIdReserva(),
                reserva.getEstado(),
                reserva.getOrigen(),

                reserva.getFechaRegistro(),
                reserva.getFechaExpiracion(),
                reserva.getTotal(),

                cliente.getIdCliente(),
                cliente.getNombreCompleto(),
                cliente.getTipoDocumento(),
                cliente.getNumDocumento(),
                cliente.getEmail(),
                cliente.getTelefono(),

                sede.getIdSede(),
                sede.getNombre(),

                cancha.getIdCancha(),
                cancha.getNombre(),
                cancha.getSuperficie(),

                detalle.getFechaTurno(),
                detalle.getHoraInicio(),
                detalle.getHoraFin(),
                detalle.getCantidadExtras().intValue(),
                detalle.getPrecioHora(),
                detalle.getSubtotal(),

                pago != null
                        ? pago.getIdPago()
                        : null,

                pago != null
                        ? pago.getMetodoPago()
                        : null,

                pago != null
                        ? pago.getEstado()
                        : null,

                pago != null
                        ? pago.getFechaPago()
                        : null
        );
    }
}