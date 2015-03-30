package Messages;

import Chunk.*;
import Files.FileToBackup;

public class GetChunkMsg implements Message {

	private String version, fileID;
	int nr,repDeg;

	public GetChunkMsg(String version, String fileId, String chunkNo) {
		this.version = version;
		this.fileID = fileId;
		this.nr = Integer.parseInt(chunkNo);
	}

	public Message process() {
		// TODO Get Chunk from database
		FileToBackup file = null;
		
		SendChunk chunk = new SendChunk(nr, file);
	
		return new ChunkMsg(chunk);
	}

	@Override
	public byte[] toBytes() {
		
		byte[] header = ("GETCHUNK " + version + " " + fileID + " "  + nr + " ").getBytes();
		
		byte[] msgToSend = new byte[header.length + HEADEREND.length];
		
		System.arraycopy(header, 0, msgToSend, 0, header.length);
		System.arraycopy(HEADEREND, 0, msgToSend, header.length, HEADEREND.length);
		
		return null;
	}

}
