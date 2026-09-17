package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.EstadoSede;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SedeRequestDTO(

        @NotBlank(message = "El nombre de la sede es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
        String direccion,

        @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
        String telefono,

        @NotNull(message = "El estado es obligatorio")
        EstadoSede estado

) {
}