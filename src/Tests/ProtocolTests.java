package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.FileToRestore;
import Main.Database;
import Messages.ChunkMsg;
import Messages.DeleteMsg;
import Messages.GetChunkMsg;
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
			
			FileToBackup file = new FileToBackup("testFiles/oneChunkFile", 1);
			
		
			
			SendChunk[] chunksToSend = file.getChunks();
			ArrayList<byte[]> msgsRecieved = new ArrayList<byte[]>();
			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			// BACKUP REQUEST
			for (SendChunk sendChunk : chunksToSend) {
				
				Message msgToSend = new PutChunkMsg(sendChunk, Message.getVersion());
				
				msgsRecieved.add(msgToSend.toBytes());
				
			}
			
			
			// REQUEST PROCESS
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
			
			
			// REPLY PROCESS
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
		
		assertFalse(oldfile.isFile());

		try {
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database dbSource = new Database();
			
			FileToRestore file = new FileToRestore(FileToRestore.fileIDForBackedFile("testFiles/oneChunkFile"));
			
			// CHUNK REQUEST
			ArrayList<byte[]> msgsToSend = new ArrayList<byte[]>();
			for (int i = 0; i < file.getNrChunks(); i++){
				
				Message msg = new GetChunkMsg(Message.getVersion(), file.fileID, Integer.toString(i));
				msgsToSend.add(msg.toBytes());
				
			}
			
			
			// PROCESS REQUESTS
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database dbDest = new Database();
			
			ArrayList<byte[]> answers = new ArrayList<byte[]>();
			for (byte[] bs : msgsToSend) {
				
				Message request = MessageFactory.processMessage(bs);
				assertTrue(request instanceof GetChunkMsg);
				
				Message returnMessage = request.process();
				assertTrue(returnMessage instanceof ChunkMsg);
				
				answers.add(returnMessage.toBytes());
				
			}
			
			// REPLY PROCESS
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			
			for (byte[] bs : answers) {
				
				Message reply = MessageFactory.processMessage(bs);
				assertTrue(reply instanceof ChunkMsg);
				
				Message result = reply.process();
				assertNull(result);
				
			}
			
			// REPLY PROCESS - PEER THAT IS NOT THE OWNER
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";

			for (byte[] bs : answers) {

				Message reply = MessageFactory.processMessage(bs);
				assertTrue(reply instanceof ChunkMsg);

				Message result = reply.process();
				assertNull(result);

			}
			
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			// CHECK FILE RESTORED
			assertEquals(file.missingChunkNrs().length,0);
			file.reconstructFile();
			
			assertTrue(file.isRestored());
			assertTrue(oldfile.isFile());
			
			RecieveChunk chunk = new RecieveChunk(file.fileID,0);
			assertNotNull(dbSource.getPathForChunk(chunk));
			
			file.cleanup();
			assertNull(dbSource.getPathForChunk(chunk));
			
			// CHECK SOURCE DATABASE
			String[] files = dbSource.backedFilePaths();
			
			assertEquals(files.length,1);
			assertEquals(files[0],file.getFilePath());			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			
			// CHECK DESTINY DATABASE
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			
			assertEquals(dbDest.nrChunksStored(),1);
			
			

			Files.delete(newfile.toPath());
			assertFalse(newfile.isFile());
			
		} catch (Exception e) {

			if(newfile.renameTo(oldfile)){
				System.out.println("File renamed");
			}else{
				System.out.println("Sorry! the file can't be renamed");
			}
			
			fail("Error");
		}
		
	}
	
//	@Test
	public void c_deleteSubProtocol() throws SQLException {
		
		
		
		try {
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database dbSource = new Database();

			FileToRestore file = new FileToRestore(FileToRestore.fileIDForBackedFile("testFiles/oneChunkFile"));

			// DELETE REQUEST
			ArrayList<byte[]> msgsToSend = new ArrayList<byte[]>();
			for (int i = 0; i < file.getNrChunks(); i++){

				Message msg = new DeleteMsg(Message.getVersion(), file.fileID);
				msgsToSend.add(msg.toBytes());

			}
			
			
			
			// PROCESS REQUESTS
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database dbDest = new Database();

			for (byte[] bs : msgsToSend) {

				Message request = MessageFactory.processMessage(bs);
				assertTrue(request instanceof DeleteMsg);

				Message returnMessage = request.process();
				assertNull(returnMessage);

			}
			
			
			// Check Source DB
			String[] files = dbSource.backedFilePaths();

			assertEquals(files.length,0);		
			assertEquals(dbSource.nrChunksStored(),0);


			// Check Destiny DB
			assertEquals(dbDest.chunksForFile(file.getFileID()).length,0);

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void reclaimSpaceSubProtocol() {
		
	}
	
	
	
}
