package com.deporplaza.reservas.service;

import com.deporplaza.reservas.dto.ClientePerfilResponseDTO;
import com.deporplaza.reservas.entity.Cliente;
import com.deporplaza.reservas.exception.ResourceNotFoundException;
import com.deporplaza.reservas.repository.ClienteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ClientePerfilService {

    private final ClienteRepository clienteRepository;


    public ClientePerfilService(
            ClienteRepository clienteRepository
    ) {

        this.clienteRepository =
                clienteRepository;
    }


    @Transactional(readOnly = true)
    public ClientePerfilResponseDTO obtenerPerfil(
            Integer idUsuario
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


        return new ClientePerfilResponseDTO(

                cliente.getIdCliente(),

                cliente.getNombreCompleto(),

                cliente.getTipoDocumento(),

                cliente.getNumDocumento(),

                cliente.getEmail(),

                cliente.getTelefono()
        );
    }
}