package com.deporplaza.reservas.config;

import com.deporplaza.reservas.entity.Usuario;
import com.deporplaza.reservas.enums.EstadoUsuario;
import com.deporplaza.reservas.enums.RolUsuario;
import com.deporplaza.reservas.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer
        implements CommandLineRunner {

    private final UsuarioRepository
            usuarioRepository;

    private final PasswordEncoder
            passwordEncoder;


    @Value("${app.bootstrap-admin.email:}")
    private String email;

    @Value("${app.bootstrap-admin.password:}")
    private String password;


    public AdminInitializer(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository =
                usuarioRepository;

        this.passwordEncoder =
                passwordEncoder;
    }


    @Override
    public void run(String... args) {

        if (email == null
                || email.isBlank()
                || password == null
                || password.isBlank()) {

            return;
        }


        if (usuarioRepository
                .existsByEmail(email)) {

            return;
        }


        Usuario admin =
                new Usuario();

        admin.setNombre(
                "Administrador"
        );

        admin.setEmail(
                email.trim()
        );

        admin.setPassword(
                passwordEncoder.encode(
                        password
                )
        );

        admin.setRol(
                RolUsuario.ADMIN
        );

        admin.setEstado(
                EstadoUsuario.ACTIVO
        );


        usuarioRepository.save(admin);
    }
}