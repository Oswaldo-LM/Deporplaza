package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.PagoComprobanteRequestDTO;
import com.deporplaza.reservas.dto.PagoResponseDTO;
import com.deporplaza.reservas.entity.Pago;
import com.deporplaza.reservas.entity.Reserva;
import com.deporplaza.reservas.enums.EstadoPago;
import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.MetodoPago;
import com.deporplaza.reservas.enums.OrigenReserva;
import com.deporplaza.reservas.exception.BusinessRuleException;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.PagoRepository;
import com.deporplaza.reservas.repository.ReservaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class PagoService {

    private static final Set<MetodoPago>
            METODOS_WEB_PERMITIDOS =
            Set.of(
                    MetodoPago.YAPE,
                    MetodoPago.PLIN,
                    MetodoPago.TRANSFERENCIA
            );


    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final ComprobanteStorageService storageService;


    public PagoService(
            ReservaRepository reservaRepository,
            PagoRepository pagoRepository,
            ComprobanteStorageService storageService
    ) {
        this.reservaRepository = reservaRepository;
        this.pagoRepository = pagoRepository;
        this.storageService = storageService;
    }


    @Transactional
    public PagoResponseDTO registrarComprobante(
            Integer idReserva,
            PagoComprobanteRequestDTO request
    ) {

        LocalDateTime ahora =
                LocalDateTime.now();


        // =====================================================
        // BLOQUEAR RESERVA
        // =====================================================

        Reserva reserva =
                reservaRepository
                        .findByIdForUpdate(idReserva)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe una reserva con id "
                                                + idReserva
                                )
                        );


        // =====================================================
        // VALIDAR ORIGEN
        // =====================================================

        if (reserva.getOrigen()
                != OrigenReserva.WEB) {

            throw new BusinessRuleException(
                    "Este comprobante solo corresponde a reservas web"
            );
        }


        // =====================================================
        // VALIDAR ESTADO
        // =====================================================

        if (reserva.getEstado()
                != EstadoReserva.PENDIENTE_PAGO) {

            throw new ResourceConflictException(
                    "La reserva ya no está pendiente de pago"
            );
        }


        // =====================================================
        // VALIDAR EXPIRACIÓN
        // =====================================================

        if (reserva.getFechaExpiracion() == null
                || !ahora.isBefore(
                reserva.getFechaExpiracion()
        )) {

            throw new ResourceConflictException(
                    "El tiempo para registrar el pago ha expirado"
            );
        }


        // =====================================================
        // NO PERMITIR DOS PAGOS
        // =====================================================

        if (pagoRepository
                .existsByReservaIdReserva(idReserva)) {

            throw new ResourceConflictException(
                    "La reserva ya tiene un comprobante registrado"
            );
        }


        // =====================================================
        // MÉTODO DE PAGO WEB
        // =====================================================

        if (!METODOS_WEB_PERMITIDOS.contains(
                request.getMetodoPago()
        )) {

            throw new BusinessRuleException(
                    "Para reservas web solo se permite YAPE, PLIN o TRANSFERENCIA"
            );
        }


        // =====================================================
        // GUARDAR ARCHIVO
        // =====================================================

        String comprobanteUrl =
                storageService.guardar(
                        request.getComprobante()
                );


        // =====================================================
        // CREAR PAGO
        // =====================================================

        Pago pago = new Pago();

        pago.setReserva(reserva);

        pago.setMetodoPago(
                request.getMetodoPago()
        );

        /*
         * El monto NO viene desde Angular.
         */
        pago.setMonto(
                reserva.getTotal()
        );

        pago.setFechaPago(ahora);

        pago.setNumOperacion(
                normalizarNumOperacion(
                        request.getNumOperacion()
                )
        );

        pago.setComprobanteUrl(
                comprobanteUrl
        );

        pago.setEstado(
                EstadoPago.PENDIENTE_VALIDACION
        );

        pago.setFechaValidacion(null);
        pago.setUsuarioValidador(null);


        Pago pagoGuardado =
                pagoRepository.save(pago);


        // =====================================================
        // CAMBIAR RESERVA
        // =====================================================

        reserva.setEstado(
                EstadoReserva.PENDIENTE_CONFIRMACION
        );


        /*
         * No es estrictamente obligatorio llamar save()
         * porque reserva está administrada por Hibernate,
         * pero lo dejamos explícito.
         */
        reservaRepository.save(reserva);


        return convertirADTO(
                pagoGuardado,
                reserva
        );
    }


    private String normalizarNumOperacion(
            String numero
    ) {

        if (numero == null
                || numero.isBlank()) {

            return null;
        }

        return numero.trim();
    }


    private PagoResponseDTO convertirADTO(
            Pago pago,
            Reserva reserva
    ) {

        return new PagoResponseDTO(
                pago.getIdPago(),
                reserva.getIdReserva(),
                pago.getMetodoPago(),
                pago.getMonto(),
                pago.getNumOperacion(),
                pago.getEstado(),
                reserva.getEstado(),
                pago.getFechaPago()
        );
    }
}