package Main;

import java.rmi.RemoteException;

public class App implements UserInterface {
	
	
	public static void main(String[] args) {
		
		if (args.length < 3 || args.length > 5) {
			System.out.println("Usage: java App  <port_number> <oper> <opnd>*");
			System.out.println("\t<host_name> is the name of the host running the server");
			System.out.println("\t<port_number> is the server port");
			System.out.println("\t<oper> REGISTER || LOOKUP || EXIT");
			System.out.println("\t<opnd>* is the list of arguments");
			System.out.println("\t\t<plate number> <owner name> for register");
			System.out.println("\t\t<plate number> for lookup");
			return;
		}
		
	}
	
	
	

	@Override
	public void backupFile(String path, int repDeg) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreFile(String path) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFile(String path) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reclaimSpace(int size) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
