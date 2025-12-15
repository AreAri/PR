/**
 * Módulo para comunicación con sockets TCP/UDP
 * Nota: En navegador solo podemos usar WebSockets, no TCP/UDP directos
 * Para este proyecto, simularemos la comunicación o usaremos un proxy
 */

class SocketManager {
    constructor() {
        this.tcpSocket = null;
        this.udpSocket = null;
        this.tcpMessages = [];
        this.udpMessages = [];
        this.isTCPConnected = false;
        this.isUDPConnected = false;
    }

    // ========== TCP SOCKET (simulado) ==========
    async connectTCP() {
        try {
            // En un navegador real, usaríamos WebSockets
            // Por ahora simulamos la conexión
            this.isTCPConnected = true;
            this.addTCPMessage('Conectado al servidor TCP en puerto 8081');
            this.addTCPMessage('Servidor: Bienvenido al servidor IoT - Proyecto Final PR');
            this.addTCPMessage('Servidor: Comandos disponibles: INFO, STATUS, DISCONNECT');
            return true;
        } catch (error) {
            this.addTCPMessage(`Error: ${error.message}`);
            return false;
        }
    }

    async sendTCPCommand(command) {
        if (!this.isTCPConnected) {
            this.addTCPMessage('Error: No conectado al servidor TCP');
            return;
        }

        this.addTCPMessage(`Cliente: ${command}`);

        // Simular respuesta del servidor
        setTimeout(() => {
            let respuesta = '';
            switch (command.toUpperCase()) {
                case 'INFO':
                    respuesta = 'Servidor IoT - Proyecto Final PR - Cliente: 127.0.0.1';
                    break;
                case 'STATUS':
                    respuesta = 'Estado: OK - Conexiones activas: varias';
                    break;
                case 'TIME':
                    respuesta = `Hora del servidor: ${new Date().toLocaleTimeString()}`;
                    break;
                case 'DISCONNECT':
                    respuesta = 'Desconectando... Adios!';
                    this.isTCPConnected = false;
                    break;
                default:
                    respuesta = `Comando no reconocido: ${command} - Comandos válidos: INFO, STATUS, TIME, DISCONNECT`;
            }
            this.addTCPMessage(`Servidor: ${respuesta}`);
        }, 500);
    }

    disconnectTCP() {
        this.isTCPConnected = false;
        this.addTCPMessage('Desconectado del servidor TCP');
    }

    addTCPMessage(message) {
        this.tcpMessages.push({
            timestamp: new Date().toISOString(),
            message: message
        });
        
        // Mantener solo los últimos 50 mensajes
        if (this.tcpMessages.length > 50) {
            this.tcpMessages.shift();
        }
        
        // Disparar evento
        this.dispatchEvent('tcpMessage', { message });
    }

    getTCPMessages() {
        return [...this.tcpMessages];
    }

    // ========== UDP SOCKET (simulado) ==========
    async sendUDPMessage(message) {
        this.addUDPMessage(`Enviado: ${message}`);

        // Simular respuesta del servidor UDP
        setTimeout(() => {
            let respuesta = '';
            if (message.startsWith('ALERTA:')) {
                respuesta = 'ALERTA_ACK';
            } else if (message.startsWith('STATUS:')) {
                respuesta = 'STATUS_OK';
            } else if (message === 'PING') {
                respuesta = 'PONG - Servidor UDP activo';
            } else {
                respuesta = `RECIBIDO: ${message}`;
            }
            this.addUDPMessage(`Recibido: ${respuesta}`);
        }, 300);
    }

    addUDPMessage(message) {
        this.udpMessages.push({
            timestamp: new Date().toISOString(),
            message: message
        });
        
        if (this.udpMessages.length > 50) {
            this.udpMessages.shift();
        }
        
        this.dispatchEvent('udpMessage', { message });
    }

    getUDPMessages() {
        return [...this.udpMessages];
    }

    // ========== EVENTOS ==========
    addEventListener(event, callback) {
        if (!this.listeners) this.listeners = {};
        if (!this.listeners[event]) this.listeners[event] = [];
        this.listeners[event].push(callback);
    }

    dispatchEvent(event, data) {
        if (this.listeners && this.listeners[event]) {
            this.listeners[event].forEach(callback => callback(data));
        }
    }

    // ========== VERIFICACIÓN DE CONEXIÓN ==========
    checkServerStatus() {
        return {
            tcp: this.isTCPConnected,
            udp: true, // UDP siempre "disponible" en simulación
            api: true   // API verificada por separado
        };
    }
}

// Exportar instancia global
const socketManager = new SocketManager();