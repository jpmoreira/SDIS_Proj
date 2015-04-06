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

			while(nrOfRepeats < 6) {
				
				allChunkDelivered = true;
				
				chunksToSend = file.getChunks();
			
				for (SendChunk sendChunk : chunksToSend) {

					
					if (sendChunk.desiredReplicationDegreeMet())
						continue;

					allChunkDelivered = false;

					sendChunk.resetReplicationCount();

					Message msgToSend = new PutChunkMsg(sendChunk,
							Message.localVersion);

					msgToSend.send();

				}
				
				if(allChunkDelivered)break;
				
				nrOfRepeats++;
				currentThread().sleep(time);
				time = time*2;
				

			}
			


		} catch (Exception e) {

		}
	}

}
