package com.deporplaza.reservas.repository;

import com.deporplaza.reservas.entity.Cliente;
import com.deporplaza.reservas.enums.TipoDocumento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ClienteRepository
        extends JpaRepository<Cliente, Integer> {


    /*
     * Buscar cliente por documento.
     *
     * Se utiliza, por ejemplo, durante
     * el registro de una cuenta CLIENTE.
     */
    Optional<Cliente> findByTipoDocumentoAndNumDocumento(
            TipoDocumento tipoDocumento,
            String numDocumento
    );


    /*
     * Buscar el registro TB_CLIENTE
     * asociado a un usuario autenticado.
     *
     * TB_USUARIO.id_usuario
     *          ↓
     * TB_CLIENTE.id_usuario
     */
    Optional<Cliente> findByUsuarioIdUsuario(
            Integer idUsuario
    );

}