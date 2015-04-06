package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import Main.Gui;
import Messages.*;

public class Scout extends Thread{


	public static final int BUFFERSIZE = 70000;
	private InetAddress address;
	private MulticastSocket socket;


	private static Scout mdbScout = null;
	private static Scout mdrScout = null;
	private static Scout mcScout = null;
	

	private Scout(int port, String ip) {

		try {

			address = InetAddress.getByName(ip);
			socket = new MulticastSocket(port);
			socket.joinGroup(address);

		}
		catch (IOException e) {
			System.out.println("Adress/ Socket Error " + e.getMessage());
		} 

	}




	@Override
	public void run() {

		try {

			while (Gui.RUNNING) {

				byte[] rbuf = new byte[BUFFERSIZE];

				DatagramPacket packet = new DatagramPacket(rbuf, BUFFERSIZE);

				socket.receive(packet);

				Message msg = MessageFactory.processMessage(packet.getData());
				
				Worker w = new Worker(msg);
				w.start();

			}



		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	public void closeSocket() {
		socket.close();	
	}


	public static Scout getMDBScout() {
		if (mdbScout == null) return new Scout(Message.MDB_PORT, Message.MDB_ADDRESS);
		return mdbScout;
	}


	public static Scout getMDRScout() {
		if (mdrScout == null) return new Scout(Message.MDR_PORT, Message.MDR_ADDRESS);
		return mdrScout;
	}	


	public static Scout getMCScout() {
		if (mcScout == null) return new Scout(Message.MC_PORT, Message.MC_ADDRESS);
		return mcScout;
	}	

	
}
