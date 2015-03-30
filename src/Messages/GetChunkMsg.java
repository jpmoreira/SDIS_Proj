/*
 * 
 */
package Messages;

import Chunk.*;

// TODO: Auto-generated Javadoc
/**
 * The Class GetChunkMsg.
 */
public class GetChunkMsg implements Message {

	private final String MSGCOD = "GETCHUNK";

	/** The file id. */
	private String version, fileID;
	
	/** The rep deg. */
	int nr;

	
	
	
	/**
	 * Instantiates a new gets the chunk msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public GetChunkMsg(String version, String fileId, String chunkNo) {
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
			System.out.println("Chunk not found!");
			chunk = null;
		}
	
		return new ChunkMsg(chunk);
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
		return (MSGCOD + " " + version + " " + fileID + " "  + nr + " ").getBytes();
	}

}
