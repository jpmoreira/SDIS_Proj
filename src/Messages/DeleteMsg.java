/*
 * 
 */
package Messages;

import Chunk.*;

/**
 * The Class DeleteMsg.
 */
public class DeleteMsg extends Message {

	/** The file id. */
	private final String MSGCOD = "DELETE";
	private String fileID;
	

	/**
	 * Instantiates a new delete msg that is going to be sent or to be recieved.
	 *
	 * @param version the version
	 * @param fileId the file id
	 */
	public DeleteMsg(String version, String fileId) {
		super(version);
		this.fileID = fileId;
		
	}

	
	
	
	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		Chunk.cleanupChunks(fileID);
		
		
		// TODO Enhancement 
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
		return (MSGCOD + " " + getVersion() + " " + fileID + " ").getBytes();
	}
	
	public String getFileID() {
		return fileID;
	}
	
	

}
