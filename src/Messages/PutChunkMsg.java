package Messages;

import java.io.IOException;

import Chunk.*;


public class PutChunkMsg implements Message {
	
	private String version, fileID;
	int nr,repDeg;
	private byte[] body;
	

	public PutChunkMsg(SendChunk chunk) {
		this.fileID = chunk.fileID;
		this.nr = chunk.nr;
		//this.repDeg = chunk.
		try {
			this.body = chunk.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public PutChunkMsg(String version, String fileId, String chunkNo,
			String repDeg, byte[] body) {
		
		this.version = version;
		this.fileID = fileId;
		this.nr = Integer.parseInt(chunkNo);
		this.body = body;
		
	}

	public Message process() {

		try {
			
			new RecieveChunk(fileID, nr, body, "path", false);

			System.out.println("Chunk stored...");
			
		} catch (Exception e) {
			System.out.println("Chunk already exists.");
			return null;
		}
		
		return new StoredMsg(version, fileID, "" + nr);
		
	}

	@Override
	public byte[] toBytes() {
		
		byte[] header = ("PUTCHUNK " + version + " " + fileID + " " + nr + " " + repDeg + " ").getBytes();
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length + body.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		System.arraycopy(body, 0, msgToSend, header.length+HEADEREND.length, body.length);		
		
		return msgToSend;
	}
	

}
