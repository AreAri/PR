//AreAri
//todo lo el usuario esta aqui, nom, contra, user rol y en teoria debo logar lo de el hast 
//porque reciclo codigo de otros proyectos(si falla es el hasheo)
package proyectofinal.model;
import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private String username;         
    private String passwordHash;      
    private String nombreCompleto;    
    private String email;             
    private String rol;              
    private LocalDateTime fechaRegistro; 
    private boolean activo;          
    
    // el vacio siempre 
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now(); 
        this.activo = true; 
    }
    
    public Usuario(String username, String nombreCompleto, String email) {
        this();
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = "usuario"; //es usuario por defecto porque no puedo crear un admi
    }
    
    //getters y setters (
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    // cosas extra porque asi puedo manejar roles y demas ademas de que fue un muy buen tip de los cursos que he tenido
    
    public boolean esAdministrador() {
        return "admin".equalsIgnoreCase(rol);
    }
    
    public boolean estaActivo() {
        return activo;
    }
    
    @Override
    public String toString() {
        return String.format("Usuario: %s (%s) - Rol: %s", username, nombreCompleto, rol);
    }
}