package com.deporplaza.reservas.entity;

import com.deporplaza.reservas.enums.DiaSemana;
import com.deporplaza.reservas.enums.EstadoHorario;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(
        name = "tb_horario_cancha",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_horario_cancha_dia",
                        columnNames = {
                                "cancha_id",
                                "dia_semana"
                        }
                )
        }
)
public class HorarioCancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario_cancha")
    private Integer idHorarioCancha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cancha_id",
            nullable = false
    )
    private Cancha cancha;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "dia_semana",
            nullable = false,
            length = 15
    )
    private DiaSemana diaSemana;

    @Column(
            name = "hora_apertura",
            nullable = false
    )
    private LocalTime horaApertura;

    @Column(
            name = "hora_cierre",
            nullable = false
    )
    private LocalTime horaCierre;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 20
    )
    private EstadoHorario estado;

    public HorarioCancha() {
    }

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoHorario.ACTIVO;
        }
    }

    public Integer getIdHorarioCancha() {
        return idHorarioCancha;
    }

    public void setIdHorarioCancha(Integer idHorarioCancha) {
        this.idHorarioCancha = idHorarioCancha;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }

    public EstadoHorario getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorario estado) {
        this.estado = estado;
    }
}