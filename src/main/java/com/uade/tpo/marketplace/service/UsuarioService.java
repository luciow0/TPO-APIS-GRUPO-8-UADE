package com.uade.tpo.marketplace.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.auth.AuthResponse;
import com.uade.tpo.marketplace.dto.PerfilPublicoResponse;

import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exception.UsuarioDuplicateException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;


public interface UsuarioService  {
    List<UsuarioDTO> listar();
    UsuarioDTO buscarPorId(Long id) throws UsuarioNotFoundException;
    Usuario obtenerUsuarioPorId(Long idUsuario) throws UsuarioNotFoundException;
    Usuario obtenerUsuarioPorEmail(String email) throws UsuarioNotFoundException;
    UsuarioDTO buscarPorEmail(String email) throws UsuarioNotFoundException;
    UsuarioDTO crear(UsuarioDTO usuarioDTO) throws UsuarioDuplicateException;
    UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) throws UsuarioNotFoundException, UsuarioDuplicateException;
    void eliminar(Long id) throws UsuarioNotFoundException;
    PerfilPublicoResponse buscarPerfilPublico(Long idUsuario) throws UsuarioNotFoundException;
    UsuarioDTO actualizarFoto(String email, MultipartFile file) throws UsuarioNotFoundException;
    UsuarioDTO quitarFoto(String email) throws UsuarioNotFoundException;
    UsuarioDTO actualizarTelefono(String email, String telefono) throws UsuarioNotFoundException;
    // Devuelve un token nuevo porque el token se arma con el email.
    AuthResponse actualizarEmail(String emailActual, String emailNuevo, String password) throws UsuarioNotFoundException;
    void actualizarPassword(String email, String actual, String nueva) throws UsuarioNotFoundException;
}
