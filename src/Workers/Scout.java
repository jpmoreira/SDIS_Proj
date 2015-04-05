package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Files.FileToRestore;
import Main.Gui;

public class Scout extends Thread{


	public static final int BUFFERSIZE = 70000;
	private InetAddress address;
	private MulticastSocket socket;




	public Scout(int port, String ip) {

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
				
				byte[] msg = new byte[packet.getLength()];

				msg = packet.getData();

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

}
