/*
 * 
 */
package Messages;


/**
 * The Class StoredMsg.
 */
public class StoredMsg implements Message {

	private final String MSGCOD = "STORED";
	private String version;
	private String fileID;
	private int nr;


	/**
	 * Instantiates a new stored msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public StoredMsg(String version, String fileId, String chunkNo) {
		this.version = version;
		this.fileID = fileId;
		this.nr = Integer.parseInt(chunkNo);
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		// TODO update database
		
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
		return  (MSGCOD  + " " + version + " " + fileID + " " + nr + " ").getBytes();
	}

}
