package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.entity.Usuario;


public interface UsuarioService  {
    List<UsuarioDTO> listar();
    UsuarioDTO buscarPorId(Long id);
    Usuario obtenerUsuarioPorId(Long idUsuario);
    UsuarioDTO crear(UsuarioDTO usuarioDTO);
    UsuarioDTO actualizar(Long id, UsuarioDTO usuarioDTO);
    void eliminar(Long id);
}
