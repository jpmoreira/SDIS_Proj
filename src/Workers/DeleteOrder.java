package Workers;

import Files.FileToBackup;
import Messages.DeleteMsg;
import Messages.Message;

public class DeleteOrder extends WorkOrder {
	
	private FileToBackup file;

	public DeleteOrder(String filePath) {

		try {
			this.file = new FileToBackup(filePath);
		} catch (Exception e) {

		}


	}

	@Override
	public void run() {
		
		Message msgToSend = new DeleteMsg(Message.localVersion, file.getFileID());
		
		msgToSend.send();
		
		//TODO Enhancement (Sugestão: nova mensagem com feedback)
		file.remove();
		
	}

}
