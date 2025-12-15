package com.iot.proyectofinal.service;

import com.iot.proyectofinal.dao.UsuarioDAO;
import com.iot.proyectofinal.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioDAO usuarioDAO;
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioDAO.obtenerTodos();
    }
    
    // Obtener usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioDAO.obtenerPorId(id);
    }
    
    // Crear nuevo usuario
    public Usuario crearUsuario(Usuario usuario) {
        // Validaciones
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es requerido");
        }
        
        if (usuarioDAO.existeUsuario(usuario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        // Establecer valores por defecto
        if (usuario.getRol() == null) {
            usuario.setRol("usuario");
        }
        
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        
        return usuarioDAO.guardar(usuario);
    }
    
    // Actualizar usuario
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistenteOpt = usuarioDAO.obtenerPorId(id);
        
        if (usuarioExistenteOpt.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOpt.get();
            
            // Actualizar solo campos permitidos
            if (usuarioActualizado.getEmail() != null) {
                usuarioExistente.setEmail(usuarioActualizado.getEmail());
            }
            
            if (usuarioActualizado.getRol() != null) {
                usuarioExistente.setRol(usuarioActualizado.getRol());
            }
            
            if (usuarioActualizado.getActivo() != null) {
                usuarioExistente.setActivo(usuarioActualizado.getActivo());
            }
            
            return usuarioDAO.guardar(usuarioExistente);
        }
        
        throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
    }
    
    // Eliminar usuario (logico)
    public boolean eliminarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioDAO.obtenerPorId(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setActivo(false);
            usuarioDAO.guardar(usuario);
            return true;
        }
        
        return false;
    }
    
    // Autenticar usuario
    public Usuario autenticar(String username, String password) {
        Usuario usuario = usuarioDAO.autenticar(username, password);
        
        if (usuario != null) {
            // Actualizar ultimo login
            usuario.setUltimoLogin(LocalDateTime.now());
            usuarioDAO.guardar(usuario);
        }
        
        return usuario;
    }
    
    // Verificar si usuario existe
    public boolean existeUsuario(String username) {
        return usuarioDAO.existeUsuario(username);
    }
}