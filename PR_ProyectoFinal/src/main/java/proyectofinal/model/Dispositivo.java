//AreAri
//aqui va todo lo del dispositivo, ip, puertos, tipo, estado, etc
package proyectofinal.model;
import java.time.LocalDateTime;

public class Dispositivo {
    private int id;
    private String codigo;            
    private String nombre;            
    private String tipo;              
    private String descripcion;       
    private String ip;                //  192.168.1.100
    private String mac;               //  AA:BB:CC:DD:EE:FF
    private int puertoTCP;            
    private int puertoUDP;            
    private String estado;           
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimaConexion;
    private int usuarioId;           
    
    // el vacio
    public Dispositivo() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "desconectado";
        this.puertoTCP = 0;
        this.puertoUDP = 0;
    }
    
   
    public Dispositivo(String codigo, String nombre, String tipo) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
    }
    
    // Getters y Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
   
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
 
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getMac() {
        return mac;
    }
    
    public void setMac(String mac) {
        this.mac = mac;
    }
    
    public int getPuertoTCP() {
        return puertoTCP;
    }
    
    public void setPuertoTCP(int puertoTCP) {
        this.puertoTCP = puertoTCP;
    }
 
    public int getPuertoUDP() {
        return puertoUDP;
    }
    
    public void setPuertoUDP(int puertoUDP) {
        this.puertoUDP = puertoUDP;
    }
 
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
  
    public LocalDateTime getUltimaConexion() {
        return ultimaConexion;
    }
    
    public void setUltimaConexion(LocalDateTime ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }
    
    public int getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    // los extras 
    
    public boolean estaConectado() {
        return "conectado".equalsIgnoreCase(estado);
    }

    public boolean tieneError() {
        return "error".equalsIgnoreCase(estado);
    }

    public void conectar() {
        this.estado = "conectado";
        this.ultimaConexion = LocalDateTime.now();
    }
    
    public void desconectar() {
        this.estado = "desconectado";
    }

    public void marcarError() {
        this.estado = "error";
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s - IP: %s", nombre, codigo, tipo, ip != null ? ip : "Sin IP");
    }
}