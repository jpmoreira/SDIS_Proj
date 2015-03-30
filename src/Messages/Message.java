package Messages;

public interface Message {

	final byte[] HEADEREND = {(byte) 0xD, (byte) 0xA, (byte) 0xD, (byte) 0xA};
	
	public Message process();
	
	public byte[] toBytes();
	
}
