//areari
//remote para rmi ya que pueden ser invocados de forma remota 
package org.example.rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UsuarioService extends Remote {

    boolean registrar(String nombre, String passwordHash)
            throws RemoteException;

    boolean autenticar(String nombre, String passwordHash)
            throws RemoteException;
}
