package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.EstadoCancha;
import com.deporplaza.reservas.enums.SuperficieCancha;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CanchaRequestDTO(

        @NotNull(message = "La sede es obligatoria")
        Integer idSede,

        @NotBlank(message = "El nombre de la cancha es obligatorio")
        @Size(
                max = 100,
                message = "El nombre no puede superar los 100 caracteres"
        )
        String nombre,

        @NotNull(message = "La superficie es obligatoria")
        SuperficieCancha superficie,

        @NotNull(message = "El precio por hora es obligatorio")
        @DecimalMin(
                value = "0.01",
                message = "El precio por hora debe ser mayor que 0"
        )
        BigDecimal precioHora,

        @NotNull(message = "El estado es obligatorio")
        EstadoCancha estado

) {
}