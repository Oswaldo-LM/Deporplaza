package com.deporplaza.reservas.controller;

import com.deporplaza.reservas.dto.ClienteRegistroRequestDTO;
import com.deporplaza.reservas.dto.LoginRequestDTO;
import com.deporplaza.reservas.dto.LoginResponseDTO;

import com.deporplaza.reservas.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService
            authService;


    public AuthController(
            AuthService authService
    ) {

        this.authService =
                authService;
    }


    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponseDTO>
    loginAdmin(

            @Valid
            @RequestBody
            LoginRequestDTO request

    ) {

        return ResponseEntity.ok(

                authService
                        .loginAdmin(
                                request
                        )

        );
    }


    @PostMapping("/cliente/login")
    public ResponseEntity<LoginResponseDTO>
    loginCliente(

            @Valid
            @RequestBody
            LoginRequestDTO request

    ) {

        return ResponseEntity.ok(

                authService
                        .loginCliente(
                                request
                        )

        );
    }


    @PostMapping("/cliente/registro")
    public ResponseEntity<LoginResponseDTO>
    registrarCliente(

            @Valid
            @RequestBody
            ClienteRegistroRequestDTO request

    ) {

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(

                        authService
                                .registrarCliente(
                                        request
                                )

                );
    }

}