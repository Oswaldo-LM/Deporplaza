package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.ComprobanteArchivoDTO;
import com.deporplaza.reservas.dto.PagoAdminResponseDTO;
import com.deporplaza.reservas.entity.Pago;
import com.deporplaza.reservas.entity.Reserva;
import com.deporplaza.reservas.entity.Usuario;
import com.deporplaza.reservas.enums.EstadoPago;
import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.EstadoUsuario;
import com.deporplaza.reservas.enums.RolUsuario;
import com.deporplaza.reservas.exception.BusinessRuleException;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.PagoRepository;
import com.deporplaza.reservas.repository.ReservaRepository;
import com.deporplaza.reservas.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoAdminService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComprobanteStorageService storageService;


    public PagoAdminService(
            PagoRepository pagoRepository,
            ReservaRepository reservaRepository,
            UsuarioRepository usuarioRepository,
            ComprobanteStorageService storageService
    ) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
    }


    @Transactional(readOnly = true)
    public List<PagoAdminResponseDTO> listarPendientes() {

        return pagoRepository
                .buscarPorEstadoConDetalle(
                        EstadoPago.PENDIENTE_VALIDACION
                )
                .stream()
                .map(this::convertirADTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public ComprobanteArchivoDTO obtenerComprobante(
            Integer idPago
    ) {

        Pago pago =
                pagoRepository.findById(idPago)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe un pago con id "
                                                + idPago
                                )
                        );


        String ruta =
                pago.getComprobanteUrl();


        return new ComprobanteArchivoDTO(
                storageService.cargar(ruta),
                storageService.obtenerContentType(ruta),
                storageService.obtenerNombreArchivo(ruta)
        );
    }


    @Transactional
    public PagoAdminResponseDTO aprobar(
            Integer idPago,
            Integer idUsuarioValidador
    ) {

        Pago pago =
                buscarPagoParaValidacion(idPago);

        Reserva reserva =
                bloquearReserva(pago);

        Usuario validador =
                buscarAdministrador(
                        idUsuarioValidador
                );


        validarEstados(
                pago,
                reserva
        );


        LocalDateTime ahora =
                LocalDateTime.now();


        pago.setEstado(
                EstadoPago.APROBADO
        );

        pago.setFechaValidacion(ahora);
        pago.setUsuarioValidador(validador);


        reserva.setEstado(
                EstadoReserva.CONFIRMADA
        );


        pagoRepository.save(pago);
        reservaRepository.save(reserva);


        return convertirADTO(pago);
    }


    @Transactional
    public PagoAdminResponseDTO rechazar(
            Integer idPago,
            Integer idUsuarioValidador
    ) {

        Pago pago =
                buscarPagoParaValidacion(idPago);

        Reserva reserva =
                bloquearReserva(pago);

        Usuario validador =
                buscarAdministrador(
                        idUsuarioValidador
                );


        validarEstados(
                pago,
                reserva
        );


        LocalDateTime ahora =
                LocalDateTime.now();


        pago.setEstado(
                EstadoPago.RECHAZADO
        );

        pago.setFechaValidacion(ahora);
        pago.setUsuarioValidador(validador);


        reserva.setEstado(
                EstadoReserva.CANCELADA
        );


        pagoRepository.save(pago);
        reservaRepository.save(reserva);


        return convertirADTO(pago);
    }


    private Pago buscarPagoParaValidacion(
            Integer idPago
    ) {

        return pagoRepository
                .findByIdForUpdate(idPago)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe un pago con id "
                                        + idPago
                        )
                );
    }


    private Reserva bloquearReserva(
            Pago pago
    ) {

        Integer idReserva =
                pago.getReserva()
                        .getIdReserva();


        return reservaRepository
                .findByIdForUpdate(idReserva)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe la reserva asociada al pago"
                        )
                );
    }


    private Usuario buscarAdministrador(
            Integer idUsuario
    ) {

        Usuario usuario =
                usuarioRepository.findById(idUsuario)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe un usuario con id "
                                                + idUsuario
                                )
                        );


        if (usuario.getEstado()
                != EstadoUsuario.ACTIVO) {

            throw new BusinessRuleException(
                    "El usuario validador no está activo"
            );
        }


        if (usuario.getRol()
                != RolUsuario.ADMIN) {

            throw new BusinessRuleException(
                    "Solo un administrador puede validar pagos"
            );
        }


        return usuario;
    }


    private void validarEstados(
            Pago pago,
            Reserva reserva
    ) {

        if (pago.getEstado()
                != EstadoPago.PENDIENTE_VALIDACION) {

            throw new ResourceConflictException(
                    "El pago ya fue validado anteriormente"
            );
        }


        if (reserva.getEstado()
                != EstadoReserva.PENDIENTE_CONFIRMACION) {

            throw new ResourceConflictException(
                    "La reserva no está pendiente de confirmación"
            );
        }
    }


    private PagoAdminResponseDTO convertirADTO(
            Pago pago
    ) {

        Reserva reserva =
                pago.getReserva();


        Usuario validador =
                pago.getUsuarioValidador();


        return new PagoAdminResponseDTO(
                pago.getIdPago(),
                reserva.getIdReserva(),

                reserva.getCliente().getIdCliente(),
                reserva.getCliente().getNombreCompleto(),

                pago.getMetodoPago(),
                pago.getMonto(),
                pago.getNumOperacion(),

                pago.getEstado(),
                reserva.getEstado(),

                pago.getFechaPago(),
                pago.getFechaValidacion(),

                validador != null
                        ? validador.getIdUsuario()
                        : null,

                validador != null
                        ? validador.getNombre()
                        : null,

                "/api/admin/pagos/"
                        + pago.getIdPago()
                        + "/comprobante"
        );
    }
}