/*
 * 
 */
package Messages;


/**
 * A factory for creating Message objects.
 */
public class MessageFactory {
	
	/**
	 * Process message.
	 *
	 * @param msg the msg
	 * @return the message
	 */
	public static Message processMessage(byte[] msg) {
		
		int msgSize = msg.length;
		int index = 0;
		boolean headerEndFound = false;
		int headerEndStatus = 0;
		
		String strMsg = "";
		
		while (!headerEndFound && index < msgSize) {
			
			strMsg = strMsg.concat(new String(msg,index,1));
			
			switch (headerEndStatus) {
			case 0:
				if (msg[index] == Message.HEADEREND[0])
					headerEndStatus++;
				break;
			case 1:
				if (msg[index] == Message.HEADEREND[1])
					headerEndStatus++;
				else 
					headerEndStatus = 0;
				break;
			case 2:
				if (msg[index] == Message.HEADEREND[2])
					headerEndStatus++;
				else 
					headerEndStatus = 0;
				break;
			case 3:
				if (msg[index] == Message.HEADEREND[3])
					headerEndFound = true;
				
				headerEndStatus = 0;	
				break;
			default:
				break;
			}
			
			index++;
		}
		
		String[] header = strMsg.trim().split(" ");
		
		try {
			switch (header[0]) {
			case "PUTCHUNK":
				
				if (header.length != 5) throw new Exception("PUTCHUNK header ERROR!");
				
				byte[] body = new byte[msg.length-index];
				System.arraycopy(msg, index, body, 0, body.length);
				
				return new PutChunkMsg(header[1],header[2],header[3],header[4], body);
				
			case "STORED":
				
				if (header.length != 4) throw new Exception("STORED header ERROR!");
				
				return new StoredMsg(header[1],header[2],header[3]);
				
			case "GETCHUNK":
				
				if (header.length != 4) throw new Exception("GETCHUNK header ERROR!");
				
				return new GetChunkMsg(header[1],header[2],header[3]);
				
			case "CHUNK":
				
				if (header.length != 4) throw new Exception("CHUNK header ERROR!");
				
				body = new byte[msg.length-index];
				System.arraycopy(msg, index, body, 0, body.length);
				
				return new ChunkMsg(header[1],header[2],header[3], body);
				
			case "DELETE":
				
				if (header.length != 3) throw new Exception("DELETE header ERROR!");
				
				return new DeleteMsg(header[1],header[2]);
				
			case "REMOVED":
				
				if (header.length != 4) throw new Exception("REMOVED header ERROR!");
				
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
