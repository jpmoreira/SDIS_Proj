/*
 * 
 */
package Messages;

import java.io.IOException;

import Chunk.*;


// TODO: Auto-generated Javadoc
/**
 * The Class PutChunkMsg.
 */
public class PutChunkMsg implements Message {
	
	private final String MSGCOD = "PUTCHUNK";

	/** The file id. */
	private String version, fileID;
	
	/** The rep deg. */
	int nr,repDeg;
	
	/** The body. */
	private byte[] body;
	

	/**
	 * Instantiates a new put chunk msg.
	 *
	 * @param chunk the chunk
	 */
	public PutChunkMsg(SendChunk chunk) {
		this.fileID = chunk.fileID;
		this.nr = chunk.nr;
		//this.repDeg = chunk.
		try {
			this.body = chunk.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			return null;
		}
		
		return new StoredMsg(version, fileID, "" + nr);
		
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
		return (MSGCOD + " " + version + " " + fileID + " " + nr + " " + repDeg + " ").getBytes();
	}
	

}
