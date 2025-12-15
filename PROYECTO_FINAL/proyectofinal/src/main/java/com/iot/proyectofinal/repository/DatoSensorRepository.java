package com.iot.proyectofinal.repository;

import com.iot.proyectofinal.model.DatoSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DatoSensorRepository extends JpaRepository<DatoSensor, Long> {
    
    // Buscar datos por dispositivo
    List<DatoSensor> findByDispositivoDispositivoId(Long dispositivoId);
    
    // Buscar datos por tipo de medida
    List<DatoSensor> findByTipoMedida(String tipoMedida);
    
    // Buscar ultimos N datos de un dispositivo
    List<DatoSensor> findTop10ByDispositivoDispositivoIdOrderByTimestampDesc(Long dispositivoId);
}