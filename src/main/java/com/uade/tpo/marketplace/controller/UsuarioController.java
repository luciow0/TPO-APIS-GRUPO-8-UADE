package com.uade.tpo.marketplace.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.marketplace.auth.AuthResponse;
import com.uade.tpo.marketplace.dto.EmailRequest;
import com.uade.tpo.marketplace.dto.PasswordRequest;
import com.uade.tpo.marketplace.dto.PerfilPublicoResponse;
import com.uade.tpo.marketplace.dto.TelefonoRequest;
import com.uade.tpo.marketplace.dto.UsuarioDTO;
import com.uade.tpo.marketplace.exception.UsuarioDuplicateException;
import com.uade.tpo.marketplace.exception.UsuarioNotFoundException;
import com.uade.tpo.marketplace.service.UsuarioService;
import com.uade.tpo.marketplace.service.ValidacionVolanta;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> listar() {
		return ResponseEntity.ok(usuarioService.listar());
	}

	@GetMapping("/me")
	public ResponseEntity<UsuarioDTO> obtenerUsuarioActual(Authentication authentication) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.buscarPorEmail(authentication.getName()));
	}

	@PostMapping("/me/foto")
	public ResponseEntity<UsuarioDTO> subirFoto(Authentication authentication,
			@RequestParam("file") MultipartFile file) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.actualizarFoto(authentication.getName(), file));
	}

	@DeleteMapping("/me/foto")
	public ResponseEntity<UsuarioDTO> quitarFoto(Authentication authentication) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.quitarFoto(authentication.getName()));
	}

	@PutMapping("/me/telefono")
	public ResponseEntity<UsuarioDTO> actualizarTelefono(Authentication authentication,
			@RequestBody TelefonoRequest request) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.actualizarTelefono(authentication.getName(), request.getTelefono()));
	}

	@PutMapping("/me/email")
	public ResponseEntity<AuthResponse> actualizarEmail(Authentication authentication,
			@RequestBody EmailRequest request) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.actualizarEmail(
				authentication.getName(), request.getEmail(), request.getPassword()));
	}

	@PutMapping("/me/password")
	public ResponseEntity<Void> actualizarPassword(Authentication authentication,
			@RequestBody PasswordRequest request) throws UsuarioNotFoundException {
		usuarioService.actualizarPassword(authentication.getName(), request.getActual(), request.getNueva());
		return ResponseEntity.noContent().build();
	}

	// Público: nunca incluye email, teléfono ni fecha de nacimiento.
	@GetMapping("/{id}/perfil")
	public ResponseEntity<PerfilPublicoResponse> perfilPublico(@PathVariable Long id) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.buscarPerfilPublico(id));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) throws UsuarioNotFoundException {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	@PostMapping
	public ResponseEntity<UsuarioDTO> crear(@RequestBody UsuarioDTO usuarioDTO) throws UsuarioDuplicateException {
		validarUsuario(usuarioDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(usuarioDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioDTO> actualizar(
			@PathVariable Long id,
			@RequestBody UsuarioDTO usuarioDTO) throws UsuarioNotFoundException, UsuarioDuplicateException {
		validarUsuario(usuarioDTO);
		return ResponseEntity.ok(usuarioService.actualizar(id, usuarioDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) throws UsuarioNotFoundException {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	private void validarUsuario(UsuarioDTO usuarioDTO) {
		ValidacionVolanta.validarNombreNoVacio(usuarioDTO.getNombre());
		ValidacionVolanta.validarApellidoNoVacio(usuarioDTO.getApellido());
		ValidacionVolanta.validarEmailValido(usuarioDTO.getEmail());
		ValidacionVolanta.validarPasswordValida(usuarioDTO.getPassword());
		ValidacionVolanta.validarTelefonoLargo(usuarioDTO.getTelefono());
		ValidacionVolanta.validarFechaNacimientoPasada(usuarioDTO.getFechaNacimiento());
	}
}
