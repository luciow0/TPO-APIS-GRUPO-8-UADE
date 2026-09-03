package com.uade.tpo.marketplace.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uade.tpo.marketplace.repository.UsuarioRepository;

/**
 * Beans de autenticacion que usan el filtro JWT y el SecurityConfig.
 *
 * UserDetailsService: le dice a Spring como buscar un usuario por email.
 * AuthenticationProvider: valida email + password contra la base (con BCrypt).
 * AuthenticationManager: lo usa el endpoint de login para autenticar.
 */

@Configuration
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    public ApplicationConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.findByEmail(email)
                .map(usuario -> User.builder()
                        .username(usuario.getEmail())
                        .password(usuario.getPassword())
                        // TODO (roles - HACER): cuando el Usuario tenga rol, mapearlo aca.
                        //   Ej: .authorities(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()))
                        .authorities(Collections.emptyList())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
