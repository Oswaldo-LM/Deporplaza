package com.deporplaza.reservas.dto;

import org.springframework.core.io.Resource;

public record ComprobanteArchivoDTO(
        Resource resource,
        String contentType,
        String nombreArchivo
) {
}