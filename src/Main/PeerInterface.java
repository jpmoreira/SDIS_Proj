package Main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote {
	
	public void backupFile(String path, int repDeg) throws RemoteException; 
	
	public void restoreFile(String path) throws RemoteException;
	
	public void deleteFile(String path) throws RemoteException;
	
	public void reclaimSpace(int size) throws RemoteException;

}
