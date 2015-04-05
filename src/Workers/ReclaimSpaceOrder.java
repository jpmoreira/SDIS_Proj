package Workers;

import java.sql.SQLException;

import Chunk.Chunk;
import Files.S_File;
import Main.Database;
import Messages.*;

public class ReclaimSpaceOrder extends WorkOrder{
	
	

	public ReclaimSpaceOrder(int size) {
		
		S_File.availableSpace -= size;
		
		
	}

	@Override
	public void run() {
		//TODO complete method
		
		Database db = null;
		try {
			db = new Database();
		} catch (SQLException e) {
			cancel();
		}
		boolean enough = false;
		
		while (S_File.spaceLeft() < 0) {
			
			Chunk chunk = db.getChunkInBetterConditionToBeDeleted();
			
			Message msgToBeSend = new RemovedMsg(chunk);
			
			msgToBeSend.send();
			
			
		}
	}

}
