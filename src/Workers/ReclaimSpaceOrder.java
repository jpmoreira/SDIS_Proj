package Workers;

import java.sql.SQLException;

import Chunk.Chunk;
import Main.Database;
import Messages.*;

public class ReclaimSpaceOrder extends WorkOrder{

	public ReclaimSpaceOrder(int size, int port, String channel) {
		
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
		
		while (!enough) {
			
			Chunk chunk = db.getChunkInBetterConditionToBeDeleted();
			
			Message msgToBeSend = new RemovedMsg(chunk);
			
		}
	}

}
