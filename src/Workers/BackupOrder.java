package Workers;

import Chunk.SendChunk;
import Files.FileToBackup;
import Messages.Message;
import Messages.PutChunkMsg;

public class BackupOrder extends WorkOrder{ 

	FileToBackup file;

	
	private int nrOfRepeats = 1;

	private long time = 1000;

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

					

					Message msgToSend = new PutChunkMsg(sendChunk,
							Message.localVersion);

					
					msgToSend.send();
					
				
					Thread.sleep(500);

				}
				
				if(allChunkDelivered)break;
				
				nrOfRepeats++;
				
				Thread.sleep(time);
				time = time*2;
				

			}
			


		} catch (Exception e) {

		}
	}

}
