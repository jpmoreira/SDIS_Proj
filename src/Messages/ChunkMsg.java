/*
 * 
 */
package Messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import Chunk.*;


/**
 * The Class ChunkMsg.
 */
public class ChunkMsg extends Message {


	private final String MSGCOD = "CHUNK";
	public Chunk chunk;
	

	/**
	 * Used to create a Chunk Message to be sent
	 *
	 * @param chunk the chunk
	 */
	public ChunkMsg(SendChunk chunk, String version) {
		super(version);
		this.chunk = chunk;
	}

	/**
	 * Used to create a Chunk Message that was recieved.
	 *
	 * @param version the version of the protocol
	 * @param fileId the file id
	 * @param chunkNo the chunk number
	 * @param body the chunk received (bytes)
	 */
	public ChunkMsg(String version, String fileId, String chunkNo, byte[] body) {	
		super(version);
		
		try {
			//new FileToRestore(fileId);
			this.chunk = new RecieveChunk(fileId,Integer.parseInt(chunkNo),body); 
		} catch (Exception e1) {
			//If fileID isn't from us.
		}
	}

	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {
		

		//TODO Enhancement
		return null;
	}

	/* (non-Javadoc)
	 * @see Messages.Message#toBytes()
	 */
	@Override
	public byte[] toBytes() {
		
		byte[] header = buildHeader();
		
		byte[] body = new byte[0];
		body = chunk.getContent();

		
		byte[] msgToSend = new byte[header.length + HEADEREND.length + body.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		System.arraycopy(body, 0, msgToSend, header.length+HEADEREND.length, body.length);		
		
		return msgToSend;
		
	}

	public byte[] buildHeader() {
		 
		return (MSGCOD  + " " + getVersion() + " " + chunk.fileID + " " + chunk.nr + " ").getBytes();
	}
	
	
	@Override
	public void send() {
		
		
		System.out.println("SENDING : "+this.toString());
		
		DatagramSocket socket = null;
		try {

			socket = new DatagramSocket();

			byte[] msg = new byte[this.toBytes().length];

			msg = this.toBytes();
			
			

			System.out.println("SENDING TO "+MDR_ADDRESS+":"+MDR_PORT+"size = "+msg.length);
			
			DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(MDR_ADDRESS, MDR_PORT) );

			socket.send(packet);


		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			socket.close();
		}
		
		
	}

	
	@Override
	public String toString() {
		
		return MSGCOD+": nr = "+chunk.nr+" fileID = "+chunk.fileID;
		
	
	}
}
