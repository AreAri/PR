/**
 * Módulo para comunicación con la API REST
 */

const API_BASE_URL = 'https://localhost:8443';

class APIClient {
    constructor() {
        this.token = localStorage.getItem('iot_token');
        this.user = JSON.parse(localStorage.getItem('iot_user') || '{}');
    }

    // Método genérico para peticiones
    async request(endpoint, method = 'GET', data = null, requiresAuth = true) {
        const url = `${API_BASE_URL}/api${endpoint}`;
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
            },
        };

        // Agregar token si existe y se requiere autenticación
        if (requiresAuth && this.token) {
            options.headers['Authorization'] = `Bearer ${this.token}`;
        }

        if (data) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(url, options);
            
            if (!response.ok) {
                if (response.status === 401) {
                    // Token inválido o expirado
                    this.logout();
                    window.location.href = '/';
                }
                throw new Error(`Error HTTP: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Error en petición API:', error);
            throw error;
        }
    }

    // ========== USUARIOS ==========
    async login(username, password) {
        try {
            const data = await this.request('/usuarios/login', 'POST', {
                username,
                password
            }, false); // No requiere autenticación

            if (data.success && data.token) {
                this.token = data.token;
                this.user = data.usuario || { username };
                
                localStorage.setItem('iot_token', this.token);
                localStorage.setItem('iot_user', JSON.stringify(this.user));
                
                return { success: true, data };
            } else {
                return { success: false, error: data.error || 'Error en login' };
            }
        } catch (error) {
            return { success: false, error: 'Error de conexión' };
        }
    }

    async registrarUsuario(usuarioData) {
        try {
            const data = await this.request('/usuarios/registro', 'POST', usuarioData, false);
            
            if (data.success && data.token) {
                this.token = data.token;
                this.user = data.usuario;
                
                localStorage.setItem('iot_token', this.token);
                localStorage.setItem('iot_user', JSON.stringify(this.user));
            }
            
            return data;
        } catch (error) {
            return { success: false, error: 'Error al registrar usuario' };
        }
    }

    logout() {
        this.token = null;
        this.user = {};
        localStorage.removeItem('iot_token');
        localStorage.removeItem('iot_user');
    }

    isAuthenticated() {
        return !!this.token;
    }

    async verificarUsuario(username) {
        try {
            const data = await this.request(`/usuarios/existe/${username}`, 'GET', null, false);
            return data.existe || false;
        } catch (error) {
            return false;
        }
    }

    // ========== DISPOSITIVOS ==========
    async getDispositivos() {
        return await this.request('/dispositivos');
    }

    async createDispositivo(dispositivo) {
        return await this.request('/dispositivos', 'POST', dispositivo);
    }

    async updateDispositivo(id, dispositivo) {
        return await this.request(`/dispositivos/${id}`, 'PUT', dispositivo);
    }

    async deleteDispositivo(id) {
        return await this.request(`/dispositivos/${id}`, 'DELETE');
    }

    async getDispositivosByTipo(tipo) {
        return await this.request(`/dispositivos/tipo/${tipo}`);
    }

    async getDispositivosActivos() {
        return await this.request('/dispositivos/activos');
    }

    // ========== TEST ==========
    async testAPI() {
        try {
            const data = await this.request('/dispositivos/test', 'GET', null, false);
            return data;
        } catch (error) {
            return { error: 'API no disponible' };
        }
    }
}

// Exportar instancia global
const apiClient = new APIClient();