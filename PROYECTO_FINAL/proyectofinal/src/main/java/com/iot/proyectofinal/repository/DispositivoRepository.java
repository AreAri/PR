package com.iot.proyectofinal.repository;

import com.iot.proyectofinal.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    
    // Buscar dispositivos por tipo
    List<Dispositivo> findByTipo(String tipo);
    
    // Buscar dispositivos por estado
    List<Dispositivo> findByEstado(String estado);
    
    // Buscar por direccion IP
    Optional<Dispositivo> findByDireccionIp(String direccionIp);
    
    // Buscar dispositivos activos
    @Query("SELECT d FROM Dispositivo d WHERE d.estado = 'activo'")
    List<Dispositivo> findDispositivosActivos();
    
    // Buscar por nombre (contiene)
    List<Dispositivo> findByNombreContainingIgnoreCase(String nombre);
}