package Workers;



import Files.FileToRestore;
import Messages.GetChunkMsg;
import Messages.Message;

public class RestoreOrder extends WorkOrder {

	private static int count = 0;
	private FileToRestore file;
	private long time = 500;

	private int nrOfRepeats = 1;
	
	public RestoreOrder(String path) {

		try {

			this.file = new FileToRestore(FileToRestore.fileIDForBackedFile(path));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		Integer[] missingChunks = file.missingChunkNrs();
		try {
			while(missingChunks.length > 0 || nrOfRepeats < 6) {

				

				for (Integer chunkNo : missingChunks) {

					Message msgToSend = new GetChunkMsg(Message.localVersion,
							file.getFileID(), chunkNo.toString());

					msgToSend.send();

				}
				
				

				nrOfRepeats++;
				Thread.sleep(time);
				time = time*2;
				
				
				missingChunks = file.missingChunkNrs();
				
				

			}

		} catch (InterruptedException e) {
			
			System.out.println(e.getMessage());
	
		}
		
	
		
		try {
			file.reconstructFile();
			file.cleanup();
		} catch (Exception e1) {

		}
		
		synchronized(RestoreOrder.class){
			count --;
			
			System.out.println("CLOSING SOCKET");
			Scout mdr = Scout.getMDRScout();
			if(count == 0){
				mdr.closeSocket();
				try {
					mdr.join();
				} catch (InterruptedException e) {}
			} 
			
			
		}
		
		
	}

	
	@Override
	public synchronized void start() {
		
		synchronized(RestoreOrder.class){
			count ++;
		}
	
		
		super.start();
	}
}
