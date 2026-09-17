package com.deporplaza.reservas.dto;

import com.deporplaza.reservas.enums.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class PagoComprobanteRequestDTO {

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @Size(
            max = 50,
            message = "El número de operación no puede superar los 50 caracteres"
    )
    private String numOperacion;

    @NotNull(message = "El comprobante es obligatorio")
    private MultipartFile comprobante;


    public PagoComprobanteRequestDTO() {
    }


    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNumOperacion() {
        return numOperacion;
    }

    public void setNumOperacion(String numOperacion) {
        this.numOperacion = numOperacion;
    }

    public MultipartFile getComprobante() {
        return comprobante;
    }

    public void setComprobante(MultipartFile comprobante) {
        this.comprobante = comprobante;
    }
}