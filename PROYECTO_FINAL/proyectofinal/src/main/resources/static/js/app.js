/**
 * Aplicación principal del cliente web
 */

class IoTApp {
    constructor() {
        this.currentDeviceId = null;
        this.devices = [];
        this.init();
    }

    async init() {
        // Verificar autenticación
        if (apiClient.isAuthenticated()) {
            this.showDashboard();
            await this.loadDevices();
            await this.updateStats();
        } else {
            this.showLogin();
        }

        this.setupEventListeners();
        this.checkAPIStatus();
    }

    // ========== MANEJO DE VISTAS ==========
    showLogin() {
        document.getElementById('login-section').style.display = 'block';
        document.getElementById('dashboard').style.display = 'none';
    }

    showDashboard() {
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('dashboard').style.display = 'block';
    }

    showSection(sectionId) {
        // Ocultar todas las secciones
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // Desactivar todos los botones de navegación
        document.querySelectorAll('.nav-btn').forEach(btn => {
            btn.classList.remove('active');
        });

        // Mostrar sección seleccionada
        const section = document.getElementById(sectionId);
        if (section) {
            section.classList.add('active');
        }

        // Activar botón correspondiente
        const navBtn = document.querySelector(`[data-section="${sectionId.replace('-section', '')}"]`);
        if (navBtn) {
            navBtn.classList.add('active');
        }
    }

    // ========== DISPOSITIVOS ==========
    async loadDevices() {
        try {
            const devicesList = document.getElementById('devices-list');
            devicesList.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i> Cargando dispositivos...</div>';

            this.devices = await apiClient.getDispositivos();
            this.renderDevices(this.devices);
            await this.updateStats();
        } catch (error) {
            this.showMessage('Error al cargar dispositivos', 'error');
        }
    }

    renderDevices(devices) {
        const devicesList = document.getElementById('devices-list');
        
        if (!devices || devices.length === 0) {
            devicesList.innerHTML = '<div class="message info">No hay dispositivos registrados</div>';
            return;
        }

        // Aplicar filtros
        const tipoFilter = document.getElementById('filter-type').value;
        const estadoFilter = document.getElementById('filter-status').value;

        const filteredDevices = devices.filter(device => {
            if (tipoFilter && device.tipo !== tipoFilter) return false;
            if (estadoFilter && device.estado !== estadoFilter) return false;
            return true;
        });

        if (filteredDevices.length === 0) {
            devicesList.innerHTML = '<div class="message info">No hay dispositivos con los filtros seleccionados</div>';
            return;
        }

        devicesList.innerHTML = filteredDevices.map(device => `
            <div class="device-card" data-id="${device.dispositivoId}">
                <div class="device-header">
                    <h3 class="device-name">${device.nombre}</h3>
                    <span class="device-status status-${device.estado}">${device.estado}</span>
                </div>
                <div class="device-info">
                    <div class="device-info-item">
                        <i class="fas fa-cogs"></i>
                        <span>Tipo: ${device.tipo}</span>
                    </div>
                    <div class="device-info-item">
                        <i class="fas fa-network-wired"></i>
                        <span>IP: ${device.direccionIp}</span>
                    </div>
                    <div class="device-info-item">
                        <i class="fas fa-map-marker-alt"></i>
                        <span>Ubicación: ${device.ubicacion || 'No especificada'}</span>
                    </div>
                    <div class="device-info-item">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Instalado: ${new Date(device.fechaInstalacion).toLocaleDateString()}</span>
                    </div>
                </div>
            </div>
        `).join('');

        // Agregar event listeners a las tarjetas
        document.querySelectorAll('.device-card').forEach(card => {
            card.addEventListener('click', (e) => {
                if (!e.target.closest('button')) {
                    const deviceId = card.dataset.id;
                    this.showDeviceModal(deviceId);
                }
            });
        });
    }

    async showDeviceModal(deviceId) {
        try {
            const device = await apiClient.getDispositivo(deviceId);
            this.currentDeviceId = deviceId;

            // Actualizar modal
            document.getElementById('modal-title').textContent = device.nombre;
            
            const modalBody = document.getElementById('modal-body');
            modalBody.innerHTML = `
                <div class="device-details">
                    <div class="detail-item">
                        <strong>Tipo:</strong> ${device.tipo}
                    </div>
                    <div class="detail-item">
                        <strong>IP:</strong> ${device.direccionIp}
                    </div>
                    <div class="detail-item">
                        <strong>Puerto:</strong> ${device.puerto || 'No especificado'}
                    </div>
                    <div class="detail-item">
                        <strong>Ubicación:</strong> ${device.ubicacion || 'No especificada'}
                    </div>
                    <div class="detail-item">
                        <strong>Estado:</strong> ${device.estado}
                    </div>
                    <div class="detail-item">
                        <strong>Modelo:</strong> ${device.modelo || 'No especificado'}
                    </div>
                    <div class="detail-item">
                        <strong>Fabricante:</strong> ${device.fabricante || 'No especificado'}
                    </div>
                    <div class="detail-item">
                        <strong>Última conexión:</strong> ${device.ultimaConexion ? new Date(device.ultimaConexion).toLocaleString() : 'Nunca'}
                    </div>
                </div>
            `;

            // Mostrar modal
            document.getElementById('device-modal').style.display = 'flex';
        } catch (error) {
            this.showMessage('Error al cargar dispositivo', 'error');
        }
    }

