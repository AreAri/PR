--areari
-- Tabla de usuarios (
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. TABLA USUARIOS
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    token_version INTEGER DEFAULT 0
);

-- 2. TABLA DISPOSITIVOS
CREATE TABLE devices (
    device_id SERIAL PRIMARY KEY,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL CHECK (device_type IN ('SENSOR', 'ACTUATOR', 'GATEWAY', 'MOBILE', 'OTHER')),
    ip_address VARCHAR(45) NOT NULL,
    mac_address VARCHAR(17) UNIQUE,
    status VARCHAR(20) DEFAULT 'OFFLINE' CHECK (status IN ('ONLINE', 'OFFLINE', 'MAINTENANCE', 'ERROR')),
    last_seen TIMESTAMP,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    owner_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. TABLA CONEXIONES
CREATE TABLE device_connections (
    connection_id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES devices(device_id) ON DELETE CASCADE,
    connection_type VARCHAR(10) CHECK (connection_type IN ('TCP', 'UDP', 'REST')),
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    data_transferred BIGINT DEFAULT 0,
    status_code INTEGER,
    error_message TEXT
);

-- 4. TABLA NOTIFICACIONES
CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES devices(device_id) ON DELETE CASCADE,
    notification_type VARCHAR(30) NOT NULL CHECK (notification_type IN ('ALERT', 'WARNING', 'INFO', 'UPDATE', 'STATUS_CHANGE')),
    message TEXT NOT NULL,
    priority INTEGER DEFAULT 1 CHECK (priority BETWEEN 1 AND 5),
    sent_via VARCHAR(10) DEFAULT 'UDP' CHECK (sent_via IN ('UDP', 'TCP', 'REST')),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. TABLA MÉTRICAS
CREATE TABLE device_metrics (
    metric_id SERIAL PRIMARY KEY,
    device_id INTEGER REFERENCES devices(device_id) ON DELETE CASCADE,
    metric_name VARCHAR(50) NOT NULL,
    metric_value DECIMAL(15, 4),
    unit VARCHAR(20),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. TABLA LOGS DEL SISTEMA
CREATE TABLE system_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE SET NULL,
    device_id INTEGER REFERENCES devices(device_id) ON DELETE SET NULL,
    log_level VARCHAR(10) CHECK (log_level IN ('DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL')),
    action VARCHAR(100) NOT NULL,
    description TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. TABLA CONFIGURACIÓN
CREATE TABLE configuration (
    config_id SERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    config_type VARCHAR(20) DEFAULT 'STRING' CHECK (config_type IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON')),
    description TEXT,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by INTEGER REFERENCES users(user_id) ON DELETE SET NULL
);

-- Índices para users
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);

-- Índices para devices
CREATE INDEX idx_devices_owner ON devices(owner_id);
CREATE INDEX idx_devices_status ON devices(status);
CREATE INDEX idx_devices_type ON devices(device_type);
CREATE INDEX idx_devices_last_seen ON devices(last_seen);

-- Índices para connections
CREATE INDEX idx_connections_device ON device_connections(device_id);
CREATE INDEX idx_connections_time ON device_connections(start_time);
CREATE INDEX idx_connections_type ON device_connections(connection_type);
CREATE INDEX idx_connections_status ON device_connections(status_code);

-- Índices para notifications
CREATE INDEX idx_notifications_device ON notifications(device_id);
CREATE INDEX idx_notifications_type ON notifications(notification_type);
CREATE INDEX idx_notifications_created ON notifications(created_at);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_notifications_priority ON notifications(priority);

-- Índices para metrics
CREATE INDEX idx_metrics_device_time ON device_metrics(device_id, recorded_at);
CREATE INDEX idx_metrics_name ON device_metrics(metric_name);

-- Índices para logs
CREATE INDEX idx_logs_user ON system_logs(user_id);
CREATE INDEX idx_logs_device ON system_logs(device_id);
CREATE INDEX idx_logs_level ON system_logs(log_level);
CREATE INDEX idx_logs_created ON system_logs(created_at);

-- ============================================
-- DATOS INICIALES (SEED DATA)
-- ============================================

-- Insertar usuario administrador por defecto (contraseña: Admin123)
INSERT INTO users (username, email, password_hash, role) 
VALUES ('admin', 'admin@rdm.com', '$2a$10$YourBcryptHashHere', 'ADMIN');

-- Insertar configuraciones por defecto
INSERT INTO configuration (config_key, config_value, config_type, description) VALUES
('tcp_port', '8081', 'INTEGER', 'Puerto TCP para conexiones de dispositivos'),
('udp_port', '8082', 'INTEGER', 'Puerto UDP para notificaciones'),
('session_timeout', '1800', 'INTEGER', 'Tiempo de sesión en segundos'),
('max_devices_per_user', '10', 'INTEGER', 'Máximo de dispositivos por usuario'),
('notification_retention_days', '30', 'INTEGER', 'Días de retención de notificaciones');

-- Función para actualizar automáticamente updated_at en devices
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para devices
CREATE TRIGGER update_devices_updated_at 
    BEFORE UPDATE ON devices 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Función para registrar logs automáticamente
CREATE OR REPLACE FUNCTION log_device_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO system_logs (device_id, log_level, action, description)
        VALUES (NEW.device_id, 'INFO', 
                'STATUS_CHANGE', 
                'Dispositivo ' || NEW.device_name || ' cambió de ' || OLD.status || ' a ' || NEW.status);
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para cambios de estado en devices
CREATE TRIGGER log_device_status 
    AFTER UPDATE OF status ON devices 
    FOR EACH ROW 
    EXECUTE FUNCTION log_device_status_change();


-- Vista para dispositivos en línea
CREATE VIEW online_devices AS
SELECT d.device_id, d.device_name, d.device_type, d.ip_address, 
       u.username as owner, d.last_seen
FROM devices d
JOIN users u ON d.owner_id = u.user_id
WHERE d.status = 'ONLINE'
AND d.last_seen > CURRENT_TIMESTAMP - INTERVAL '5 minutes';

-- Vista para notificaciones no leídas
CREATE VIEW unread_notifications AS
SELECT n.notification_id, d.device_name, n.notification_type, 
       n.message, n.priority, n.created_at
FROM notifications n
JOIN devices d ON n.device_id = d.device_id
WHERE n.is_read = FALSE
ORDER BY n.priority DESC, n.created_at DESC;

-- Vista para estadísticas de dispositivos
CREATE VIEW device_stats AS
SELECT 
    d.device_type,
    COUNT(*) as total_devices,
    SUM(CASE WHEN d.status = 'ONLINE' THEN 1 ELSE 0 END) as online_devices,
    AVG(EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - d.last_seen))/60) as avg_minutes_offline
FROM devices d
GROUP BY d.device_type;


COMMENT ON TABLE users IS 'Usuarios del sistema con roles de acceso';
COMMENT ON TABLE devices IS 'Dispositivos remotos gestionados por el sistema';
COMMENT ON TABLE device_connections IS 'Historial de conexiones de dispositivos';
COMMENT ON TABLE notifications IS 'Notificaciones enviadas via UDP/TCP';
COMMENT ON TABLE device_metrics IS 'Métricas y telemetría de dispositivos';
COMMENT ON TABLE system_logs IS 'Logs de auditoría del sistema';
COMMENT ON TABLE configuration IS 'Configuración del sistema';

SELECT 'Base de datos RDM creada exitosamente' as message;
