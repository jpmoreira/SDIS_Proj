/*
 * 
 */
package Messages;

import java.util.ArrayList;

import Workers.Worker;


/**
 * A factory for creating Message objects.
 */
public class MessageFactory {
	
	
	static ArrayList<Worker> putChunkList = new ArrayList<Worker>();
	static ArrayList<Worker> storedList = new ArrayList<Worker>();
	static ArrayList<Worker> getChunkList = new ArrayList<Worker>();
	static ArrayList<Worker> chunkList = new ArrayList<Worker>();
	static ArrayList<Worker> deleteList = new ArrayList<Worker>();
	static ArrayList<Worker> removeList = new ArrayList<Worker>();
	
	
	/**
	 * Process message.
	 *
	 * @param msg the msg
	 * @return the message
	 */
	public static Message processMessage(byte[] msg) {
		
		int msgSize = msg.length;
		int index = 0;
		boolean headerEndFound = false;
		int headerEndStatus = 0;		
		
		String strMsg = "";
		
		while (!headerEndFound && index < msgSize) {
			
			strMsg = strMsg.concat(new String(msg,index,1));
			
			switch (headerEndStatus) {
			case 0:
				if (msg[index] == Message.HEADEREND[0])
					headerEndStatus++;
				break;
			case 1:
				if (msg[index] == Message.HEADEREND[1])
					headerEndStatus++;
				else 
					headerEndStatus = 0;
				break;
			case 2:
				if (msg[index] == Message.HEADEREND[2])
					headerEndStatus++;
				else 
					headerEndStatus = 0;
				break;
			case 3:
				if (msg[index] == Message.HEADEREND[3])
					headerEndFound = true;
				
				headerEndStatus = 0;	
				break;
			default:
				break;
			}
			
			index++;
		}
		
		String[] header = strMsg.trim().split(" ");
		
		try {
			switch (header[0]) {
			case "PUTCHUNK":
				
				if (header.length != 5) throw new Exception("PUTCHUNK header ERROR!");
				
				byte[] body = new byte[msg.length-index];
				System.arraycopy(msg, index, body, 0, body.length);
				
				PutChunkMsg putChunkMsg = new PutChunkMsg(header[1],header[2],header[3],header[4], body);
				notifyList(putChunkMsg);
				
				return putChunkMsg;
				
			case "STORED":
				
				if (header.length != 4) throw new Exception("STORED header ERROR!");
				
				StoredMsg storedMsg = new StoredMsg(header[1],header[2],header[3]);
				notifyList(storedMsg);
				
				return storedMsg;
				
			case "GETCHUNK":
				
				if (header.length != 4) throw new Exception("GETCHUNK header ERROR!");
				
				GetChunkMsg getChunkMsg = new GetChunkMsg(header[1],header[2],header[3]);
				notifyList(getChunkMsg);
				
				return getChunkMsg;
				
			case "CHUNK":
				System.out.println("Chunk message");
				if (header.length != 4) throw new Exception("CHUNK header ERROR!");
				
				body = new byte[msg.length-index];
				System.arraycopy(msg, index, body, 0, body.length);
				
				System.out.println("B");
				
				ChunkMsg chunkMsg = new ChunkMsg(header[1],header[2],header[3], body);
				System.out.println("Bxxxxx");
				
				notifyList(chunkMsg);
				
				System.out.println("tttt");
				
				return chunkMsg;
				
			case "DELETE":
				
				if (header.length != 3) throw new Exception("DELETE header ERROR!");
				
				DeleteMsg deleteMsg = new DeleteMsg(header[1],header[2]);
				notifyList(deleteMsg);
				
				return deleteMsg;
				
			case "REMOVED":
				
				if (header.length != 4) throw new Exception("REMOVED header ERROR!");
				
				RemovedMsg removedMsg = new RemovedMsg(header[1],header[2],header[3]);
				notifyList(removedMsg);
				
				return removedMsg;
				
			default:
				break;
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		return null;
		
	}

	/*
	 * 
	 * Waiting Lists Management
	 * 
	 */


	/**
	 * Register a worker waiting for PutChunkMsg
	 * 
	 * @param w
	 */
	public static void attachObserverToPutChunkMsg(Worker w) {
		putChunkList.add(w);
	}

	/**
	 * Remove a worker from the PutChunkMsg waiting list
	 * 
	 * @param w
	 */
	public static void detachObserverFromPutChunkMsg(Worker w) {
		putChunkList.remove(w);
	}

	
	/**
	 * Register a worker waiting for StoredMsg
	 * 
	 * @param w
	 */
	public static void attachObserverToStoredMsg(Worker w) {
		storedList.add(w);
	}


	/**
	 * Remove a worker from the StoredMsg waiting list
	 * 
	 * @param w
	 */
	public static void detachObserverFromStoredMsg(Worker w) {
		storedList.remove(w);
	}


	/**
	 * Register a worker waiting for GetChunkMsg
	 * 
	 * @param w
	 */
	public static void attachObserverToGetChunkMsg(Worker w) {
		getChunkList.add(w);
	}


	/**
	 * Remove a worker from the GetChunkMsg waiting list
	 * 
	 * @param w
	 */
	public static void detachObserverFromGetChunkMsg(Worker w) {
		getChunkList.remove(w);
	}

	
	/**
	 * Register a worker waiting for ChunkMsg
	 * 
	 * @param w
	 */
	public static void attachObserverToChunkMsg(Worker w) {
		chunkList.add(w);
	}


	/**
	 * Remove a worker from the ChunkMsg waiting list
	 * 
	 * @param w
	 */
	public static void detachObserverFromChunkMsg(Worker w) {
		chunkList.remove(w);
	}
	
	
	/**
	 * Register a worker waiting for DeleteMsg
	 * 
	 * @param w
	 */
	public void attachObserverToDeleteMsg(Worker w) {
		deleteList.add(w);
	}


	/**
	 * Remove a worker from the DeleteMsg waiting list
	 * 
	 * @param w
	 */
	public static  void detachObserverFromDeleteMsg(Worker w) {
		deleteList.remove(w);
	}

	
	/**
	 * Register a worker waiting for RemovedMsg
	 * 
	 * @param w
	 */
	public static void attachObserverToRemoveMsg(Worker w) {
		removeList.add(w);
	}


	/**
	 * Remove a worker from the RemovedMsg waiting list
	 * 
	 * @param w
	 */
	public static void detachObserverFromRemoveMsg(Worker w) {
		removeList.remove(w);
	}

	
	/**
	 * Notify all workers on PutChunkMsg list
	 * 
	 * @param msg
	 */
	private static void notifyList(PutChunkMsg msg) {
		
		for (Worker worker : putChunkList) {
			worker.update(msg);
		}

	}

	
	/**
	 * Notify all workers on StoredMsg list
	 * 
	 * @param msg
	 */
	private static void notifyList(StoredMsg msg) {
		for (Worker worker : storedList) {
			worker.update(msg);
		}
		
	}

	
	
	/**
	 * Notify all workers on GetChunkMsg list
	 * 
	 * @param msg
	 */
	private static void notifyList(GetChunkMsg msg) {
		for (Worker worker : getChunkList) {
			worker.update(msg);
		}
		
	}
	
	
	/**
	 * Notify all workers on ChunkMsg list
	 * 
	 * @param msg
	 */
	private static void notifyList(ChunkMsg msg) {
		for (Worker worker : chunkList) {
			worker.update(msg);
		}
	}
	
	
	/**
	 * Notify all workers on RemovedMsg list
	 * 
	 * @param msg
	 */
	private static void notifyList(RemovedMsg msg) {
		for (Worker worker : removeList) {
			worker.update(msg);
		}
		
	}

	/**
	 * Notify all workers on RemovedMsg list
	 * 
	 * @param msg
	 */
	private static void notifyList(DeleteMsg msg) {
		for (Worker worker : deleteList) {
			worker.update(msg);
		}
		
	}
	

}
