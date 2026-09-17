package com.deporplaza.reservas.config;

import com.deporplaza.reservas.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.web.SecurityFilterChain;

import com.deporplaza.reservas.security.RestAccessDeniedHandler;
import com.deporplaza.reservas.security.RestAuthenticationEntryPoint;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider provider
    ) {

        return new ProviderManager(
                List.of(provider)
        );
    }


    @Bean
    public JwtAuthenticationConverter
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter roles =
                new JwtGrantedAuthoritiesConverter();

        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");


        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                roles
        );

        return converter;
    }


    @Bean
public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationConverter jwtConverter,
        CorsConfigurationSource corsConfigurationSource,
        RestAuthenticationEntryPoint authenticationEntryPoint,
        RestAccessDeniedHandler accessDeniedHandler
) throws Exception {

    http

            .csrf(csrf ->
                    csrf.disable()
            )

            .cors(cors ->
                    cors.configurationSource(
                            corsConfigurationSource
                    )
            )

            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            .exceptionHandling(exception ->
                    exception
                            .authenticationEntryPoint(
                                    authenticationEntryPoint
                            )
                            .accessDeniedHandler(
                                    accessDeniedHandler
                            )
            )

            .authorizeHttpRequests(auth -> auth

                    // LOGIN
                    .requestMatchers(
                            "/api/auth/**"
                    ).permitAll()


                    // HEALTH
                    .requestMatchers(
                            "/api/health/**"
                    ).permitAll()


                    // CONSULTAS PÚBLICAS
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/sedes/**",
                            "/api/canchas/**",
                            "/api/horarios/**",
                            "/api/disponibilidad/**"
                    ).permitAll()


                    // RESERVA WEB
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/reservas/web"
                    ).permitAll()


                    // SUBIR COMPROBANTE
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/reservas/*/pago"
                    ).permitAll()


                    // ADMIN
                    .requestMatchers(
                            "/api/admin/**"
                    ).hasRole("ADMIN")

                    .requestMatchers("/api/cliente/**")
                    .hasRole("CLIENTE")


                    // SEDES
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/sedes/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/sedes/**"
                    ).hasRole("ADMIN")


                    // CANCHAS
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/canchas/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/canchas/**"
                    ).hasRole("ADMIN")


                    // HORARIOS
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/horarios/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.PUT,
                            "/api/horarios/**"
                    ).hasRole("ADMIN")


                    .anyRequest()
                    .authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwt ->
                            jwt.jwtAuthenticationConverter(
                                    jwtConverter
                            )
                    )
            );


    return http.build();
}

    @Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
            new CorsConfiguration();

    configuration.setAllowedOrigins(
            List.of(
                    "http://localhost:4200",
                    "http://127.0.0.1:4200"
            )
    );

    configuration.setAllowedMethods(
            List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "PATCH",
                    "DELETE",
                    "OPTIONS"
            )
    );

    configuration.setAllowedHeaders(
            List.of(
                    "Authorization",
                    "Content-Type",
                    "Accept"
            )
    );

    configuration.setExposedHeaders(
            List.of(
                    "Location"
            )
    );

    configuration.setAllowCredentials(false);


    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
            "/**",
            configuration
    );

    return source;
}
}