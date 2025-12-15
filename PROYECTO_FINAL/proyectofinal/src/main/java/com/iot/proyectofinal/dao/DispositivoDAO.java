package com.iot.proyectofinal.dao;

import com.iot.proyectofinal.model.Dispositivo;
import com.iot.proyectofinal.repository.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class DispositivoDAO {
    
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    // Obtener todos los dispositivos
    public List<Dispositivo> obtenerTodos() {
        return dispositivoRepository.findAll();
    }
    
    // Obtener dispositivo por ID
    public Optional<Dispositivo> obtenerPorId(Long id) {
        return dispositivoRepository.findById(id);
    }
    
    // Guardar dispositivo
    public Dispositivo guardar(Dispositivo dispositivo) {
        return dispositivoRepository.save(dispositivo);
    }
    
    // Eliminar dispositivo
    public void eliminar(Long id) {
        dispositivoRepository.deleteById(id);
    }
    
    // Obtener dispositivos por tipo
    public List<Dispositivo> obtenerPorTipo(String tipo) {
        return dispositivoRepository.findByTipo(tipo);
    }
    
    // Obtener dispositivos activos
    public List<Dispositivo> obtenerActivos() {
        return dispositivoRepository.findDispositivosActivos();
    }
    
    // Obtener dispositivo por IP
    public Optional<Dispositivo> obtenerPorIp(String ip) {
        return dispositivoRepository.findByDireccionIp(ip);
    }
    
    // Actualizar estado del dispositivo
    public Dispositivo actualizarEstado(Long id, String estado) {
        Optional<Dispositivo> dispositivoOpt = dispositivoRepository.findById(id);
        if (dispositivoOpt.isPresent()) {
            Dispositivo dispositivo = dispositivoOpt.get();
            dispositivo.setEstado(estado);
            return dispositivoRepository.save(dispositivo);
        }
        return null;
    }
    
    // Verificar si existe dispositivo
    public boolean existe(Long id) {
        return dispositivoRepository.existsById(id);
    }
}