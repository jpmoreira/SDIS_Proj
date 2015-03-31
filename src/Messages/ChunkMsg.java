/*
 * 
 */
package Messages;

import java.io.IOException;

import Chunk.*;


/**
 * The Class ChunkMsg.
 */
public class ChunkMsg implements Message {


	private String version, fileID;
	int nr,repDeg;
	private byte[] body;
	
	private final String MSGCOD = "CHUNK";
	

	/**
	 * Instantiates a new chunk msg.
	 *
	 * @param chunk the chunk
	 */
	public ChunkMsg(Chunk chunk) {
		this.fileID = chunk.fileID;
		this.nr = chunk.nr;
		//this.repDeg = chunk.
		try {
			this.body = chunk.getContent();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Instantiates a new chunk msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 * @param body the body
	 */
	public ChunkMsg(String version, String fileId, String chunkNo, byte[] body) {
		this.version = version;
		this.fileID = fileId;
		this.nr = Integer.parseInt(chunkNo);
		this.body = body;
		
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		try {

			new RecieveChunk(fileID, nr, body, "path", false);

			System.out.println("Chunk stored...");

		} catch (Exception e) {
			System.out.println("Chunk already exists.");
		}
		
		//TODO Enhancement
		return null;
	}

	/* (non-Javadoc)
	 * @see Messages.Message#toBytes()
	 */
	@Override
	public byte[] toBytes() {
		
		byte[] header = buildHeader();
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length + body.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		System.arraycopy(body, 0, msgToSend, header.length+HEADEREND.length, body.length);		
		
		return msgToSend;
		
	}

	public byte[] buildHeader() {
		 
		return (MSGCOD  + " " + version + " " + fileID + " " + nr + " " + repDeg + " ").getBytes();
	}
	
	

}
