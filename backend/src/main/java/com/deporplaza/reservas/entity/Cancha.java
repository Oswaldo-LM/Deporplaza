package com.deporplaza.reservas.entity;

import com.deporplaza.reservas.enums.EstadoCancha;
import com.deporplaza.reservas.enums.SuperficieCancha;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tb_cancha",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_cancha_sede_nombre",
                        columnNames = {
                                "sede_id",
                                "nombre"
                        }
                )
        }
)
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancha")
    private Integer idCancha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sede_id",
            nullable = false
    )
    private Sede sede;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "superficie",
            nullable = false,
            length = 15
    )
    private SuperficieCancha superficie;

    @Column(
            name = "precio_hora",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precioHora;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 20
    )
    private EstadoCancha estado;

    public Cancha() {
    }

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoCancha.ACTIVA;
        }
    }

    public Integer getIdCancha() {
        return idCancha;
    }

    public void setIdCancha(Integer idCancha) {
        this.idCancha = idCancha;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public SuperficieCancha getSuperficie() {
        return superficie;
    }

    public void setSuperficie(SuperficieCancha superficie) {
        this.superficie = superficie;
    }

    public BigDecimal getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(BigDecimal precioHora) {
        this.precioHora = precioHora;
    }

    public EstadoCancha getEstado() {
        return estado;
    }

    public void setEstado(EstadoCancha estado) {
        this.estado = estado;
    }
}