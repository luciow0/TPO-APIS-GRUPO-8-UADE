package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exception.UsuarioDuplicateException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;
import com.uade.tpo.marketplace.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
                usuario.getFechaNacimiento());
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
