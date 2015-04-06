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
 * The Class PutChunkMsg.
 */
public class PutChunkMsg extends Message {
	
	private final String MSGCOD = "PUTCHUNK";
	public Chunk chunk = null;
	

	/**
	 * Instantiates a new put chunk msg that is to be sent.
	 *
	 * @param chunk the chunk
	 */
	public PutChunkMsg(SendChunk chunk, String version) {
		super(version);
		this.chunk = chunk;
		
	}
	
	
	

	/**
	 * Instantiates a new put chunk msg that was recieved.
	 *
	 * @param version the version
	 * @param fileId the file id
	 * @param chunkNo the chunk no
	 * @param repDeg the rep deg
	 * @param body the body
	 */
	
	public PutChunkMsg(String version, String fileId, String chunkNo,
			String repDeg, byte[] body) {
		
		super(version);
		try {
			this.chunk = new RecieveChunk(fileId, Integer.parseInt(chunkNo));//attempt to load the chunk
			this.chunk = null;// put it to null
		} catch (Exception e) {//in case we were not able to load the chunk
			try {
				this.chunk = new RecieveChunk(fileId, Integer.parseInt(chunkNo),body,Integer.parseInt(repDeg));//create a new one and store it
			} catch (Exception e1) {
				//In case the file to whom this chunk belongs is ours.
				e1.printStackTrace();
			}
		}
		
	}

	
	
	
	/* (non-Javadoc)
	 * @see Messages.Message#process()
	 */
	public Message process() {

		if (chunk == null) return null;
	
		
		
		try {
			if(chunk.desiredReplicationDegreeExceeded()) {
				((RecieveChunk) chunk).cleanup();
			}
		} catch (Exception e) {
			System.out.println("Chunk already exists.");
			return null;
		}
		
		return new StoredMsg(getVersion(), chunk.fileID, "" + chunk.nr);
		
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
		return (MSGCOD + " " + getVersion() + " " + chunk.fileID + " " + chunk.nr + " " + chunk.desiredReplicationDegree() + " ").getBytes();
	}
	
	
	@Override
	public void send() {
		
		
		System.out.println("SENDING : "+this.toString());
		
		DatagramSocket socket = null;
		try {

			socket = new DatagramSocket();

			byte[] msg = new byte[this.toBytes().length];

			msg = this.toBytes();

			DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(MDB_ADDRESS, MDB_PORT) );

			socket.send(packet);


		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			socket.close();
		}
		
		
	}

	
	@Override
	public String toString() {
		
		if(chunk != null) return MSGCOD+": nr = "+chunk.nr+" fileID = "+chunk.fileID;
		else return MSGCOD+" (not interested)";
	}
}
