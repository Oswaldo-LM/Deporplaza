package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.ReservaPresencialRequestDTO;
import com.deporplaza.reservas.dto.ReservaResponseDTO;
import com.deporplaza.reservas.dto.ReservaWebRequestDTO;

import com.deporplaza.reservas.entity.*;

import com.deporplaza.reservas.enums.*;

import com.deporplaza.reservas.exception.BusinessRuleException;
import com.deporplaza.reservas.exception.ResourceConflictException;
import com.deporplaza.reservas.exception.ResourceNotFoundException;

import com.deporplaza.reservas.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;


@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    private final ReservaCanchaRepository reservaCanchaRepository;

    private final CanchaRepository canchaRepository;

    private final ClienteRepository clienteRepository;

    private final HorarioCanchaRepository horarioRepository;

    private final UsuarioRepository usuarioRepository;

    private final PagoRepository pagoRepository;


    public ReservaService(
            ReservaRepository reservaRepository,
            ReservaCanchaRepository reservaCanchaRepository,
            CanchaRepository canchaRepository,
            ClienteRepository clienteRepository,
            HorarioCanchaRepository horarioRepository,
            UsuarioRepository usuarioRepository,
            PagoRepository pagoRepository
    ) {

        this.reservaRepository =
                reservaRepository;

        this.reservaCanchaRepository =
                reservaCanchaRepository;

        this.canchaRepository =
                canchaRepository;

        this.clienteRepository =
                clienteRepository;

        this.horarioRepository =
                horarioRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.pagoRepository =
                pagoRepository;
    }


    // =========================================================
    // CREAR RESERVA WEB
    // =========================================================

    @Transactional
    public ReservaResponseDTO crearReservaWeb(
            ReservaWebRequestDTO request,
            Integer idUsuarioAutenticado
    ) {

        LocalDateTime ahora =
                LocalDateTime.now();


        validarFecha(
                request.fechaTurno(),
                request.horaInicio(),
                ahora
        );


        // =====================================================
        // BLOQUEAMOS LA CANCHA DURANTE LA TRANSACCIÓN
        // =====================================================

        Cancha cancha =
                canchaRepository
                        .findByIdForUpdate(
                                request.idCancha()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe una cancha con id "
                                                + request.idCancha()
                                )
                        );


        validarCanchaDisponible(
                cancha
        );


        // =====================================================
        // DURACIÓN
        // =====================================================

        int duracionMinutos =
                60
                + (
                    request.cantidadExtras()
                    * 30
                );


        LocalTime horaFin =
                request.horaInicio()
                        .plusMinutes(
                                duracionMinutos
                        );


        if (
                !horaFin.isAfter(
                        request.horaInicio()
                )
        ) {

            throw new BusinessRuleException(
                    "La reserva no puede extenderse al día siguiente"
            );
        }


        // =====================================================
        // HORARIO DE LA CANCHA
        // =====================================================

        DiaSemana diaSemana =
                convertirDiaSemana(
                        request.fechaTurno()
                );


        HorarioCancha horario =
                horarioRepository
                        .findByCanchaIdCanchaAndDiaSemanaAndEstado(
                                cancha.getIdCancha(),
                                diaSemana,
                                EstadoHorario.ACTIVO
                        )
                        .orElseThrow(() ->
                                new ResourceConflictException(
                                        "La cancha no tiene un horario activo para "
                                                + diaSemana
                                )
                        );


        validarDentroDelHorario(
                request.horaInicio(),
                horaFin,
                horario
        );


        // =====================================================
        // SOLAPAMIENTO
        // =====================================================

        List<EstadoReserva> estadosQueBloquean =
                List.of(
                        EstadoReserva.PENDIENTE_PAGO,
                        EstadoReserva.PENDIENTE_CONFIRMACION,
                        EstadoReserva.CONFIRMADA
                );


        long solapamientos =
                reservaCanchaRepository
                        .contarSolapamientos(
                                cancha.getIdCancha(),
                                request.fechaTurno(),
                                request.horaInicio(),
                                horaFin,
                                estadosQueBloquean
                        );


        if (
                solapamientos > 0
        ) {

            throw new ResourceConflictException(
                    "La cancha ya está reservada en el horario seleccionado"
            );
        }


        // =====================================================
        // CLIENTE
        // =====================================================

        Cliente cliente =
                resolverClienteReservaWeb(
                        request,
                        idUsuarioAutenticado
                );


        // =====================================================
        // PRECIO
        // =====================================================

        BigDecimal precioHora =
                cancha.getPrecioHora();


        BigDecimal subtotal =
                calcularSubtotal(
                        precioHora,
                        request.cantidadExtras()
                );


        // =====================================================
        // CREAR RESERVA
        // =====================================================

        Reserva reserva =
                new Reserva();


        reserva.setCliente(
                cliente
        );


        /*
         * Reserva hecha directamente
         * desde la web.
         */
        reserva.setUsuarioRegistro(
                null
        );


        reserva.setFechaRegistro(
                ahora
        );


        reserva.setFechaExpiracion(
                ahora.plusMinutes(
                        5
                )
        );


        reserva.setTotal(
                subtotal
        );


        reserva.setEstado(
                EstadoReserva.PENDIENTE_PAGO
        );


        reserva.setOrigen(
                OrigenReserva.WEB
        );


        Reserva reservaGuardada =
                reservaRepository.save(
                        reserva
                );


        // =====================================================
        // DETALLE DE CANCHA
        // =====================================================

        ReservaCancha detalle =
                new ReservaCancha();


        detalle.setReserva(
                reservaGuardada
        );


        detalle.setCancha(
                cancha
        );


        detalle.setFechaTurno(
                request.fechaTurno()
        );


        detalle.setHoraInicio(
                request.horaInicio()
        );


        detalle.setHoraFin(
                horaFin
        );


        detalle.setPrecioHora(
                precioHora
        );


        detalle.setSubtotal(
                subtotal
        );


        detalle.setCantidadExtras(
                request.cantidadExtras()
                        .byteValue()
        );


        reservaCanchaRepository.save(
                detalle
        );


        return convertirADTO(
                reservaGuardada,
                detalle,
                duracionMinutos
        );
    }


    // =========================================================
    // MIS RESERVAS DEL CLIENTE
    // =========================================================

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarMisReservas(
            Integer idUsuario
    ) {

        /*
         * Buscamos TB_CLIENTE utilizando
         * el usuario autenticado.
         *
         * TB_USUARIO.id_usuario
         *          ↓
         * TB_CLIENTE.id_usuario
         */
        Cliente cliente =
                clienteRepository
                        .findByUsuarioIdUsuario(
                                idUsuario
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe un cliente asociado al usuario autenticado"
                                )
                        );


        /*
         * Buscamos únicamente las reservas
         * pertenecientes a este cliente.
         */
        List<ReservaCancha> detalles =
                reservaCanchaRepository
                        .buscarPorCliente(
                                cliente.getIdCliente()
                        );


        /*
         * Convertimos cada ReservaCancha
         * al mismo ReservaResponseDTO que
         * ya utiliza el sistema.
         */
        return detalles
                .stream()
                .map(detalle -> {

                    Reserva reserva =
                            detalle.getReserva();


                    int cantidadExtras =
                            detalle
                                    .getCantidadExtras()
                                    .intValue();


                    int duracionMinutos =
                            60
                            + (
                                cantidadExtras
                                * 30
                            );


                    return convertirADTO(
                            reserva,
                            detalle,
                            duracionMinutos
                    );
                })
                .toList();
    }

    // =========================================================
