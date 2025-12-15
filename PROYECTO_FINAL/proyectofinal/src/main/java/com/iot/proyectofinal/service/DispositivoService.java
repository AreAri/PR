package com.iot.proyectofinal.service;

import com.iot.proyectofinal.dao.DispositivoDAO;
import com.iot.proyectofinal.model.Dispositivo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DispositivoService {
    
    @Autowired
    private DispositivoDAO dispositivoDAO;
    
    // Obtener todos los dispositivos
    public List<Dispositivo> obtenerTodosDispositivos() {
        return dispositivoDAO.obtenerTodos();
    }
    
    // Obtener dispositivo por ID
    public Optional<Dispositivo> obtenerDispositivoPorId(Long id) {
        return dispositivoDAO.obtenerPorId(id);
    }
    
    // Crear nuevo dispositivo
    public Dispositivo crearDispositivo(Dispositivo dispositivo) {
        // Validaciones basicas
        if (dispositivo.getNombre() == null || dispositivo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del dispositivo es requerido");
        }
        
        if (dispositivo.getTipo() == null || dispositivo.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo del dispositivo es requerido");
        }
        
        if (dispositivo.getDireccionIp() == null || dispositivo.getDireccionIp().trim().isEmpty()) {
            throw new IllegalArgumentException("La direccion IP es requerida");
        }
        
        // Establecer valores por defecto si no vienen
        if (dispositivo.getEstado() == null) {
            dispositivo.setEstado("inactivo");
        }
        
        return dispositivoDAO.guardar(dispositivo);
    }
    
    // Actualizar dispositivo
    public Dispositivo actualizarDispositivo(Long id, Dispositivo dispositivoActualizado) {
        Optional<Dispositivo> dispositivoExistenteOpt = dispositivoDAO.obtenerPorId(id);
        
        if (dispositivoExistenteOpt.isPresent()) {
            Dispositivo dispositivoExistente = dispositivoExistenteOpt.get();
            
            // Actualizar campos permitidos
            if (dispositivoActualizado.getNombre() != null) {
                dispositivoExistente.setNombre(dispositivoActualizado.getNombre());
            }
            
            if (dispositivoActualizado.getTipo() != null) {
                dispositivoExistente.setTipo(dispositivoActualizado.getTipo());
            }
            
            if (dispositivoActualizado.getDireccionIp() != null) {
                dispositivoExistente.setDireccionIp(dispositivoActualizado.getDireccionIp());
            }
            
            if (dispositivoActualizado.getEstado() != null) {
                dispositivoExistente.setEstado(dispositivoActualizado.getEstado());
            }
            
            if (dispositivoActualizado.getUbicacion() != null) {
                dispositivoExistente.setUbicacion(dispositivoActualizado.getUbicacion());
            }
            
            if (dispositivoActualizado.getPuerto() != null) {
                dispositivoExistente.setPuerto(dispositivoActualizado.getPuerto());
            }
            
            return dispositivoDAO.guardar(dispositivoExistente);
        }
        
        throw new IllegalArgumentException("Dispositivo no encontrado con ID: " + id);
    }
    
    // Eliminar dispositivo
    public boolean eliminarDispositivo(Long id) {
        if (dispositivoDAO.existe(id)) {
            dispositivoDAO.eliminar(id);
            return true;
        }
        return false;
    }
    
    // Obtener dispositivos por tipo
    public List<Dispositivo> obtenerDispositivosPorTipo(String tipo) {
        return dispositivoDAO.obtenerPorTipo(tipo);
    }
    
    // Obtener dispositivos activos
    public List<Dispositivo> obtenerDispositivosActivos() {
        return dispositivoDAO.obtenerActivos();
    }
    
    // Registrar conexion de dispositivo
    public Dispositivo registrarConexion(Long dispositivoId, String ip) {
        Optional<Dispositivo> dispositivoOpt = dispositivoDAO.obtenerPorId(dispositivoId);
        
        if (dispositivoOpt.isPresent()) {
            Dispositivo dispositivo = dispositivoOpt.get();
            dispositivo.setUltimaConexion(LocalDateTime.now());
            dispositivo.setEstado("activo");
            dispositivo.setDireccionIp(ip);
            return dispositivoDAO.guardar(dispositivo);
        }
        
        throw new IllegalArgumentException("Dispositivo no encontrado con ID: " + dispositivoId);
    }
    
    // Cambiar estado del dispositivo
    public Dispositivo cambiarEstado(Long dispositivoId, String nuevoEstado) {
        return dispositivoDAO.actualizarEstado(dispositivoId, nuevoEstado);
    }
}