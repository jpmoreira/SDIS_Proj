package Tests;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import Messages.ChunkMsg;
import Messages.DeleteMsg;
import Messages.GetChunkMsg;
import Messages.Message;
import Messages.MessageFactory;
import Messages.PutChunkMsg;
import Messages.RemovedMsg;
import Messages.StoredMsg;

public class MessagesTests {
	
	byte[] out,body, msgToBeSent;
	Message processedMsg;
	
	@Before
	public void initialize(){
		body = "BODY".getBytes();
	}
	
	
	private void appendHeaderEnd(byte[] msg) {
		
		out = new byte[msg.length + Message.HEADEREND.length];
		System.arraycopy(msg, 0, out, 0, msg.length);
		System.arraycopy(Message.HEADEREND, 0, out, msg.length, Message.HEADEREND.length);
		
	}
	
	private void appendBody() {
		
		msgToBeSent = new byte[out.length + body.length];
		System.arraycopy(out, 0, msgToBeSent, 0, out.length);
		System.arraycopy(body, 0, msgToBeSent, out.length, body.length);
		
	}

	@Test
	public void creatPutMsg() {
		
		byte[] msg = "PUTCHUNK 12.0 FileId 1 3 ".getBytes();
		
		appendHeaderEnd(msg);
		appendBody();
		
		processedMsg = MessageFactory.processMessage(msgToBeSent);
		assertTrue(processedMsg instanceof PutChunkMsg);
		
	}
	

	@Test
	public void createStoredMsg() {
		
		byte[] msg = "STORED 12.0 FileId 2 ".getBytes();
		
		appendHeaderEnd(msg);
		
		processedMsg = MessageFactory.processMessage(out);
		assertTrue(processedMsg instanceof StoredMsg);
		
	}


	@Test
	public void createGetChunkMsg() {
		
		byte[] msg = "GETCHUNK 12.0 FileId 3 ".getBytes();	
		
		appendHeaderEnd(msg);
		
		processedMsg = MessageFactory.processMessage(out);
		assertTrue(processedMsg instanceof GetChunkMsg);
		
	}

	
	@Test
	public void createChunkMsg() {
		
		byte[] msg = "CHUNK 12.0 FileId 4 ".getBytes();	
		
		appendHeaderEnd(msg);
		appendBody();
		
		processedMsg = MessageFactory.processMessage(msgToBeSent);
		assertTrue(processedMsg instanceof ChunkMsg);
		
	}
	
	
	@Test
	public void createDeleteMsg() {
		
		byte[] msg = "DELETE 12.0 FileId ".getBytes();	
		
		appendHeaderEnd(msg);
		
		processedMsg = MessageFactory.processMessage(out);
		assertTrue(processedMsg instanceof DeleteMsg);
		
	}


	@Test
	public void createRemovedMsg() {
		
		byte[] msg = "REMOVED 12.0 FileId 5 ".getBytes();		
		
		appendHeaderEnd(msg);
		
		processedMsg = MessageFactory.processMessage(out);
		assertTrue(processedMsg instanceof RemovedMsg);
		
	}

	
}
