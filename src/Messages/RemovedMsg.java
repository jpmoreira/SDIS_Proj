/*
 * 
 */
package Messages;

import Chunk.Chunk;
import Chunk.SendChunk;

/**
 * The Class RemovedMsg.
 */
public class RemovedMsg extends Message {

	
	private final String MSGCOD = "REMOVED";
	
	int repDeg;

	private Chunk chunk = null;

	/**
	 * Instantiates a new removed message.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public RemovedMsg(String version, String fileId, String chunkNo) {
		
		super(version);
		try {
			this.chunk = new SendChunk(fileId, Integer.parseInt(chunkNo));
			if(this.chunk != null) chunk.decrementReplicationCount();
		} catch (Exception e) {
			
		}
	}

	public RemovedMsg(Chunk chunk) {
		super(Message.localVersion);
		this.chunk = chunk;
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		if (chunk == null || chunk.desiredReplicationDegreeMet()) return null;		
		
		return new PutChunkMsg((SendChunk)chunk, getVersion());
		
	}

	/* (non-Javadoc)
	 * @see Messages.Message#toBytes()
	 */
	@Override
	public byte[] toBytes() {
		
		byte[] header = buildHeader();
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		
		return msgToSend;
	}

	
	
	@Override
	public byte[] buildHeader() {
		
		return (MSGCOD + " " + getVersion() + " " + chunk.fileID + " "  + chunk.nr + " ").getBytes();
	}

	
	@Override
	public boolean ofInterest(Message msg) {
		
		if(!(msg instanceof PutChunkMsg))return false;
		
		
		PutChunkMsg chunkMsg = (PutChunkMsg) msg;
		
		if (chunkMsg.chunk.nr != this.chunk.nr) return false;
		
		if(!chunkMsg.chunk.fileID.equals(this.chunk.fileID))return false;
		
		
		return true;
	}


	@Override
	public String toString() {
		
		if(chunk != null)return MSGCOD+": nr = "+chunk.nr+" fileID = "+chunk.fileID;
		return MSGCOD+" (not interested)";
	}
}
