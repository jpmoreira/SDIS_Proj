package Workers;



import Files.FileToRestore;
import Messages.GetChunkMsg;
import Messages.Message;

public class RestoreOrder extends WorkOrder {

	private static int count = 0;
	private FileToRestore file;
	private long time = 500;

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
			while(missingChunks.length > 0) {

				
				System.out.println("looping");

				for (Integer chunkNo : missingChunks) {

					Message msgToSend = new GetChunkMsg(Message.localVersion,
							file.getFileID(), chunkNo.toString());

					msgToSend.send();

				}
				
				System.out.println("looping2");


				Thread.sleep(time);
				time = time*2;
				System.out.println("ola");
				
				missingChunks = file.missingChunkNrs();
				
				System.out.println(missingChunks.length);

			}

		} catch (InterruptedException e) {
			
			System.out.println(e.getMessage());
	
		}
		
		

		System.out.println("finished");
		
		synchronized(RestoreOrder.class){
			count --;
			
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
