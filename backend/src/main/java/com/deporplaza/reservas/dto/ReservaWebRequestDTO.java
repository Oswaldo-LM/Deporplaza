package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.TipoDocumento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaWebRequestDTO(

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 150)
        String nombreCompleto,

        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(max = 20)
        String numDocumento,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 150)
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20)
        String telefono,

        @NotNull(message = "La cancha es obligatoria")
        Integer idCancha,

        @NotNull(message = "La fecha del turno es obligatoria")
        LocalDate fechaTurno,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La cantidad de extras es obligatoria")
        @Min(
                value = 0,
                message = "La cantidad de extras no puede ser menor que 0"
        )
        @Max(
                value = 4,
                message = "La cantidad máxima de extras es 4"
        )
        Integer cantidadExtras

) {
}