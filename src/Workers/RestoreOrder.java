package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import Files.FileToRestore;
import Messages.GetChunkMsg;
import Messages.Message;

public class RestoreOrder extends WorkOrder {

	private int port;
	private DatagramSocket socket;
	private InetAddress address;
	private FileToRestore file;

	public RestoreOrder(String path, int port, String channel) {

		try {

			this.file = new FileToRestore(FileToRestore.fileIDForBackedFile("path"));
			this.address = InetAddress.getByName(channel);
			this.socket = new DatagramSocket();
			this.port = port;

		} catch (Exception e) {

		}

	}

	@Override
	public void run() {
		
		Integer[] missingChunks = file.missingChunkNrs();
		
		if (missingChunks.length == 0) cancel();
		
		try {
			for (Integer chunkNo : missingChunks) {

				Message msgToSend = new GetChunkMsg(Message.getVersion(), file.getFileID(), chunkNo.toString());
				byte[] msg = msgToSend.toBytes();


				socket = new DatagramSocket();

				DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(address, port) );

				socket.send(packet);

			}
		} catch (IOException e) {
		}
		
	}

}
