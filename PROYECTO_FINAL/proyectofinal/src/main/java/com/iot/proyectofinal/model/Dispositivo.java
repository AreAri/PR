package com.iot.proyectofinal.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivos")
public class Dispositivo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispositivo_id")
    private Long dispositivoId;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo; // sensor, actuador, camara, termometro, otros
    
    @Column(name = "modelo", length = 100)
    private String modelo;
    
    @Column(name = "fabricante", length = 100)
    private String fabricante;
    
    @Column(name = "direccion_ip", nullable = false)
    private String direccionIp;
    
    @Column(name = "puerto")
    private Integer puerto;
    
    @Column(name = "estado", length = 20)
    private String estado = "inactivo"; // activo, inactivo, mantenimiento, error
    
    @Column(name = "ubicacion", length = 200)
    private String ubicacion;
    
    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion = LocalDate.now();
    
    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion;
    
    // Relacion con Usuario (muchos dispositivos pueden pertenecer a un usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    public Dispositivo() {
    }
    
    public Dispositivo(String nombre, String tipo, String direccionIp) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccionIp = direccionIp;
    }
    
    // Getters y Setters
    public Long getDispositivoId() {
        return dispositivoId;
    }
    
    public void setDispositivoId(Long dispositivoId) {
        this.dispositivoId = dispositivoId;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getModelo() {
        return modelo;
    }
    
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    
    public String getFabricante() {
        return fabricante;
    }
    
    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }
    
    public String getDireccionIp() {
        return direccionIp;
    }
    
    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }
    
    public Integer getPuerto() {
        return puerto;
    }
    
    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public LocalDate getFechaInstalacion() {
        return fechaInstalacion;
    }
    
    public void setFechaInstalacion(LocalDate fechaInstalacion) {
        this.fechaInstalacion = fechaInstalacion;
    }
    
    public LocalDateTime getUltimaConexion() {
        return ultimaConexion;
    }
    
    public void setUltimaConexion(LocalDateTime ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    // Metodo para actualizar conexion
    public void actualizarConexion() {
        this.ultimaConexion = LocalDateTime.now();
        this.estado = "activo";
    }
    
    @Override
    public String toString() {
        return "Dispositivo{id=" + dispositivoId + ", nombre='" + nombre + "', tipo='" + tipo + "'}";
    }
}