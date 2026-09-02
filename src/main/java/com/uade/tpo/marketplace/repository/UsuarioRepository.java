package com.uade.tpo.marketplace.repository;

import com.uade.tpo.marketplace.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	boolean existsByEmail(String email);

	java.util.Optional<Usuario> findByEmail(String email);
}