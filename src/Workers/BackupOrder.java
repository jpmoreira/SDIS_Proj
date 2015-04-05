package Workers;

import Chunk.SendChunk;
import Files.FileToBackup;
import Messages.Message;
import Messages.PutChunkMsg;

public class BackupOrder extends WorkOrder{ 

	FileToBackup file;

	
	private int nrOfRepeats = 1;

	private long time = 500;

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

			boolean allChunkDelivered;

			do {
				
				chunksToSend = file.getChunks();
				allChunkDelivered = true;
				for (SendChunk sendChunk : chunksToSend) {

					if (sendChunk.desiredReplicationDegreeMet())
						continue;

					allChunkDelivered = false;

					sendChunk.resetReplicationCount();

					Message msgToSend = new PutChunkMsg(sendChunk,
							Message.getVersion());

					msgToSend.send();

				}
				
				nrOfRepeats++;
				Thread.sleep(time);
				time = time*2;
				
			} while (allChunkDelivered || nrOfRepeats > 5);

		} catch (Exception e) {

		}
	}

}
