package Messages;

public class MessageFactory {
	
	public static Message processMessage(byte[] msg) {
		
		String strMsg = new String(msg);
		
		
		
		return new PutChunkMsg(msg);
		
	}

}
