package com.iot.proyectofinal.model;

import jakarta.persistence.*;
import java.math.BigDecimal;  
import java.time.LocalDateTime;

@Entity
@Table(name = "datos_sensor")
public class DatoSensor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dato_id")
    private Long datoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
    
    @Column(name = "tipo_medida", nullable = false, length = 50)
    private String tipoMedida;
    
    @Column(name = "valor", nullable = false, precision = 10, scale = 4)
    private BigDecimal valor;  // <-- CAMBIO AQUÍ
    
    @Column(name = "unidad", length = 20)
    private String unidad = "N/A";
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(name = "calidad_dato")
    private Integer calidadDato;
    
    public DatoSensor() {
    }
    
    // Actualiza el constructor
    public DatoSensor(Dispositivo dispositivo, String tipoMedida, BigDecimal valor) {
        this.dispositivo = dispositivo;
        this.tipoMedida = tipoMedida;
        this.valor = valor;
    }
    
    // Constructor con double para facilitar (opcional)
    public DatoSensor(Dispositivo dispositivo, String tipoMedida, double valor) {
        this.dispositivo = dispositivo;
        this.tipoMedida = tipoMedida;
        this.valor = BigDecimal.valueOf(valor);
    }
    
    // Getters y Setters actualizados
    public Long getDatoId() {
        return datoId;
    }
    
    public void setDatoId(Long datoId) {
        this.datoId = datoId;
    }
    
    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    
    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
    
    public String getTipoMedida() {
        return tipoMedida;
    }
    
    public void setTipoMedida(String tipoMedida) {
        this.tipoMedida = tipoMedida;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    // Método auxiliar para obtener como double
    public double getValorAsDouble() {
        return valor != null ? valor.doubleValue() : 0.0;
    }
    
    // Método auxiliar para establecer desde double
    public void setValorFromDouble(double valor) {
        this.valor = BigDecimal.valueOf(valor);
    }
    
    public String getUnidad() {
        return unidad;
    }
    
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getCalidadDato() {
        return calidadDato;
    }
    
    public void setCalidadDato(Integer calidadDato) {
        this.calidadDato = calidadDato;
    }
    
    @Override
    public String toString() {
        return "DatoSensor{id=" + datoId + ", tipo='" + tipoMedida + "', valor=" + valor + "}";
    }
}