// OBTENER UNA RESERVA DEL CLIENTE
// =========================================================

@Transactional(readOnly = true)
public ReservaResponseDTO obtenerMiReserva(
        Integer idUsuario,
        Integer idReserva
) {

    Cliente cliente =
            clienteRepository
                    .findByUsuarioIdUsuario(
                            idUsuario
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No existe un cliente asociado al usuario autenticado"
                            )
                    );


    ReservaCancha detalle =
            reservaCanchaRepository
                    .buscarPorCliente(
                            cliente.getIdCliente()
                    )
                    .stream()
                    .filter(item ->
                            item.getReserva()
                                    .getIdReserva()
                                    .equals(idReserva)
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No existe la reserva solicitada"
                            )
                    );


    int cantidadExtras =
            detalle.getCantidadExtras()
                    .intValue();


    int duracionMinutos =
            60
            + (
                cantidadExtras
                * 30
            );


    return convertirADTO(
            detalle.getReserva(),
            detalle,
            duracionMinutos
    );
}




    // =========================================================
// RESOLVER CLIENTE PARA RESERVA WEB
// =========================================================

private Cliente resolverClienteReservaWeb(
        ReservaWebRequestDTO request,
        Integer idUsuarioAutenticado
) {

    /*
     * =====================================================
     * USUARIO NO AUTENTICADO
     * =====================================================
     *
     * Se mantiene el funcionamiento original:
     * buscar o crear cliente mediante documento.
     */
    if (idUsuarioAutenticado == null) {

        return obtenerOCrearCliente(
                request.nombreCompleto(),
                request.tipoDocumento(),
                request.numDocumento(),
                request.email(),
                request.telefono()
        );
    }


    /*
     * =====================================================
     * USUARIO AUTENTICADO
     * =====================================================
     */

    Usuario usuario =
            usuarioRepository
                    .findById(
                            idUsuarioAutenticado
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No existe el usuario autenticado"
                            )
                    );


    /*
     * Solo las cuentas CLIENTE deben utilizar
     * su relación TB_USUARIO -> TB_CLIENTE.
     *
     * Si por alguna razón un ADMIN o EMPLEADO
     * utiliza el formulario público,
     * mantenemos el comportamiento de invitado.
     */
    if (
            usuario.getRol()
                    != RolUsuario.CLIENTE
    ) {

        return obtenerOCrearCliente(
                request.nombreCompleto(),
                request.tipoDocumento(),
                request.numDocumento(),
                request.email(),
                request.telefono()
        );
    }


    /*
     * Cuenta CLIENTE autenticada.
     *
     * En este caso NO confiamos en el
     * documento enviado desde Angular.
     *
     * Utilizamos:
     *
     * JWT.userId
     *      ↓
     * TB_USUARIO.id_usuario
     *      ↓
     * TB_CLIENTE.id_usuario
     */
    return clienteRepository
            .findByUsuarioIdUsuario(
                    idUsuarioAutenticado
            )
            .orElseThrow(() ->
                    new BusinessRuleException(
                            "El usuario autenticado no tiene un cliente asociado"
                    )
            );
}


    // =========================================================
    // OBTENER O CREAR CLIENTE
    // =========================================================

    private Cliente obtenerOCrearCliente(
            String nombreCompleto,
            TipoDocumento tipoDocumento,
            String numDocumento,
            String email,
            String telefono
    ) {

        return clienteRepository
                .findByTipoDocumentoAndNumDocumento(
                        tipoDocumento,
                        numDocumento
                )
                .map(cliente -> {

                    cliente.setNombreCompleto(
                            nombreCompleto
                    );


                    cliente.setEmail(
                            email
                    );


                    cliente.setTelefono(
                            telefono
                    );


                    return clienteRepository.save(
                            cliente
                    );
                })
                .orElseGet(() -> {

                    Cliente cliente =
                            new Cliente();


                    cliente.setUsuario(
                            null
                    );


                    cliente.setNombreCompleto(
                            nombreCompleto
                    );


                    cliente.setTipoDocumento(
                            tipoDocumento
                    );


                    cliente.setNumDocumento(
                            numDocumento
                    );


                    cliente.setEmail(
                            email
                    );


                    cliente.setTelefono(
                            telefono
                    );


                    return clienteRepository.save(
                            cliente
                    );
                });
    }


    // =========================================================
    // VALIDAR FECHA
    // =========================================================

    private void validarFecha(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalDateTime ahora
    ) {

        LocalDate hoy =
                ahora.toLocalDate();


        if (
                fecha.isBefore(
                        hoy
                )
        ) {

            throw new BusinessRuleException(
                    "No se puede reservar una fecha pasada"
            );
        }


        LocalDate fechaMaxima =
                hoy.plusMonths(
                        1
                );


        if (
                fecha.isAfter(
                        fechaMaxima
                )
        ) {

            throw new BusinessRuleException(
                    "Solo se puede reservar hasta un mes de anticipación"
            );
        }


        if (
                fecha.equals(
                        hoy
                )
                &&
                !horaInicio.isAfter(
                        ahora.toLocalTime()
                )
        ) {

            throw new BusinessRuleException(
                    "La hora de inicio debe ser posterior a la hora actual"
            );
        }
    }


    // =========================================================
    // CANCHA / SEDE
    // =========================================================

    private void validarCanchaDisponible(
            Cancha cancha
    ) {

        if (
                cancha.getEstado()
                        != EstadoCancha.ACTIVA
        ) {

            throw new ResourceConflictException(
                    "La cancha no está disponible"
            );
        }


        if (
                cancha.getSede()
                        .getEstado()
                        != EstadoSede.ACTIVA
        ) {

            throw new ResourceConflictException(
                    "La sede no está disponible"
            );
        }
    }


    // =========================================================
    // HORARIO
    // =========================================================

    private void validarDentroDelHorario(
            LocalTime horaInicio,
            LocalTime horaFin,
            HorarioCancha horario
    ) {

        if (
                horaInicio.isBefore(
                        horario.getHoraApertura()
                )
        ) {

            throw new BusinessRuleException(
                    "La reserva comienza antes del horario de apertura"
            );
        }


        if (
                horaFin.isAfter(
                        horario.getHoraCierre()
                )
        ) {

            throw new BusinessRuleException(
                    "La reserva excede el horario de cierre"
            );
        }
    }


    // =========================================================
    // PRECIO
    // =========================================================

    private BigDecimal calcularSubtotal(
            BigDecimal precioHora,
            int cantidadExtras
    ) {

        /*
         * Una hora equivale a 2 bloques
         * de 30 minutos.
         *
         * extras = 0 → 2 bloques
         * extras = 1 → 3 bloques
         * extras = 4 → 6 bloques
         */

        BigDecimal cantidadBloques =
                BigDecimal.valueOf(
                        2L
                        + cantidadExtras
                );


        return precioHora
                .multiply(
                        cantidadBloques
                )
                .divide(
                        BigDecimal.valueOf(
                                2
                        ),
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // =========================================================
    // DÍA DE SEMANA
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


    // =========================================================
    // CONVERTIR A DTO
    // =========================================================

    private ReservaResponseDTO convertirADTO(
            Reserva reserva,
            ReservaCancha detalle,
            int duracionMinutos
    ) {

        Cancha cancha =
                detalle.getCancha();


        Sede sede =
                cancha.getSede();


        return new ReservaResponseDTO(

                reserva.getIdReserva(),

                reserva.getCliente()
                        .getIdCliente(),

                reserva.getCliente()
                        .getNombreCompleto(),

                sede.getIdSede(),

                sede.getNombre(),

                cancha.getIdCancha(),

                cancha.getNombre(),

                detalle.getFechaTurno(),

                detalle.getHoraInicio(),

                detalle.getHoraFin(),

                detalle.getCantidadExtras()
                        .intValue(),

                duracionMinutos,

                detalle.getPrecioHora(),

                reserva.getTotal(),

                reserva.getEstado(),

                reserva.getOrigen(),

                reserva.getFechaRegistro(),

                reserva.getFechaExpiracion()
        );
    }


    // =========================================================
    // CREAR RESERVA PRESENCIAL
    // =========================================================

    @Transactional
    public ReservaResponseDTO crearReservaPresencial(
            ReservaPresencialRequestDTO request,
            Integer idUsuarioRegistro
    ) {

        LocalDateTime ahora =
                LocalDateTime.now();


        validarFecha(
                request.fechaTurno(),
                request.horaInicio(),
                ahora
        );


        Usuario usuarioRegistro =
                usuarioRepository
                        .findById(
                                idUsuarioRegistro
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe el usuario que registra la reserva"
                                )
                        );


        if (
                usuarioRegistro.getEstado()
                        != EstadoUsuario.ACTIVO
        ) {

            throw new BusinessRuleException(
                    "El usuario que registra la reserva no está activo"
            );
        }


        if (
                usuarioRegistro.getRol()
                        != RolUsuario.ADMIN
                &&
                usuarioRegistro.getRol()
                        != RolUsuario.EMPLEADO
        ) {

            throw new BusinessRuleException(
                    "El usuario no puede registrar reservas presenciales"
            );
        }


        Cancha cancha =
                canchaRepository
                        .findByIdForUpdate(
                                request.idCancha()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "No existe una cancha con id "
                                                + request.idCancha()
                                )
                        );


        validarCanchaDisponible(
                cancha
        );


        int duracionMinutos =
                60
                + (
                    request.cantidadExtras()
                    * 30
                );


        LocalTime horaFin =
                request.horaInicio()
                        .plusMinutes(
                                duracionMinutos
                        );


        if (
                !horaFin.isAfter(
                        request.horaInicio()
                )
        ) {

            throw new BusinessRuleException(
                    "La reserva no puede extenderse al día siguiente"
            );
        }


        DiaSemana diaSemana =
                convertirDiaSemana(
                        request.fechaTurno()
                );


        HorarioCancha horario =
                horarioRepository
                        .findByCanchaIdCanchaAndDiaSemanaAndEstado(
                                cancha.getIdCancha(),
                                diaSemana,
                                EstadoHorario.ACTIVO
                        )
                        .orElseThrow(() ->
                                new ResourceConflictException(
                                        "La cancha no tiene horario activo para "
                                                + diaSemana
                                )
                        );


        validarDentroDelHorario(
                request.horaInicio(),
                horaFin,
                horario
        );


        List<EstadoReserva> estadosQueBloquean =
                List.of(
                        EstadoReserva.PENDIENTE_PAGO,
                        EstadoReserva.PENDIENTE_CONFIRMACION,
                        EstadoReserva.CONFIRMADA
                );


        long solapamientos =
                reservaCanchaRepository
                        .contarSolapamientos(
                                cancha.getIdCancha(),
                                request.fechaTurno(),
                                request.horaInicio(),
                                horaFin,
                                estadosQueBloquean
                        );


        if (
                solapamientos > 0
        ) {

            throw new ResourceConflictException(
                    "La cancha ya está reservada en el horario seleccionado"
            );
        }


        Cliente cliente =
                obtenerOCrearCliente(
                        request.nombreCompleto(),
                        request.tipoDocumento(),
                        request.numDocumento(),
                        request.email(),
                        request.telefono()
                );


        BigDecimal precioHora =
                cancha.getPrecioHora();


        BigDecimal subtotal =
                calcularSubtotal(
                        precioHora,
                        request.cantidadExtras()
                );


        Reserva reserva =
                new Reserva();


        reserva.setCliente(
                cliente
        );


        reserva.setUsuarioRegistro(
                usuarioRegistro
        );


        reserva.setFechaRegistro(
                ahora
        );


        reserva.setFechaExpiracion(
                null
        );


        reserva.setTotal(
                subtotal
        );


        reserva.setEstado(
                EstadoReserva.CONFIRMADA
        );


        reserva.setOrigen(
                OrigenReserva.PRESENCIAL
        );


        Reserva reservaGuardada =
                reservaRepository.save(
                        reserva
                );


        ReservaCancha detalle =
                new ReservaCancha();


        detalle.setReserva(
                reservaGuardada
        );


        detalle.setCancha(
                cancha
        );


        detalle.setFechaTurno(
                request.fechaTurno()
        );


        detalle.setHoraInicio(
                request.horaInicio()
        );


        detalle.setHoraFin(
                horaFin
        );


        detalle.setPrecioHora(
                precioHora
        );


        detalle.setSubtotal(
                subtotal
        );


        detalle.setCantidadExtras(
                request.cantidadExtras()
                        .byteValue()
        );


        reservaCanchaRepository.save(
                detalle
        );


        Pago pago =
                new Pago();


        pago.setReserva(
                reservaGuardada
        );


        pago.setMetodoPago(
                request.metodoPago()
        );


        pago.setMonto(
                subtotal
        );


        pago.setFechaPago(
                ahora
        );


        pago.setNumOperacion(
                request.numOperacion() == null
                        ||
                        request.numOperacion().isBlank()
                        ? null
                        : request.numOperacion().trim()
        );


        pago.setComprobanteUrl(
                null
        );


        pago.setEstado(
                EstadoPago.APROBADO
        );


        pago.setFechaValidacion(
                ahora
        );


        pago.setUsuarioValidador(
                usuarioRegistro
        );


        pagoRepository.save(
                pago
        );


        return convertirADTO(
                reservaGuardada,
                detalle,
                duracionMinutos
        );
    }

}