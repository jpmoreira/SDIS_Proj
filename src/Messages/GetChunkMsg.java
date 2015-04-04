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
	 * Instantiates a new gets the chunk msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public GetChunkMsg(String version, String fileId, String chunkNo) {
		super(version);
		
		try {
			this.chunk = new SendChunk(fileId,Integer.parseInt(chunkNo));
		} catch (Exception e) {
			chunk = null;
		}
	}

	
	
	
	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		if  (chunk == null) return null;
		
		try {
			return new ChunkMsg(new SendChunk(chunk.fileID, chunk.nr),getVersion());
		} catch (Exception e) {
			
		}
		return null;
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

}
