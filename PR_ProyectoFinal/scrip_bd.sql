--areari
-- Tabla de usuarios (
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,  -- Se hash con BCrypt desde Java o eso espero
    nombre VARCHAR(100),
    email VARCHAR(100),
    rol VARCHAR(20) DEFAULT 'usuario',    -- 'admin', 'usuario', 'tecnico'
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP,
    esta_activo BOOLEAN DEFAULT TRUE
);


-- Tabla de dispositivos
CREATE TABLE dispositivos (
    id_dispositivo SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,      
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,               -- 'sensor', 'actuador', 'camara', 'movil'
    descripcion TEXT,
    direccion_ip VARCHAR(15),
    direccion_mac VARCHAR(17),
    puerto_tcp INTEGER DEFAULT 0,
    puerto_udp INTEGER DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'desconectado', -- 'conectado', 'desconectado', 'error'
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_conexion TIMESTAMP,
    usuario_id INTEGER REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);

-- Tabla para logs de conexiones TCP/UDP
CREATE TABLE logs_tcp (
    id_log SERIAL PRIMARY KEY,
    dispositivo_codigo VARCHAR(50) NOT NULL,
    accion VARCHAR(50),                      -- 'conectar', 'desconectar', 'enviar', 'recibir'
    mensaje TEXT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exito BOOLEAN DEFAULT TRUE,
    direccion_ip_cliente VARCHAR(15),
    duracion_ms INTEGER                      
);

CREATE TABLE mensajes_udp (
    id_mensaje SERIAL PRIMARY KEY,
    dispositivo_origen VARCHAR(50) NOT NULL,
    dispositivo_destino VARCHAR(50),
    tipo VARCHAR(30) DEFAULT 'informacion',  -- 'alerta', 'informacion', 'warning', 'error'
    contenido TEXT NOT NULL,
    prioridad INTEGER DEFAULT 1,             -- 1 (baja) a 5 (crítica)
    leido BOOLEAN DEFAULT FALSE,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_recibido TIMESTAMP
);

-- aqui si admito, fue ia porque la verdad muchos problemas me estaba causando y me fio esta opcion
-- que nunca se me abria ocurrido la verdad porque no recordaba esta funcion 
CREATE INDEX idx_dispositivos_estado ON dispositivos(estado);
CREATE INDEX idx_dispositivos_usuario ON dispositivos(usuario_id);
CREATE INDEX idx_logs_fecha ON logs_tcp(fecha_hora DESC);
CREATE INDEX idx_logs_dispositivo ON logs_tcp(dispositivo_codigo);
CREATE INDEX idx_mensajes_prioridad ON mensajes_udp(prioridad DESC);
CREATE INDEX idx_mensajes_leido ON mensajes_udp(leido);

--los datos me los dio la ia porque no se me ocurrua que agregar
INSERT INTO dispositivos (codigo, nombre, tipo, descripcion, direccion_ip, estado) VALUES
('SENSOR-TEMP-01', 'Sensor de Temperatura', 'sensor', 'Mide temperatura ambiente en grados Celsius', '192.168.1.101', 'desconectado'),
('ACTUADOR-VENT-01', 'Ventilador Inteligente', 'actuador', 'Controla velocidad de ventilador según temperatura', '192.168.1.102', 'desconectado'),
('CAMARA-01', 'Cámara de Seguridad', 'camara', 'Cámara IP para vigilancia', '192.168.1.103', 'desconectado'),
('TABLET-CONTROL', 'Tablet de Control', 'movil', 'Dispositivo móvil para control remoto', '192.168.1.104', 'desconectado');