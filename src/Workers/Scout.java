package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Main.Gui;

public class Scout implements Runnable{


	private static final int BUFFERSIZE = 70000;
	private InetAddress address;
	private MulticastSocket socket;




	public Scout(int port, String ip) {

		try {

			address = InetAddress.getByName(ip);
			socket = new MulticastSocket(port);

		} catch (IOException e) {
			System.out.println("Adress/ Socket Error");
		}

	}




	@Override
	public void run() {

		System.out.println("Receiving...");

		try {
			
			socket.joinGroup(address);


			while (Gui.RUNNING) {

				byte[] rbuf = new byte[BUFFERSIZE];

				DatagramPacket packet = new DatagramPacket(rbuf, BUFFERSIZE);

				socket.receive(packet);

				byte[] msg = new byte[packet.getLength()];

				msg = packet.getData();

				Thread t = new Thread(new Worker(msg));
				t.start();
				
			}
			
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
