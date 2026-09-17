package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.RolUsuario;

public record LoginResponseDTO(

        String accessToken,
        String tokenType,

        Integer idUsuario,
        String nombre,
        String email,
        RolUsuario rol

) {
}