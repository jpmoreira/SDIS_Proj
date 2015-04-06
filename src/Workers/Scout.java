package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
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
	
	
	private static DatagramSocket sender = null;

	
	int port;
	String ip;
	

	private Scout(int port, String ip) {

		this.port = port;
		this.ip = ip;
		
		try {

			
			
			address = InetAddress.getByName(ip);
			socket = new MulticastSocket(port);
			socket.setTimeToLive(0);
			socket.joinGroup(address);
			socket.setLoopbackMode(true);

		}
		catch (IOException e) {
			System.out.println("Adress/ Socket Error " + e.getMessage());
		} 

	}




	@Override
	public void run() {

		System.out.println("LISTENING AT "+ip+":"+port);
		try {

			
			
			while (true) {

				
				System.out.println("Scouting");
				
				
				byte[] rbuf = new byte[BUFFERSIZE];

				DatagramPacket packet = new DatagramPacket(rbuf, BUFFERSIZE);

				
			
				socket.receive(packet);
				
				
				synchronized (Scout.class) {
					
					if(sender == null){
						try {
							sender = new DatagramSocket();
						} catch (SocketException e) {}
					}
					
					
				}
	
				
				if(packet.getPort() == sender.getLocalPort() && packet.getAddress().equals(InetAddress.getLocalHost())){
				
					
					byte[] byteMsg = new byte[packet.getLength()];
					System.arraycopy(packet.getData(),packet.getOffset(),byteMsg, 0, packet.getLength());
					
					Message msg = MessageFactory.processMessage(byteMsg);
					System.out.println("Dropping "+msg.toString());
					continue;
					
				}
				
				byte[] byteMsg = new byte[packet.getLength()];
				System.arraycopy(packet.getData(),packet.getOffset(),byteMsg, 0, packet.getLength());
				
				Message msg = MessageFactory.processMessage(byteMsg);
				
				System.out.println("Recieved Message: "+msg.toString());
				
				Worker w = new Worker(msg);
				w.start();

			}



		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("OUT");
		return;
	}

	public void closeSocket() {
		
		socket.close();	

	}


	public static Scout getMDBScout() {
		
		
		if (mdbScout == null ) mdbScout = new Scout(Message.MDB_PORT, Message.MDB_ADDRESS);
		
		return mdbScout;
	}


	public static Scout getMDRScout() {
		
		
		if (mdrScout == null || mdrScout.getState().equals(Thread.State.TERMINATED)){
			System.out.println("NEEWWW");
			mdrScout = new Scout(Message.MDR_PORT, Message.MDR_ADDRESS);
		}
		return mdrScout;
	}	


	public static Scout getMCScout() {
		if (mcScout == null) mcScout = new Scout(Message.MC_PORT, Message.MC_ADDRESS);
		return mcScout;
	}	

	
	
	public static void sendSocket(DatagramPacket packet){
		
		synchronized (Scout.class) {
			
			if(sender == null){
				try {
					sender = new DatagramSocket();
				} catch (SocketException e) {}
			}
			
			
			try {
				sender.send(packet);
			} catch (IOException e) {}
			
		}
		
		
	}
	
}
