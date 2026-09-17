package com.deporplaza.reservas.entity;

import com.deporplaza.reservas.enums.TipoDocumento;
import jakarta.persistence.*;

@Entity
@Table(
        name = "tb_cliente",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_cliente_usuario",
                        columnNames = "id_usuario"
                ),
                @UniqueConstraint(
                        name = "uq_cliente_documento",
                        columnNames = {
                                "tipo_documento",
                                "num_documento"
                        }
                )
        }
)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_usuario",
            referencedColumnName = "id_usuario",
            nullable = true,
            unique = true
    )
    private Usuario usuario;

    @Column(
            name = "nombre_completo",
            nullable = false,
            length = 150
    )
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_documento",
            nullable = false,
            length = 20
    )
    private TipoDocumento tipoDocumento;

    @Column(
            name = "num_documento",
            nullable = false,
            length = 20
    )
    private String numDocumento;

    @Column(
            name = "email",
            nullable = false,
            length = 150
    )
    private String email;

    @Column(
            name = "telefono",
            nullable = false,
            length = 20
    )
    private String telefono;

    public Cliente() {
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}