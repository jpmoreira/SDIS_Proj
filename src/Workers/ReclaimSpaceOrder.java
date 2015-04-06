package Workers;

import java.sql.SQLException;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Files.S_File;
import Main.Database;
import Messages.*;

public class ReclaimSpaceOrder extends WorkOrder{
	
	

	public ReclaimSpaceOrder(int size) {
		
		S_File.availableSpace -= size;
		
		
	}

	@Override
	public void run() {
		
		Database db = null;
		try {
			db = new Database();
		} catch (SQLException e) {}
		
		while (S_File.spaceLeft() < 0) {
			
			RecieveChunk chunk = db.getChunkInBetterConditionToBeDeleted();
			
			try {
				chunk.cleanup();
			} catch (Exception e) {}
			
			Message msgToBeSend = new RemovedMsg(chunk);
			msgToBeSend.send();
			
			
		}
	}

}
