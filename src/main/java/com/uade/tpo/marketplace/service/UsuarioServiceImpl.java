package com.uade.tpo.marketplace.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.auth.AuthResponse;
import com.uade.tpo.marketplace.config.JwtService;
import com.uade.tpo.marketplace.dto.PerfilPublicoResponse;
import com.uade.tpo.marketplace.exception.ContrasenaIncorrectaException;
import com.uade.tpo.marketplace.service.ValidacionVolanta;

import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exception.UsuarioDuplicateException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final long MAX_FOTO_BYTES = 5L * 1024 * 1024;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@seguridadDominio.esMismoUsuario(authentication, #id) or hasRole('ADMIN')")
    public UsuarioDTO buscarPorId(Long id) throws UsuarioNotFoundException {
        return convertirADTO(obtenerUsuario(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long idUsuario) throws UsuarioNotFoundException {
        return obtenerUsuario(idUsuario);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorEmail(String email) throws UsuarioNotFoundException {
        return convertirADTO(obtenerUsuarioPorEmail(email));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorEmail(String email) throws UsuarioNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNotFoundException::new);
    }

    @Override
    @Transactional
    public UsuarioDTO crear(UsuarioDTO usuarioDTO) throws UsuarioDuplicateException {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new UsuarioDuplicateException();
        }

        Usuario usuario = convertirAEntidad(usuarioDTO);
        return convertirADTO(usuarioRepository.save(usuario));
    }

    @PreAuthorize("@seguridadDominio.esMismoUsuario(authentication, #id) or hasRole('ADMIN')")
    @Override
    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO)
            throws UsuarioNotFoundException, UsuarioDuplicateException {
        Usuario usuario = obtenerUsuario(id);

        Optional<Usuario> otroUsuario = usuarioRepository.findByEmail(usuarioDTO.getEmail());
        if (otroUsuario.isPresent() && !otroUsuario.get().getIdUsuario().equals(id)) {
            throw new UsuarioDuplicateException();
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        return convertirADTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(Long id) throws UsuarioNotFoundException {
        usuarioRepository.delete(obtenerUsuario(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilPublicoResponse buscarPerfilPublico(Long idUsuario) throws UsuarioNotFoundException {
        Usuario u = obtenerUsuario(idUsuario);
        return new PerfilPublicoResponse(
                u.getIdUsuario(), u.getNombre(), u.getApellido(), fotoABase64(u), u.getFechaRegistro());
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UsuarioDTO actualizarFoto(String email, MultipartFile file) throws UsuarioNotFoundException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Elegí una imagen para subir.");
        }
        if (file.getSize() > MAX_FOTO_BYTES) {
            throw new IllegalArgumentException("La foto pesa más de 5 MB. Elegí una más liviana.");
        }
        try {
            byte[] bytes = file.getBytes();
            if (!esJpgOPng(bytes)) {
                throw new IllegalArgumentException("La foto tiene que ser JPG o PNG.");
            }
            Usuario usuario = obtenerUsuarioPorEmail(email);
            usuario.setFoto(new SerialBlob(bytes));
            return convertirADTO(usuarioRepository.save(usuario));
        } catch (IOException | SQLException e) {
            throw new IllegalArgumentException("No pudimos leer la imagen. Probá con un JPG o PNG.");
        }
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UsuarioDTO quitarFoto(String email) throws UsuarioNotFoundException {
        Usuario usuario = obtenerUsuarioPorEmail(email);
        usuario.setFoto(null);
        return convertirADTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UsuarioDTO actualizarTelefono(String email, String telefono) throws UsuarioNotFoundException {
        ValidacionVolanta.validarTelefonoNuevo(telefono);
        Usuario usuario = obtenerUsuarioPorEmail(email);
        usuario.setTelefono(telefono);
        return convertirADTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public AuthResponse actualizarEmail(String emailActual, String emailNuevo, String password)
            throws UsuarioNotFoundException {
        ValidacionVolanta.validarEmailValido(emailNuevo);
        ValidacionVolanta.validarPasswordIngresada(password);
        emailNuevo = emailNuevo.trim();

        Usuario usuario = obtenerUsuarioPorEmail(emailActual);
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new ContrasenaIncorrectaException("La contraseña no es correcta.");
        }
        Optional<Usuario> otro = usuarioRepository.findByEmail(emailNuevo);
        if (otro.isPresent() && !otro.get().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalStateException("Ya existe una cuenta con ese email. Usá otro.");
        }

        usuario.setEmail(emailNuevo);
        usuarioRepository.saveAndFlush(usuario);

        // El token lleva el email: se devuelve uno nuevo para no perder la sesión.
        UserDetails ud = userDetailsService.loadUserByUsername(emailNuevo);
        return new AuthResponse(jwtService.generateToken(Map.of("role", usuario.getRole().name()), ud));
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void actualizarPassword(String email, String actual, String nueva) throws UsuarioNotFoundException {
        ValidacionVolanta.validarPasswordIngresada(actual);
        ValidacionVolanta.validarPasswordNueva(nueva);

        Usuario usuario = obtenerUsuarioPorEmail(email);
        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new ContrasenaIncorrectaException("La contraseña actual no es correcta.");
        }
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    private static boolean esJpgOPng(byte[] b) {
        boolean jpg = b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
        boolean png = b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G';
        return jpg || png;
    }

    private String fotoABase64(Usuario usuario) {
        Blob blob = usuario.getFoto();
        if (blob == null) {
            return null;
        }
        try {
            return Base64.getEncoder().encodeToString(blob.getBytes(1, (int) blob.length()));
        } catch (SQLException e) {
            return null;
        }
    }

    private Usuario obtenerUsuario(Long id) throws UsuarioNotFoundException {
        return usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getTelefono(),
                usuario.getFechaNacimiento(),
                usuario.getRole(),
                usuario.getFechaRegistro(),
                fotoABase64(usuario));
    }

    private Usuario convertirAEntidad(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        return usuario;
    }
}
