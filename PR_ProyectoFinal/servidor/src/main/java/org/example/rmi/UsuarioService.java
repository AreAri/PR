package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UsuarioService extends Remote {

    boolean registrarUsuario(String nombre, String passwordHash)
            throws RemoteException;
}
