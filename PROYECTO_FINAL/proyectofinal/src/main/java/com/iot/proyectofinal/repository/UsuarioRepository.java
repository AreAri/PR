package com.iot.proyectofinal.repository;

import com.iot.proyectofinal.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar usuario por nombre de usuario
    Optional<Usuario> findByUsername(String username);
    
    // Verificar si existe un usuario con ese username
    boolean existsByUsername(String username);
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
}