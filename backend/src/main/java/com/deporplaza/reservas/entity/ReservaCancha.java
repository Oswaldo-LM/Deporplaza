package com.deporplaza.reservas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tb_reserva_cancha")
public class ReservaCancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_cancha")
    private Integer idReservaCancha;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reserva_id",
            nullable = false
    )
    private Reserva reserva;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cancha_id",
            nullable = false
    )
    private Cancha cancha;


    @Column(
            name = "fecha_turno",
            nullable = false
    )
    private LocalDate fechaTurno;


    @Column(
            name = "hora_inicio",
            nullable = false
    )
    private LocalTime horaInicio;


    @Column(
            name = "hora_fin",
            nullable = false
    )
    private LocalTime horaFin;


    @Column(
            name = "precio_hora",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precioHora;


    @Column(
            name = "subtotal",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal subtotal;


    @Column(
        name = "cantidad_extras",
        nullable = false
    )
    private Byte cantidadExtras;


    public ReservaCancha() {
    }


    @PrePersist
    public void prePersist() {

    if (cantidadExtras == null) {
        cantidadExtras = (byte) 0;
    }
    }


    public Integer getIdReservaCancha() {
        return idReservaCancha;
    }

    public void setIdReservaCancha(Integer idReservaCancha) {
        this.idReservaCancha = idReservaCancha;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public LocalDate getFechaTurno() {
        return fechaTurno;
    }

    public void setFechaTurno(LocalDate fechaTurno) {
        this.fechaTurno = fechaTurno;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(BigDecimal precioHora) {
        this.precioHora = precioHora;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Byte getCantidadExtras() {
    return cantidadExtras;
    }

    public void setCantidadExtras(Byte cantidadExtras) {
    this.cantidadExtras = cantidadExtras;
    }
}