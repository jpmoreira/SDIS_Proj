/*
 * 
 */
package Messages;

import Chunk.*;


/**
 * The Class StoredMsg.
 */
public class StoredMsg extends Message {

	private final String MSGCOD = "STORED";

	private SendChunk chunk = null;


	/**
	 * Instantiates a new stored msg.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 */
	public StoredMsg(String version, String fileId, String chunkNo) {
		super(version);
	
		try {
			this.chunk = new SendChunk(fileId, Integer.parseInt(chunkNo));//because using a RecieveChunk would require that the chunk has a path
			if(chunk != null) chunk.incrementReplicationCount();
		} catch (Exception e) {
			this.chunk = null;
		}
		
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		
		return null;
	}
	
	@Override
	public void send() {
		//if(chunk != null)chunk.decrementReplicationCount(); // we know we are going to get our own message
		super.send();
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
		return  (MSGCOD  + " " + getVersion() + " " + chunk.fileID + " " + chunk.nr + " ").getBytes();
	}



	@Override
	public String toString() {
		
		if(chunk != null)return MSGCOD+": nr = "+chunk.nr+" fileID = "+chunk.fileID;
		return MSGCOD+" (not interested)";
	}
}
