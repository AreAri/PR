package com.iot.proyectofinal.controller;
import com.iot.proyectofinal.model.Dispositivo;
import com.iot.proyectofinal.service.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {
    
    @Autowired
    private DispositivoService dispositivoService;
    
    // GET /api/dispositivos - Obtener todos los dispositivos
    @GetMapping
    public ResponseEntity<List<Dispositivo>> obtenerTodosDispositivos() {
        List<Dispositivo> dispositivos = dispositivoService.obtenerTodosDispositivos();
        return ResponseEntity.ok(dispositivos);
    }
    
    // GET /api/dispositivos/{id} - Obtener dispositivo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDispositivoPorId(@PathVariable Long id) {
        try {
           Optional<Dispositivo> dispositivoOpt = dispositivoService.obtenerDispositivoPorId(id);
            if (dispositivoOpt.isPresent()) {
                return ResponseEntity.ok(dispositivoOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearError("Dispositivo no encontrado con ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al obtener dispositivo: " + e.getMessage()));
        }
    }
    
    // POST /api/dispositivos - Crear nuevo dispositivo
    @PostMapping
    public ResponseEntity<?> crearDispositivo(@RequestBody Dispositivo dispositivo) {
        try {
            Dispositivo nuevoDispositivo = dispositivoService.crearDispositivo(dispositivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDispositivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al crear dispositivo: " + e.getMessage()));
        }
    }
    
    // PUT /api/dispositivos/{id} - Actualizar dispositivo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDispositivo(@PathVariable Long id, @RequestBody Dispositivo dispositivo) {
        try {
            Dispositivo dispositivoActualizado = dispositivoService.actualizarDispositivo(id, dispositivo);
            return ResponseEntity.ok(dispositivoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al actualizar dispositivo: " + e.getMessage()));
        }
    }
    
    // DELETE /api/dispositivos/{id} - Eliminar dispositivo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDispositivo(@PathVariable Long id) {
        try {
            boolean eliminado = dispositivoService.eliminarDispositivo(id);
            if (eliminado) {
                return ResponseEntity.ok(crearMensaje("Dispositivo eliminado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(crearError("Dispositivo no encontrado con ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al eliminar dispositivo: " + e.getMessage()));
        }
    }
    
    // GET /api/dispositivos/tipo/{tipo} - Obtener dispositivos por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> obtenerDispositivosPorTipo(@PathVariable String tipo) {
        try {
            List<Dispositivo> dispositivos = dispositivoService.obtenerDispositivosPorTipo(tipo);
            return ResponseEntity.ok(dispositivos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al obtener dispositivos por tipo: " + e.getMessage()));
        }
    }
    
    // GET /api/dispositivos/activos - Obtener dispositivos activos
    @GetMapping("/activos")
    public ResponseEntity<?> obtenerDispositivosActivos() {
        try {
            List<Dispositivo> dispositivos = dispositivoService.obtenerDispositivosActivos();
            return ResponseEntity.ok(dispositivos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al obtener dispositivos activos: " + e.getMessage()));
        }
    }
    
    // PUT /api/dispositivos/{id}/conectar - Registrar conexion de dispositivo
    @PutMapping("/{id}/conectar")
    public ResponseEntity<?> registrarConexion(@PathVariable Long id, @RequestParam String ip) {
        try {
            Dispositivo dispositivo = dispositivoService.registrarConexion(id, ip);
            return ResponseEntity.ok(dispositivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al registrar conexion: " + e.getMessage()));
        }
    }
    
    // PUT /api/dispositivos/{id}/estado - Cambiar estado del dispositivo
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        try {
            Dispositivo dispositivo = dispositivoService.cambiarEstado(id, estado);
            return ResponseEntity.ok(dispositivo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearError("Error al cambiar estado: " + e.getMessage()));
        }
    }
    
    // GET /api/dispositivos/test - Endpoint de prueba
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "API de dispositivos funcionando correctamente");
        respuesta.put("version", "1.0.0");
        respuesta.put("proyecto", "Proyecto Final PR - Sistema IoT");
        respuesta.put("estudiante", "Nombre del Estudiante");
        return ResponseEntity.ok(respuesta);
    }
    
    // Metodos auxiliares para respuestas
    private Map<String, String> crearError(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return error;
    }
    
    private Map<String, String> crearMensaje(String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}