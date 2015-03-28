package Messages;

public class MessageFactory {
	
	public static Message processMessage(byte[] msg) {
		
		final byte CRLF = (byte) 0xDA;
		
		int msgSize = msg.length;
		boolean headerEndFound = false;
		boolean headerEndStart = false;
		int index = 0;
		
		String strMsg = "";
		
		while (!headerEndFound && index < msgSize) {
			
			strMsg = strMsg.concat(new String(msg,index,1));
			
			if ((msg[index] == CRLF)) {
				if (headerEndStart){
					headerEndFound = true;
				} else {
					headerEndStart = true;
				}
			}
			index++;
		}
		
		String[] header = strMsg.trim().split(" ");
		
		try {
			switch (header[0]) {
			case "PUTCHUNK":
				
				if (header.length != 6) throw new Exception("PUTCHUNK header ERROR!");
				
				byte[] body = new byte[msg.length-index];
				System.arraycopy(msg, index, body, 0, body.length);
				
				return new PutChunkMsg(header[1],header[2],header[3],header[4], body);
				
			case "STORED":
				
				if (header.length != 5) throw new Exception("STORED header ERROR!");
				
				return new StoredMsg(header[1],header[2],header[3]);
				
			case "GETCHUNK":
				
				if (header.length != 5) throw new Exception("GETCHUNK header ERROR!");
				
				return new GetChunkMsg(header[1],header[2],header[3]);
				
			case "CHUNK":
				
				if (header.length != 5) throw new Exception("CHUNK header ERROR!");
				
				body = new byte[msg.length-index];
				System.arraycopy(msg, index, body, 0, body.length);
				
				return new ChunkMsg(header[1],header[2],header[3], body);
				
			case "DELETE":
				
				if (header.length != 4) throw new Exception("DELETE header ERROR!");
				
				return new DeleteMsg(header[1],header[2]);
				
			case "REMOVED":
				
				if (header.length != 5) throw new Exception("REMOVED header ERROR!");
				
				return new RemovedMsg(header[1],header[2],header[3]);
				
			default:
				break;
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		return null;
		
	}

}
