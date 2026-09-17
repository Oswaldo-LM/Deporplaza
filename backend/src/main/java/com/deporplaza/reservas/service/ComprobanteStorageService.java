package com.deporplaza.reservas.service;

import com.deporplaza.reservas.exception.BusinessRuleException;
import com.deporplaza.reservas.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ComprobanteStorageService {

    private static final long MAX_SIZE =
            5L * 1024 * 1024;

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "pdf"
            );

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "application/pdf"
            );


    private final Path root;


    public ComprobanteStorageService(
            @Value("${app.storage.root:uploads}")
            String root
    ) {
        this.root =
                Paths.get(root)
                        .toAbsolutePath()
                        .normalize();
    }


    public String guardar(
            MultipartFile archivo
    ) {

        validarArchivo(archivo);


        String extension =
                obtenerExtension(archivo);


        String nombreArchivo =
                UUID.randomUUID()
                        + "."
                        + extension;


        Path directorio =
                root.resolve("comprobantes")
                        .normalize();


        Path destino =
                directorio.resolve(nombreArchivo)
                        .normalize();


        if (!destino.startsWith(directorio)) {

            throw new BusinessRuleException(
                    "Nombre de archivo inválido"
            );
        }


        try {

            Files.createDirectories(directorio);

            try (InputStream inputStream =
                         archivo.getInputStream()) {

                Files.copy(
                        inputStream,
                        destino,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException ex) {

            throw new FileStorageException(
                    "No se pudo almacenar el comprobante",
                    ex
            );
        }


        /*
         * Guardamos una ruta relativa en MySQL,
         * NO C:\\... ni una ruta absoluta.
         */
        return "comprobantes/" + nombreArchivo;
    }


    private void validarArchivo(
            MultipartFile archivo
    ) {

        if (archivo == null
                || archivo.isEmpty()) {

            throw new BusinessRuleException(
                    "El comprobante está vacío"
            );
        }


        if (archivo.getSize() > MAX_SIZE) {

            throw new BusinessRuleException(
                    "El comprobante no puede superar los 5 MB"
            );
        }


        String contentType =
                archivo.getContentType();


        if (contentType == null
                || !TIPOS_PERMITIDOS.contains(
                contentType.toLowerCase(Locale.ROOT)
        )) {

            throw new BusinessRuleException(
                    "El comprobante debe ser JPG, PNG o PDF"
            );
        }


        String extension =
                obtenerExtension(archivo);


        if (!EXTENSIONES_PERMITIDAS.contains(
                extension
        )) {

            throw new BusinessRuleException(
                    "Extensión de archivo no permitida"
            );
        }
    }


    private String obtenerExtension(
            MultipartFile archivo
    ) {

        String nombreOriginal =
                archivo.getOriginalFilename();


        String extension =
                StringUtils.getFilenameExtension(
                        nombreOriginal
                );


        if (extension == null
                || extension.isBlank()) {

            throw new BusinessRuleException(
                    "El comprobante debe tener una extensión válida"
            );
        }


        return extension.toLowerCase(
                Locale.ROOT
        );
    }

    public Resource cargar(
        String rutaRelativa
) {

    if (rutaRelativa == null
            || rutaRelativa.isBlank()) {

        throw new BusinessRuleException(
                "El pago no tiene un comprobante asociado"
        );
    }


    Path archivo =
            root.resolve(rutaRelativa)
                    .normalize();


    if (!archivo.startsWith(root)) {

        throw new BusinessRuleException(
                "Ruta de comprobante inválida"
        );
    }


    try {

        Resource resource =
                new UrlResource(
                        archivo.toUri()
                );


        if (!resource.exists()
                || !resource.isReadable()) {

            throw new FileStorageException(
                    "El comprobante no existe o no puede leerse"
            );
        }


        return resource;

    } catch (java.net.MalformedURLException ex) {

        throw new FileStorageException(
                "No se pudo leer el comprobante",
                ex
        );
    }
}


    public String obtenerContentType(
        String rutaRelativa
)   {

    Path archivo =
            root.resolve(rutaRelativa)
                    .normalize();

    try {

        String contentType =
                Files.probeContentType(archivo);

        return contentType != null
                ? contentType
                : "application/octet-stream";

    } catch (IOException ex) {

        return "application/octet-stream";
    }
    }


   public String obtenerNombreArchivo(
        String rutaRelativa
   ) {

    return Paths.get(rutaRelativa)
            .getFileName()
            .toString();
   }
}