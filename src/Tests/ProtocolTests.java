package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import Chunk.SendChunk;
import Files.FileToBackup;
import Main.Database;
import Messages.Message;
import Messages.MessageFactory;
import Messages.PutChunkMsg;
import Messages.StoredMsg;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProtocolTests {
	
	@Test
	public void a_backupSubProtocol() {
		
		try {
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database dbSource = new Database(true);
			
			FileToBackup file = new FileToBackup("testFiles/oneChunkFile");
			
		
			
			SendChunk[] chunksToSend = file.getChunks();
			ArrayList<byte[]> msgsRecieved = new ArrayList<byte[]>();
			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			//SEND MESSAGES
			for (SendChunk sendChunk : chunksToSend) {
				
				Message msgToSend = new PutChunkMsg(sendChunk, Message.getVersion());
				
				msgsRecieved.add(msgToSend.toBytes());
				
			}
			
			
			// RECEIVE MESSAGES
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database dbDest = new Database(true);
			ArrayList<byte[]> returnMsgs = new ArrayList<byte[]>();
			
			for (byte[] bs : msgsRecieved) {
				
				Message msg = MessageFactory.processMessage(bs);			
				assertTrue(msg instanceof PutChunkMsg);
				
				Message rtrnMsg = msg.process();
				assertTrue(rtrnMsg instanceof StoredMsg);
				
				returnMsgs.add(rtrnMsg.toBytes());
							
			}
			
			
			// RETURN MESSAGES
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			for (byte[] bs : returnMsgs) {
				
				Message msg = MessageFactory.processMessage(bs);
				assertTrue(msg instanceof StoredMsg);
				
				Message resultMsg = msg.process();
				assertNull(resultMsg);
				
			}
			
			
			// Check Source DB
			String[] files = dbSource.backedFilePaths();
			
			assertEquals(files.length,1);
			assertEquals(files[0],file.getFilePath());			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			
			// Check Destiny DB
			assertEquals(dbDest.chunksForFile(file.getFileID()).length,file.getNrChunks());
			
		} catch (Exception e) {
			fail("Error");
		}
		
	}
	
	@Test
	public void b_restoreSubProtocol() {
		
		File oldfile =new File("testFiles/oneChunkFile");
		File newfile =new File("testFiles/oneChunkFile_BackUp");

		if(oldfile.renameTo(newfile)){
			System.out.println("File renamed");
		}else{
			System.out.println("Sorry! the file can't be renamed");
		}


		try {
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database dbSource = new Database(true);
			
			FileToBackup file = new FileToBackup("testFiles/oneChunkFile");
			
		
			
			SendChunk[] chunksToSend = file.getChunks();
			ArrayList<byte[]> msgsRecieved = new ArrayList<byte[]>();
			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			//SEND MESSAGES
			for (SendChunk sendChunk : chunksToSend) {
				
				Message msgToSend = new PutChunkMsg(sendChunk, Message.getVersion());
				
				msgsRecieved.add(msgToSend.toBytes());
				
			}
			
			
			// RECEIVE MESSAGES
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database dbDest = new Database(true);
			ArrayList<byte[]> returnMsgs = new ArrayList<byte[]>();
			
			for (byte[] bs : msgsRecieved) {
				
				Message msg = MessageFactory.processMessage(bs);			
				assertTrue(msg instanceof PutChunkMsg);
				
				Message rtrnMsg = msg.process();
				assertTrue(rtrnMsg instanceof StoredMsg);
				
				returnMsgs.add(rtrnMsg.toBytes());
							
			}
			
			
			// RETURN MESSAGES
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			for (byte[] bs : returnMsgs) {
				
				Message msg = MessageFactory.processMessage(bs);
				assertTrue(msg instanceof StoredMsg);
				
				Message resultMsg = msg.process();
				assertNull(resultMsg);
				
			}
			
			
			// Check Source DB
			String[] files = dbSource.backedFilePaths();
			
			assertEquals(files.length,1);
			assertEquals(files[0],file.getFilePath());			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			
			// Check Destiny DB
			assertEquals(dbDest.chunksForFile(file.getFileID()).length,file.getNrChunks());
			
		} catch (Exception e) {
			fail("Error");
		}
		
	}
	
	
	
	
}
