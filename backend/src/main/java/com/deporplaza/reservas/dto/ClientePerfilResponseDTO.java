package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.TipoDocumento;


public record ClientePerfilResponseDTO(

        Integer idCliente,

        String nombreCompleto,

        TipoDocumento tipoDocumento,

        String numDocumento,

        String email,

        String telefono

) {
}