    async addDevice(deviceData) {
        try {
            await apiClient.createDispositivo(deviceData);
            this.showMessage('Dispositivo creado exitosamente', 'success');
            await this.loadDevices();
            this.showSection('devices-section');
            document.getElementById('add-device-form').reset();
        } catch (error) {
            this.showMessage(`Error al crear dispositivo: ${error.message}`, 'error');
        }
    }

    async updateDevice(deviceId, deviceData) {
        try {
            await apiClient.updateDispositivo(deviceId, deviceData);
            this.showMessage('Dispositivo actualizado exitosamente', 'success');
            await this.loadDevices();
            this.closeModal();
        } catch (error) {
            this.showMessage(`Error al actualizar dispositivo: ${error.message}`, 'error');
        }
    }

    async deleteDevice(deviceId) {
        if (!confirm('¿Está seguro de eliminar este dispositivo?')) return;

        try {
            await apiClient.deleteDispositivo(deviceId);
            this.showMessage('Dispositivo eliminado exitosamente', 'success');
            await this.loadDevices();
            this.closeModal();
        } catch (error) {
            this.showMessage(`Error al eliminar dispositivo: ${error.message}`, 'error');
        }
    }

    async connectDevice(deviceId, ip) {
        try {
            await apiClient.conectarDispositivo(deviceId, ip);
            this.showMessage('Dispositivo conectado exitosamente', 'success');
            await this.loadDevices();
            this.closeModal();
        } catch (error) {
            this.showMessage(`Error al conectar dispositivo: ${error.message}`, 'error');
        }
    }

    async changeDeviceStatus(deviceId, status) {
        try {
            await apiClient.cambiarEstadoDispositivo(deviceId, status);
            this.showMessage(`Estado cambiado a: ${status}`, 'success');
            await this.loadDevices();
            this.closeModal();
        } catch (error) {
            this.showMessage(`Error al cambiar estado: ${error.message}`, 'error');
        }
    }

    // ========== ESTADÍSTICAS ==========
    async updateStats() {
        try {
            const stats = await apiClient.getEstadisticas();
            
            document.getElementById('total-devices').textContent = stats.total;
            document.getElementById('active-devices').textContent = stats.activos;
            document.getElementById('inactive-devices').textContent = stats.inactivos;
            document.getElementById('error-devices').textContent = stats.errores;

            // Actualizar gráfico
            this.updateChart(stats.porTipo);
        } catch (error) {
            console.error('Error al cargar estadísticas:', error);
        }
    }

