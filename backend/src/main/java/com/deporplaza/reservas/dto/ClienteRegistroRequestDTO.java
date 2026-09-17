package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.TipoDocumento;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record ClienteRegistroRequestDTO(

        @NotBlank
        @Size(max = 150)
        String nombreCompleto,

        @NotNull
        TipoDocumento tipoDocumento,

        @NotBlank
        @Size(max = 20)
        String numDocumento,

        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(max = 20)
        String telefono,

        @NotBlank
        @Size(min = 8, max = 100)
        String password

) {
}