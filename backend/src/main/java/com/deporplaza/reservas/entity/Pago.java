package com.deporplaza.reservas.entity;

import com.deporplaza.reservas.enums.EstadoPago;
import com.deporplaza.reservas.enums.MetodoPago;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_pago",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pago_reserva",
                        columnNames = "reserva_id"
                )
        }
)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reserva_id",
            nullable = false,
            unique = true
    )
    private Reserva reserva;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "metodo_pago",
            nullable = false,
            length = 30
    )
    private MetodoPago metodoPago;


    @Column(
            name = "monto",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal monto;


    @Column(
            name = "fecha_pago",
            nullable = false
    )
    private LocalDateTime fechaPago;


    @Column(
            name = "num_operacion",
            length = 50
    )
    private String numOperacion;


    @Column(
            name = "comprobante_url",
            length = 255
    )
    private String comprobanteUrl;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoPago estado;


    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_validador_id"
    )
    private Usuario usuarioValidador;


    public Pago() {
    }


    @PrePersist
    public void prePersist() {

        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoPago.PENDIENTE_VALIDACION;
        }
    }


    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getNumOperacion() {
        return numOperacion;
    }

    public void setNumOperacion(String numOperacion) {
        this.numOperacion = numOperacion;
    }

    public String getComprobanteUrl() {
        return comprobanteUrl;
    }

    public void setComprobanteUrl(String comprobanteUrl) {
        this.comprobanteUrl = comprobanteUrl;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public Usuario getUsuarioValidador() {
        return usuarioValidador;
    }

    public void setUsuarioValidador(Usuario usuarioValidador) {
        this.usuarioValidador = usuarioValidador;
    }
}