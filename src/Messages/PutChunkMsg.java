/*
 * 
 */
package Messages;

import java.io.IOException;

import Chunk.*;



/**
 * The Class PutChunkMsg.
 */
public class PutChunkMsg extends Message {
	
	private final String MSGCOD = "PUTCHUNK";
	private Chunk chunk = null;
	

	/**
	 * Instantiates a new put chunk msg.
	 *
	 * @param chunk the chunk
	 */
	public PutChunkMsg(SendChunk chunk, String version) {
		super(version);
		this.chunk = chunk;
		
	}
	
	
	

	/**
	 * Instantiates a new put chunk msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 * @param repDeg the rep deg
	 * @param body the body
	 */
	public PutChunkMsg(String version, String fileId, String chunkNo,
			String repDeg, byte[] body) {
		
		super(version);
		try {
			this.chunk = new RecieveChunk(fileId, Integer.parseInt(chunkNo));
			this.chunk = null;
		} catch (Exception e) {
			try {
				this.chunk = new RecieveChunk(fileId, Integer.parseInt(chunkNo),body);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}

	
	
	
	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {

		if (chunk == null) return null;
	
		
		//TODO Testar
		try {
			if(chunk.desiredReplicationDegreeMet()) {
				((RecieveChunk) chunk).cleanup();
			}
		} catch (Exception e) {
			System.out.println("Chunk already exists.");
			return null;
		}
		
		chunk.incrementReplicationCount();
		return new StoredMsg(getVersion(), chunk.fileID, "" + chunk.nr);
		
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see Messages.Message#toBytes()
	 */
	@Override
	public byte[] toBytes() {
		
		byte[] header = buildHeader();
		
		byte[] body = new byte[0];
		try {
			body = chunk.getContent();
		} catch (IOException e) {

		}
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length + body.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		System.arraycopy(body, 0, msgToSend, header.length+HEADEREND.length, body.length);		
		
		return msgToSend;
	}




	public byte[] buildHeader() {
		return (MSGCOD + " " + getVersion() + " " + chunk.fileID + " " + chunk.nr + " " + chunk.desiredReplicationDegree() + " ").getBytes();
	}
	

}
