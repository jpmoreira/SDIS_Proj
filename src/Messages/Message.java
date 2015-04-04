/*
 * 
 */
package Messages;


/**
 * The Interface Message.
 */
public abstract class Message {

	/** The HEADEREND. */
	public final static byte[] HEADEREND = {(byte) 0xD, (byte) 0xA, (byte) 0xD, (byte) 0xA};
	private static String version = "1.0";
	
	
	public Message(String version) {
		this.setVersion(version);
	}

	/**
	 * Process.
	 *
	 * @return the message
	 */
	public abstract Message process();
	
	/**
	 * To bytes.
	 *
	 * @return the byte[]
	 */
	public abstract byte[] toBytes();

	public abstract byte[] buildHeader();

	public static String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
