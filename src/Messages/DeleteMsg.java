package Messages;

public class DeleteMsg implements Message {

	private String version, fileID;
	

	public DeleteMsg(String version, String fileId) {
		
		this.version = version;
		this.fileID = fileId;
		
	}

	public Message process() {
		// TODO Remove chunk and update database
		
		
		// TODO Enhancement
		return null;
	}

	@Override
	public byte[] toBytes() {
		
		byte[] header = ("DELETE " + version + " " + fileID + " ").getBytes();
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);	
		
		return msgToSend;
		
	}

}
