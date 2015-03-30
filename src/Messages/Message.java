/*
 * 
 */
package Messages;

// TODO: Auto-generated Javadoc
/**
 * The Interface Message.
 */
public interface Message {

	/** The HEADEREND. */
	final byte[] HEADEREND = {(byte) 0xD, (byte) 0xA, (byte) 0xD, (byte) 0xA};
	
	/**
	 * Process.
	 *
	 * @return the message
	 */
	public Message process();
	
	/**
	 * To bytes.
	 *
	 * @return the byte[]
	 */
	public byte[] toBytes();

	public byte[] buildHeader();
	
}
