//AreAri
//aqui va todo lo del mensaje que se envia entre cliente y servidor
package proyectofinal.model;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum TipoMensaje {
        LOGIN,               
        LOGOUT,              
        REGISTRAR,          
        CONECTAR,            
        DESCONECTAR,        
        ENVIAR_COMANDO,      
        CONSULTAR,          
        NOTIFICACION,       
        ERROR,               
        RESPUESTA_OK,       
        PING               
    }
    
    //los atributos del mensaje en si 
    private TipoMensaje tipo;
    private String remitente;      
    private String destinatario;    
    private Object contenido;       
    private LocalDateTime fechaHora;
    private boolean necesitaRespuesta; 
    private int idMensaje;          
    
    // vacio
    public Mensaje() {
        this.fechaHora = LocalDateTime.now();
        this.necesitaRespuesta = false;
    }
    
    public Mensaje(TipoMensaje tipo, String remitente) {
        this();
        this.tipo = tipo;
        this.remitente = remitente;
    }
    
    public Mensaje(TipoMensaje tipo, String remitente, Object contenido) {
        this(tipo, remitente);
        this.contenido = contenido;
    }
    
    // Getters y Setters
    
    public TipoMensaje getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoMensaje tipo) {
        this.tipo = tipo;
    }
    
    public String getRemitente() {
        return remitente;
    }
    
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }
    
    public String getDestinatario() {
        return destinatario;
    }
    
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public Object getContenido() {
        return contenido;
    }
    
    public void setContenido(Object contenido) {
        this.contenido = contenido;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public boolean isNecesitaRespuesta() {
        return necesitaRespuesta;
    }
    
    public void setNecesitaRespuesta(boolean necesitaRespuesta) {
        this.necesitaRespuesta = necesitaRespuesta;
    }
    
    public int getIdMensaje() {
        return idMensaje;
    }
    
    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }
    
    // extras
    
    public boolean esError() {
        return tipo == TipoMensaje.ERROR;
    }

    public boolean esLogin() {
        return tipo == TipoMensaje.LOGIN;
    }

    public Mensaje crearRespuesta(Object contenido) {
        Mensaje respuesta = new Mensaje(TipoMensaje.RESPUESTA_OK, "servidor", contenido);
        respuesta.setDestinatario(this.remitente);
        respuesta.setIdMensaje(this.idMensaje); // Mismo ID para correlacionar
        return respuesta;
    }

    public Mensaje crearError(String mensajeError) {
        Mensaje error = new Mensaje(TipoMensaje.ERROR, "servidor", mensajeError);
        error.setDestinatario(this.remitente);
        error.setIdMensaje(this.idMensaje);
        return error;
    }
    
    @Override
    public String toString() {
        String dest = destinatario != null ? destinatario : "Todos";
        return String.format("[%s] De: %s -> Para: %s | Contenido: %s", tipo, remitente, dest, contenido);
    }
}