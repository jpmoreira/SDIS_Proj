/*
 * 
 */
package Messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;


/**
 * The Interface Message.
 */
public abstract class Message {

	/** The HEADEREND. */
	public final static byte[] HEADEREND = {(byte) 0xD, (byte) 0xA, (byte) 0xD, (byte) 0xA};
	private String version = "1.0";
	
	public static String localVersion = "1.0";
	
	public static String MDB_ADDRESS = "224.0.0.1";
	public static int MDB_PORT = 8081;
	public static String MC_ADDRESS = "224.0.0.2";
	public static int MC_PORT = 8082;
	public static String MDR_ADDRESS = "224.0.0.3";
	public static int MDR_PORT = 8083;
	
	
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	

	public void send() {
		
		System.out.println("SENDING : "+this.toString());
		
		DatagramSocket socket = null;
		try {

			socket = new DatagramSocket();

			byte[] msg = new byte[this.toBytes().length];

			msg = this.toBytes();

			DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(MC_ADDRESS, MC_PORT) );

			socket.send(packet);


		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			socket.close();
		}
		
		
	}

	/**
	 * 
	 * States whether the passed message is of interest to this instance. (Used in the observer)
	 * @param msg
	 * @return
	 */
	public boolean ofInterest(Message msg){
		
		return false;
		
	}



	@Override
	public String toString() {
		return "Generic Message";
	}
}
