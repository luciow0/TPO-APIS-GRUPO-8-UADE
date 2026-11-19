package com.uade.tpo.marketplace.auth;

import com.uade.tpo.marketplace.Enum.Role;
import com.uade.tpo.marketplace.config.JwtService;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.repository.UsuarioRepository;
import com.uade.tpo.marketplace.service.ValidacionVolanta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest req) {
        ValidacionVolanta.validarDatosCompletos(req);
        ValidacionVolanta.validarNombreNoVacio(req.getNombre());
        ValidacionVolanta.validarApellidoNoVacio(req.getApellido());
        ValidacionVolanta.validarEmailValido(req.getEmail());
        ValidacionVolanta.validarPasswordValida(req.getPassword());
        ValidacionVolanta.validarTelefonoLargo(req.getTelefono());
        ValidacionVolanta.validarFechaNacimientoPasada(req.getFechaNacimiento());

        if (usuarioRepository.findByEmail(req.getEmail()).isPresent())
            throw new IllegalStateException("Ya existe una cuenta con ese email. Iniciá sesión o usá otro.");


        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setApellido(req.getApellido());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword())); // BCrypt
        u.setTelefono(req.getTelefono());
        u.setFechaNacimiento(req.getFechaNacimiento());
        u.setRole(Role.USER);
        usuarioRepository.save(u);

        UserDetails ud = userDetailsService.loadUserByUsername(u.getEmail());
        return new AuthResponse(jwtService.generateToken(Map.of("role", u.getRole().name()), ud));
    }

    public AuthResponse login(LoginRequest req) {
        ValidacionVolanta.validarDatosCompletos(req);
        ValidacionVolanta.validarEmailValido(req.getEmail());
        ValidacionVolanta.validarPasswordIngresada(req.getPassword());

        authenticationManager.authenticate(          // valida email+password con BCrypt
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        Usuario u = usuarioRepository.findByEmail(req.getEmail()).orElseThrow();
        UserDetails ud = userDetailsService.loadUserByUsername(u.getEmail());
        return new AuthResponse(jwtService.generateToken(Map.of("role", u.getRole().name()), ud));
    }

}
