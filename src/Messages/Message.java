package Messages;

public interface Message {

	final byte[] HEADEREND = {(byte) 0xDA,(byte) 0xDA};
	
	public Message process();
	
	public byte[] toBytes();
	
}
