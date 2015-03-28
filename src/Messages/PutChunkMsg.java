package Messages;

import Main.Chunk;

public class PutChunkMsg implements Message {
	
	private String version, fileID, repDeg;
	int number;
	private byte[] body;
	


	public PutChunkMsg(String version, String fileId, String chunkNo,
			String repDeg, byte[] body) {
		
		this.version = version;
		this.fileID = fileId;
		this.number = Integer.parseInt(chunkNo);
		this.body = body;
		
	}

	public Message process() {

		System.out.println("Making backup...");
		
		// TODO Update constructor
		// 
		//Chunk chunk = new Chunk(fileID, number);
		
		
		return new StoredMsg(version, fileID, "" + number);
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		
		return null;
	}
	

}
