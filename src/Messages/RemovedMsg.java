/*
 * 
 */
package Messages;

import Chunk.SendChunk;

/**
 * The Class RemovedMsg.
 */
public class RemovedMsg implements Message {

	
	private final String MSGCOD = "REMOVED";
	
	private String version, fileID;
	
	int nr, repDeg;

	/**
	 * Instantiates a new removed message.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public RemovedMsg(String version, String fileId, String chunkNo) {
		this.version = version;
		this.fileID = fileId;
		this.nr = Integer.parseInt(chunkNo);
		
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		SendChunk chunk;
		
		try {
			chunk = new SendChunk(fileID, nr, false);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			chunk = null;
		}
	//TODO check replicationDegree
		if (chunk == null /*|| repDeg >= expRepDeg */) return null;		
		
		return new PutChunkMsg(chunk);
		
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
		
		return (MSGCOD + " " + version + " " + fileID + " "  + nr + " ").getBytes();
	}

}
