package com.uade.tpo.marketplace.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.uade.tpo.marketplace.exception.GlobalExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuración central de Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/pagos/mercadopago/retorno").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pagos/mercadopago/webhook").permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/publicaciones/**",
                                "/disponibilidades/publicacion/{idPublicacion}",
                                "/imagen-vehiculo",
                                "/imagen-vehiculo/**",
                                "/vehiculo",
                                "/vehiculo/**",
                                "/tipo-vehiculo",
                                "/tipo-vehiculo/**")
                        .permitAll()
                        .anyRequest().authenticated()

                )

                // Sin sesion (o con el token vencido) y sin permiso: mismo formato {"mensaje"} que el resto.
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint((request, response, ex) -> escribirMensaje(
                                response, HttpStatus.UNAUTHORIZED, GlobalExceptionHandler.MENSAJE_SESION_VENCIDA))
                        .accessDeniedHandler((request, response, ex) -> escribirMensaje(
                                response, HttpStatus.FORBIDDEN, GlobalExceptionHandler.MENSAJE_SIN_PERMISO)))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void escribirMensaje(HttpServletResponse response, HttpStatus status, String mensaje)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        // Los mensajes son constantes sin comillas, asi que el JSON se arma directo.
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
