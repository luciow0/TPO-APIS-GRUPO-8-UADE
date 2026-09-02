package com.uade.tpo.marketplace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> listar() {
		return ResponseEntity.ok(usuarioService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioDTO usuarioDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(usuarioDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioDTO> actualizar(
			@PathVariable Long id,
			@Valid @RequestBody UsuarioDTO usuarioDTO) {
		return ResponseEntity.ok(usuarioService.actualizar(id, usuarioDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}