    updateChart(typeData) {
        const ctx = document.getElementById('type-chart').getContext('2d');
        
        // Destruir gráfico existente si hay uno
        if (this.chart) {
            this.chart.destroy();
        }

        const labels = Object.keys(typeData);
        const data = Object.values(typeData);
        const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe'];

        this.chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: colors,
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // ========== SOCKETS ==========
    setupSocketListeners() {
        // Actualizar terminal TCP
        socketManager.addEventListener('tcpMessage', (data) => {
            const tcpMessages = socketManager.getTCPMessages();
            const tcpTerminal = document.getElementById('tcp-messages');
            tcpTerminal.innerHTML = tcpMessages.map(msg => 
                `<div>${new Date(msg.timestamp).toLocaleTimeString()}: ${msg.message}</div>`
            ).join('');
            tcpTerminal.scrollTop = tcpTerminal.scrollHeight;
        });

        // Actualizar terminal UDP
        socketManager.addEventListener('udpMessage', (data) => {
            const udpMessages = socketManager.getUDPMessages();
            const udpTerminal = document.getElementById('udp-messages');
            udpTerminal.innerHTML = udpMessages.map(msg => 
                `<div>${new Date(msg.timestamp).toLocaleTimeString()}: ${msg.message}</div>`
            ).join('');
            udpTerminal.scrollTop = udpTerminal.scrollHeight;
        });
    }

    // ========== EVENT LISTENERS ==========
    setupEventListeners() {
        // Login
        document.getElementById('login-btn').addEventListener('click', async () => {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;

            if (!username || !password) {
                this.showMessage('Usuario y contraseña son requeridos', 'error', 'login-message');
                return;
            }

            try {
                await apiClient.login(username, password);
                this.showDashboard();
                await this.loadDevices();
                this.setupSocketListeners();
                this.showMessage('Login exitoso', 'success', 'login-message');
            } catch (error) {
                this.showMessage('Credenciales incorrectas', 'error', 'login-message');
            }
        });

        // Logout
        document.getElementById('logout-btn').addEventListener('click', () => {
            apiClient.logout();
            this.showLogin();
        });

        // Navegación
        document.querySelectorAll('.nav-btn[data-section]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                if (!e.target.closest('.btn-logout')) {
                    const section = e.target.closest('.nav-btn').dataset.section;
                    this.showSection(`${section}-section`);
                }
            });
        });

        // Dispositivos - Filtros
        document.getElementById('filter-type').addEventListener('change', () => {
            this.renderDevices(this.devices);
        });

        document.getElementById('filter-status').addEventListener('change', () => {
            this.renderDevices(this.devices);
        });

        document.getElementById('refresh-devices').addEventListener('click', () => {
            this.loadDevices();
        });

        // Formulario de agregar dispositivo
        document.getElementById('add-device-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const deviceData = {
                nombre: document.getElementById('device-name').value,
                tipo: document.getElementById('device-type').value,
                direccionIp: document.getElementById('device-ip').value,
                puerto: document.getElementById('device-port').value ? parseInt(document.getElementById('device-port').value) : null,
                ubicacion: document.getElementById('device-location').value,
                estado: document.getElementById('device-status').value,
                modelo: document.getElementById('device-model').value || null
            };

            await this.addDevice(deviceData);
        });

        // Sockets
        document.getElementById('tcp-connect-btn').addEventListener('click', async () => {
            const btn = document.getElementById('tcp-connect-btn');
            if (!socketManager.isTCPConnected) {
                await socketManager.connectTCP();
                btn.innerHTML = '<i class="fas fa-plug"></i> Desconectar';
                btn.classList.add('btn-danger');
            } else {
                socketManager.disconnectTCP();
                btn.innerHTML = '<i class="fas fa-plug"></i> Conectar';
                btn.classList.remove('btn-danger');
            }
        });

        document.getElementById('send-tcp-btn').addEventListener('click', () => {
            const command = document.getElementById('tcp-command').value;
            if (command) {
                socketManager.sendTCPCommand(command);
                document.getElementById('tcp-command').value = '';
            }
        });

        document.getElementById('tcp-command').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                document.getElementById('send-tcp-btn').click();
            }
        });

        document.getElementById('udp-test-btn').addEventListener('click', () => {
            socketManager.sendUDPMessage('PING');
        });

        document.getElementById('send-udp-btn').addEventListener('click', () => {
            const message = document.getElementById('udp-message').value;
            if (message) {
                socketManager.sendUDPMessage(message);
                document.getElementById('udp-message').value = '';
            }
        });

        document.getElementById('udp-message').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                document.getElementById('send-udp-btn').click();
            }
        });

        // Modal
        document.querySelectorAll('.close-modal').forEach(btn => {
            btn.addEventListener('click', () => this.closeModal());
        });

        document.getElementById('update-device-btn').addEventListener('click', () => {
            // Implementar actualización de dispositivo
            this.showMessage('Funcionalidad en desarrollo', 'info');
        });

        document.getElementById('delete-device-btn').addEventListener('click', async () => {
            if (this.currentDeviceId) {
                await this.deleteDevice(this.currentDeviceId);
            }
        });

        document.getElementById('connect-device-btn').addEventListener('click', async () => {
            if (this.currentDeviceId) {
                const ip = prompt('Ingrese la IP para conectar:');
                if (ip) {
                    await this.connectDevice(this.currentDeviceId, ip);
                }
            }
        });

        // Cerrar modal al hacer clic fuera
        document.getElementById('device-modal').addEventListener('click', (e) => {
            if (e.target.id === 'device-modal') {
                this.closeModal();
            }
        });
    }

    // ========== UTILIDADES ==========
    async checkAPIStatus() {
        try {
            const data = await apiClient.testAPI();
            const apiStatus = document.getElementById('api-status');
            
            if (data.error) {
                apiStatus.textContent = 'API: Desconectado';
                apiStatus.className = 'status-indicator offline';
            } else {
                apiStatus.textContent = 'API: Conectado';
                apiStatus.className = 'status-indicator online';
            }
        } catch (error) {
            console.error('Error checking API status:', error);
        }
    }

    showMessage(text, type = 'info', elementId = 'form-message') {
        const messageElement = document.getElementById(elementId);
        messageElement.textContent = text;
        messageElement.className = `message ${type}`;
        
        // Auto-ocultar después de 5 segundos
        setTimeout(() => {
            messageElement.textContent = '';
            messageElement.className = 'message';
        }, 5000);
    }

    closeModal() {
        document.getElementById('device-modal').style.display = 'none';
        this.currentDeviceId = null;
    }
}

// Inicializar aplicación cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    window.iotApp = new IoTApp();
});