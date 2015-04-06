package Workers;



import Files.FileToRestore;
import Messages.GetChunkMsg;
import Messages.Message;

public class RestoreOrder extends WorkOrder {

	private FileToRestore file;
	private long time = 500;

	public RestoreOrder(String path) {

		try {

			this.file = new FileToRestore(FileToRestore.fileIDForBackedFile("path"));

		} catch (Exception e) {

		}

	}

	@Override
	public void run() {

		Integer[] missingChunks;
		try {
			do {

				missingChunks = file.missingChunkNrs();


				for (Integer chunkNo : missingChunks) {

					Message msgToSend = new GetChunkMsg(Message.localVersion,
							file.getFileID(), chunkNo.toString());

					msgToSend.send();

				}


				Thread.sleep(time);
				time = time*2;

			} while (missingChunks.length != 0);

		} catch (InterruptedException e) {
	
		}
		//TODO Enhancement

	}

}
