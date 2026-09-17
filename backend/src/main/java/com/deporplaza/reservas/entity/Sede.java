package com.deporplaza.reservas.entity;

import com.deporplaza.reservas.enums.EstadoSede;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede")
    private Integer idSede;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100
    )
    private String nombre;

    @Column(
            name = "direccion",
            nullable = false,
            length = 200
    )
    private String direccion;

    @Column(
            name = "telefono",
            length = 20
    )
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 20
    )
    private EstadoSede estado;

    public Sede() {
    }

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoSede.ACTIVA;
        }
    }

    public Integer getIdSede() {
        return idSede;
    }

    public void setIdSede(Integer idSede) {
        this.idSede = idSede;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public EstadoSede getEstado() {
        return estado;
    }

    public void setEstado(EstadoSede estado) {
        this.estado = estado;
    }
}