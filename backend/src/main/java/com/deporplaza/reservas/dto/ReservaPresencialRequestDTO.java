package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.MetodoPago;
import com.deporplaza.reservas.enums.TipoDocumento;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaPresencialRequestDTO(

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

        @NotNull
        Integer idCancha,

        @NotNull
        LocalDate fechaTurno,

        @NotNull
        LocalTime horaInicio,

        @NotNull
        @Min(0)
        @Max(4)
        Integer cantidadExtras,

        @NotNull
        MetodoPago metodoPago,

        @Size(max = 50)
        String numOperacion

) {
}