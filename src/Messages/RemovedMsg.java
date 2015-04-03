/*
 * 
 */
package Messages;

import Chunk.SendChunk;

/**
 * The Class RemovedMsg.
 */
public class RemovedMsg extends Message {

	
	private final String MSGCOD = "REMOVED";
	
	int repDeg;

	private SendChunk chunk = null;

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
		} catch (Exception e) {
			
		}
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {

		//TODO check replicationDegree
		
		if (chunk == null || chunk. >= expRepDeg */) return null;		
		
		return new PutChunkMsg(chunk, getVersion());
		
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

}
