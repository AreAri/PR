package com.iot.proyectofinal.service;

import com.iot.proyectofinal.dao.UsuarioDAO;
import com.iot.proyectofinal.model.Usuario;
import com.iot.proyectofinal.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioDAO usuarioDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioDAO.obtenerTodos();
    }
    
    // Obtener usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioDAO.obtenerPorId(id);
    }
    
    // Obtener usuario por username
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioDAO.obtenerPorUsername(username);
    }
    
    // Registrar nuevo usuario
    public Map<String, Object> registrarUsuario(Usuario usuario) {
        Map<String, Object> respuesta = new HashMap<>();
        
        // Validaciones
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            respuesta.put("error", "El nombre de usuario es requerido");
            return respuesta;
        }
        
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
            respuesta.put("error", "La contraseña es requerida");
            return respuesta;
        }
        
        if (usuarioDAO.existeUsuario(usuario.getUsername())) {
            respuesta.put("error", "El nombre de usuario ya existe");
            return respuesta;
        }
        
        // Encriptar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        
        // Valores por defecto
        if (usuario.getRol() == null) {
            usuario.setRol("usuario");
        }
        
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        
        // Guardar usuario
        Usuario usuarioGuardado = usuarioDAO.guardar(usuario);
        
        // Generar token
        String token = jwtService.generateToken(usuario.getUsername(), usuario.getRol());
        
        // Preparar respuesta
        respuesta.put("success", true);
        respuesta.put("mensaje", "Usuario registrado exitosamente");
        respuesta.put("usuario", usuarioGuardado);
        respuesta.put("token", token);
        
        return respuesta;
    }
    
    // Login de usuario
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> respuesta = new HashMap<>();
        
        Optional<Usuario> usuarioOpt = usuarioDAO.obtenerPorUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar contraseña
            if (passwordEncoder.matches(password, usuario.getPasswordHash())) {
                // Actualizar último login
                usuario.setUltimoLogin(LocalDateTime.now());
                usuarioDAO.guardar(usuario);
                
                // Generar token JWT
                String token = jwtService.generateToken(username, usuario.getRol());
                
                respuesta.put("success", true);
                respuesta.put("mensaje", "Login exitoso");
                respuesta.put("token", token);
                respuesta.put("usuario", Map.of(
                    "id", usuario.getUsuarioId(),
                    "username", usuario.getUsername(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRol()
                ));
            } else {
                respuesta.put("success", false);
                respuesta.put("error", "Contraseña incorrecta");
            }
        } else {
            respuesta.put("success", false);
            respuesta.put("error", "Usuario no encontrado");
        }
        
        return respuesta;
    }
    
    // Verificar token
    public boolean validarToken(String token, String username) {
        return jwtService.validateToken(token, username);
    }
    
    // Verificar si usuario existe
    public boolean existeUsuario(String username) {
        return usuarioDAO.existeUsuario(username);
    }
}
