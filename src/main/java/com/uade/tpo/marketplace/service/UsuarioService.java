package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.entity.Usuario;
import com.uade.tpo.marketplace.exception.UsuarioDuplicateException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;


public interface UsuarioService  {
    List<UsuarioDTO> listar();
    UsuarioDTO buscarPorId(Long id) throws UsuarioNotFoundException;
    Usuario obtenerUsuarioPorId(Long idUsuario) throws UsuarioNotFoundException;
    UsuarioDTO crear(UsuarioDTO usuarioDTO) throws UsuarioDuplicateException;
    UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO) throws UsuarioNotFoundException, UsuarioDuplicateException;
    void eliminar(Long id) throws UsuarioNotFoundException;
}
