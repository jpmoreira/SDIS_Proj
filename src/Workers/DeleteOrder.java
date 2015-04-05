package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import Files.FileToBackup;
import Messages.DeleteMsg;
import Messages.Message;

public class DeleteOrder extends WorkOrder {
	
	private FileToBackup file;
	private InetAddress address;
	private DatagramSocket socket;
	private int port;

	public DeleteOrder(String filePath, int port, String channel) {
		try {

			this.file = new FileToBackup(filePath);
			this.address = InetAddress.getByName(channel);
			this.socket = new DatagramSocket();
			this.port = port;

		} catch (Exception e) {

		}
		
	}

	@Override
	public void run() {
		
		Message msgToSend = new DeleteMsg(Message.getVersion(), file.getFileID());
		
		byte[] msg = msgToSend.toBytes();

		try {
			socket = new DatagramSocket();

			DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(address, port) );

			socket.send(packet);

		} catch (IOException e) {
		}
		
		//TODO Enhancement
		file.remove();
		
	}

}
