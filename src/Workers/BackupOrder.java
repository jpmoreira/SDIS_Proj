package Workers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import Chunk.SendChunk;
import Files.FileToBackup;
import Messages.Message;
import Messages.PutChunkMsg;

public class BackupOrder extends WorkOrder{ 

	FileToBackup file;
	private InetAddress address;
	private DatagramSocket socket;
	private int port;

	public BackupOrder(String filePath, int repDegree, int port, String channel) {
		try {

			this.file = new FileToBackup(filePath, repDegree);
			this.address = InetAddress.getByName(channel);
			this.socket = new DatagramSocket();
			this.port = port;

		} catch (Exception e) {

		}
	}



	@Override
	public void run() {

		SendChunk[] chunksToSend = null;

		try {

			chunksToSend = file.getChunks();

			boolean allChunkDelivered = true;

			for (SendChunk sendChunk : chunksToSend) {

				if(sendChunk.desiredReplicationDegreeMet()) continue;

				allChunkDelivered = false;

				sendChunk.resetReplicationCount();

				Message msgToSend = new PutChunkMsg(sendChunk, Message.getVersion());
				byte[] msg = msgToSend.toBytes();

				socket = new DatagramSocket();
				DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(address, port) );

				socket.send(packet);

			}

			if (allChunkDelivered) cancel();

		} catch (Exception e) {

		}
	}

}
