/*
 * 
 */
package Messages;

import java.io.IOException;

import Chunk.*;
import Files.FileToRestore;


/**
 * The Class ChunkMsg.
 */
public class ChunkMsg extends Message {


	private final String MSGCOD = "CHUNK";
	private Chunk chunk;
	

	/**
	 * Instantiates a new chunk msg.
	 *
	 * @param chunk the chunk
	 */
	public ChunkMsg(SendChunk chunk, String version) {
		super(version);
		this.chunk = chunk;
	}

	/**
	 * Instantiates a new chunk message.
	 *
	 * @param version the version of the protocol
	 * @param fileId the file id
	 * @param chunkNo the chunk number
	 * @param body the chunk received (bytes)
	 */
	public ChunkMsg(String version, String fileId, String chunkNo, byte[] body) {	
		super(version);
		
		try {
			new FileToRestore(fileId);
			this.chunk = new RecieveChunk(fileId,Integer.parseInt(chunkNo),body);
		} catch (Exception e1) {
			
		}
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		try {
			new FileToRestore(chunk.fileID).addChunk((RecieveChunk) chunk);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
		
		byte[] body = new byte[0];
		try {
			body = chunk.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length + body.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		System.arraycopy(body, 0, msgToSend, header.length+HEADEREND.length, body.length);		
		
		return msgToSend;
		
	}

	public byte[] buildHeader() {
		 
		return (MSGCOD  + " " + getVersion() + " " + chunk.fileID + " " + chunk.nr + " ").getBytes();
	}
	
	

}
