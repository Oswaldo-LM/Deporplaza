package com.deporplaza.reservas.service;

import com.deporplaza.reservas.entity.Usuario;
import com.deporplaza.reservas.enums.EstadoUsuario;
import com.deporplaza.reservas.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;


    public CustomUserDetailsService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository =
                usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Usuario no encontrado"
                                )
                        );


        boolean activo =
                usuario.getEstado()
                        == EstadoUsuario.ACTIVO;


        return User
                .withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(
                        "ROLE_" + usuario.getRol().name()
                )
                .disabled(!activo)
                .build();
    }
}