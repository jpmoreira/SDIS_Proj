package Workers;

import Chunk.SendChunk;
import Files.FileToBackup;
import Messages.Message;
import Messages.PutChunkMsg;

public class BackupOrder extends WorkOrder{ 

	FileToBackup file;

	
	private int nrOfRepeats = 1;

	public BackupOrder(String filePath, int repDegree) {
		try {

			this.file = new FileToBackup(filePath, repDegree);
	
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
				
				msgToSend.send();
				
			}
			
			nrOfRepeats++;

			if (allChunkDelivered || nrOfRepeats > 5) cancel();

		} catch (Exception e) {

		}
	}

}
