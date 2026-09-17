package com.deporplaza.reservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // =========================================================
    // 404 - RECURSO NO ENCONTRADO
    // =========================================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            ResourceNotFoundException ex
    ) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }


    // =========================================================
    // 400 - ERRORES DE VALIDACIÓN
    // =========================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errores = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            errores.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Error de validación");
        body.put("errors", errores);

        return ResponseEntity
                .badRequest()
                .body(body);
    }


    // =========================================================
    // 409 - CONFLICTO
    // =========================================================
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<Map<String, Object>> manejarConflicto(
            ResourceConflictException ex
    ) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }


    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(
        BusinessRuleException ex
    ) {

    Map<String, Object> body = new LinkedHashMap<>();

    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put("message", ex.getMessage());

    return ResponseEntity
            .badRequest()
            .body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(
        HttpMessageNotReadableException ex
    ) {

    Map<String, Object> body = new LinkedHashMap<>();

    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put(
            "message",
            "El cuerpo de la solicitud contiene datos inválidos"
    );

    return ResponseEntity
            .badRequest()
            .body(body);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorArchivo(
        FileStorageException ex
    ) {

    Map<String, Object> body = new LinkedHashMap<>();

    body.put("timestamp", LocalDateTime.now());
    body.put(
            "status",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
    );
    body.put(
            "error",
            "Internal Server Error"
    );
    body.put(
            "message",
            "No se pudo almacenar el comprobante"
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> manejarArchivoMuyGrande(
        MaxUploadSizeExceededException ex
    ) {

    Map<String, Object> body = new LinkedHashMap<>();

    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("error", "Bad Request");
    body.put(
            "message",
            "El comprobante no puede superar los 5 MB"
    );

    return ResponseEntity
            .badRequest()
            .body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>>
    manejarCredencialesInvalidas(
        InvalidCredentialsException ex
    ) {

    Map<String, Object> body =
            new LinkedHashMap<>();

    body.put(
            "timestamp",
            LocalDateTime.now()
    );

    body.put(
            "status",
            HttpStatus.UNAUTHORIZED.value()
    );

    body.put(
            "error",
            "Unauthorized"
    );

    body.put(
            "message",
            ex.getMessage()
    );


    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(body);
}
}