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

	private FileToRestore file;

	public RestoreOrder(String path) {

		try {

			this.file = new FileToRestore(FileToRestore.fileIDForBackedFile("path"));

		} catch (Exception e) {

		}

	}

	@Override
	public void run() {
		
		Integer[] missingChunks = file.missingChunkNrs();
		
		if (missingChunks.length == 0) cancel();
		
		for (Integer chunkNo : missingChunks) {

			Message msgToSend = new GetChunkMsg(Message.getVersion(), file.getFileID(), chunkNo.toString());

			msgToSend.send();

		}
		
		//TODO Enhancement
		
	}

}
