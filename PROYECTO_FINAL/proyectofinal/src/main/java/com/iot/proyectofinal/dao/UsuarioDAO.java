package com.iot.proyectofinal.dao;

import com.iot.proyectofinal.model.Usuario;
import com.iot.proyectofinal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;


@Component
public class UsuarioDAO {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
    
    // Obtener usuario por ID
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    // Obtener usuario por username
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    // Guardar usuario (crear o actualizar)
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    // Eliminar usuario por ID
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    // Verificar si existe usuario por username
    public boolean existeUsuario(String username) {
        return usuarioRepository.existsByUsername(username);
    }
    
    // Autenticar usuario
    public Usuario autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // NOTA: En produccion usar BCrypt para comparar hashes
            if (usuario.getPasswordHash().equals(password)) {
                return usuario;
            }
        }
        return null;
    }
}