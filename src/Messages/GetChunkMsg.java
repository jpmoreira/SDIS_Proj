/*
 * 
 */
package Messages;

import Chunk.*;


/**
 * The Class GetChunkMsg.
 */
public class GetChunkMsg extends Message {

	private final String MSGCOD = "GETCHUNK";

	private Chunk chunk;

	
	
	
	/**
	 * Instantiates a new get chunk msg to be 
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public GetChunkMsg(String version, String fileId, String chunkNo) {
		super(version);
		
		try {
			this.chunk = new SendChunk(fileId,Integer.parseInt(chunkNo));
			
			if(chunk.isOwn())chunk = null;
		} catch (Exception e) {
			chunk = null;
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		if  (chunk == null) return null;
		
		return new ChunkMsg((SendChunk)chunk,getVersion());

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




	public byte[] buildHeader() {
		return (MSGCOD + " " + getVersion() + " " + chunk.fileID + " "  + chunk.nr + " ").getBytes();
	}

	
	public boolean ofInterest(Message msg){
		
		
		if(!(msg instanceof ChunkMsg))return false;
		
		
		ChunkMsg chunkMsg = (ChunkMsg) msg;
		
		if (chunkMsg.chunk.nr != this.chunk.nr) return false;
		
		if(!chunkMsg.chunk.fileID.equals(this.chunk.fileID))return false;
		
		
		return true;
		
	}
	
	
	@Override
	public String toString() {
		
		if(chunk != null) return MSGCOD+": nr = "+chunk.nr+" fileID = "+chunk.fileID;
		
		else return MSGCOD+" (not interested)";
		
		
	}
}
