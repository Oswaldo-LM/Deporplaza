package com.deporplaza.reservas.entity;

import com.deporplaza.reservas.enums.EstadoReserva;
import com.deporplaza.reservas.enums.OrigenReserva;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_registro_id",
            nullable = true
    )
    private Usuario usuarioRegistro;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cliente_id",
            nullable = false
    )
    private Cliente cliente;


    @Column(
            name = "fecha_registro",
            nullable = false
    )
    private LocalDateTime fechaRegistro;


    @Column(
            name = "fecha_expiracion"
    )
    private LocalDateTime fechaExpiracion;


    @Column(
            name = "total",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal total;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoReserva estado;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "origen",
            nullable = false,
            length = 20
    )
    private OrigenReserva origen;


    public Reserva() {
    }


    @PrePersist
    public void prePersist() {

        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoReserva.PENDIENTE_PAGO;
        }
    }


    public Integer getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuario usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public OrigenReserva getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenReserva origen) {
        this.origen = origen;
    }
}