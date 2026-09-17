package com.deporplaza.reservas.service;

import com.deporplaza.reservas.entity.Usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;


    @Value("${app.jwt.expiration-minutes:120}")
    private long expirationMinutes;


    public JwtService(
            JwtEncoder jwtEncoder
    ) {
        this.jwtEncoder = jwtEncoder;
    }


    public String generarToken(
            Usuario usuario
    ) {

        Instant ahora =
                Instant.now();


        JwtClaimsSet claims =
                JwtClaimsSet.builder()

                        .issuer("reservas-api")

                        .issuedAt(ahora)

                        .expiresAt(
                                ahora.plus(
                                        expirationMinutes,
                                        ChronoUnit.MINUTES
                                )
                        )

                        .subject(
                                usuario.getEmail()
                        )

                        .claim(
                                "userId",
                                usuario.getIdUsuario()
                        )

                        .claim(
                                "roles",
                                List.of(
                                        usuario.getRol()
                                                .name()
                                )
                        )

                        .build();


        JwsHeader header =
                JwsHeader
                        .with(MacAlgorithm.HS256)
                        .build();


        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }
}