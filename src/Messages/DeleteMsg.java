/*
 * 
 */
package Messages;

// TODO: Auto-generated Javadoc
/**
 * The Class DeleteMsg.
 */
public class DeleteMsg implements Message {

	/** The file id. */
	private String version, fileID;
	private final String MSGCOD = "DELETE";

	/**
	 * Instantiates a new delete msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 */
	public DeleteMsg(String version, String fileId) {
		
		this.version = version;
		this.fileID = fileId;
		
	}

	
	
	
	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		// TODO Remove chunk and update database
		
		
		
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
		return (MSGCOD + " " + version + " " + fileID + " ").getBytes();
	}

}
