package Tests;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.FileToRestore;
import Files.S_File;
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
			
			S_File.availableSpace = 2560000;
			
			S_File.cleanFolder(new File("backups/"));
			
			S_File.cleanFolder(new File("backups_2/"));
			
			
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database.defaultBackupDir = "backups/";
			Database dbSource = new Database(true);
			
			FileToBackup file = new FileToBackup("testFiles/oneChunkFile", 1);
			
		
			
			SendChunk[] chunksToSend = file.getChunks();
			ArrayList<byte[]> msgsRecieved = new ArrayList<byte[]>();
			
			assertEquals(dbSource.nrChunksStored(),file.getNrChunks());
			
			// BACKUP REQUEST
			for (SendChunk sendChunk : chunksToSend) {
				
				Message msgToSend = new PutChunkMsg(sendChunk, Message.localVersion);
				
				msgsRecieved.add(msgToSend.toBytes());
				
			}
			
			
			// REQUEST PROCESS
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database.defaultBackupDir = "backups_2/";
			Database dbDest = new Database(true);
			ArrayList<byte[]> returnMsgs = new ArrayList<byte[]>();
			
			for (byte[] bs : msgsRecieved) {
				
				Message msg = MessageFactory.processMessage(bs);			
				assertTrue(msg instanceof PutChunkMsg);
				
				Message rtrnMsg = msg.process();
				assertTrue(rtrnMsg instanceof StoredMsg);
				
				returnMsgs.add(rtrnMsg.toBytes());
				
				
				//we already backed it up ! therefore the result should be noting
				
				int nrChunksBefore = dbDest.nrChunksStored();
				
				Message msg2 = MessageFactory.processMessage(bs);
				Message rtrnMsg2 = msg2.process();
				
				assertEquals(nrChunksBefore,dbDest.nrChunksStored());//no change should be made 
				assertNull(rtrnMsg2);
				
				
							
			}
			
			
			// REPLY PROCESS
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database.defaultBackupDir = "backups/";
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
			Database.defaultBackupDir = "backups/";
			Database dbSource = new Database();
			
			FileToRestore file = new FileToRestore(FileToRestore.fileIDForBackedFile("testFiles/oneChunkFile"));
			
			// CHUNK REQUEST
			ArrayList<byte[]> msgsToSend = new ArrayList<byte[]>();
			for (int i = 0; i < file.getNrChunks(); i++){
				
				Message msg = new GetChunkMsg(Message.localVersion, file.fileID, Integer.toString(i));
				msgsToSend.add(msg.toBytes());
				
			}
			
			
			// PROCESS REQUESTS
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database.defaultBackupDir = "backups_2/";
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
			Database.defaultBackupDir = "backups/";
			
			for (byte[] bs : answers) {
				
				Message reply = MessageFactory.processMessage(bs);
				assertTrue(reply instanceof ChunkMsg);
				
				Message result = reply.process();
				assertNull(result);
				
			}
			
			// REPLY PROCESS - PEER THAT IS NOT THE OWNER
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database.defaultBackupDir = "backups_2/";

			for (byte[] bs : answers) {

				Message reply = MessageFactory.processMessage(bs);
				assertTrue(reply instanceof ChunkMsg);

				Message result = reply.process();
				assertNull(result);

			}
			
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database.defaultBackupDir = "backups/";
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
			Database.defaultBackupDir = "backups_2/";
			
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
	
	@Test
	public void c_deleteSubProtocol() {
		
		
		
		try {
			Database.databaseToUse = "supportingFiles/supportingDB.db";
			Database.defaultBackupDir = "backups/";
			Database dbSource = new Database();

			FileToBackup file = new FileToBackup("testFiles/oneChunkFile");

			// DELETE REQUEST
			ArrayList<byte[]> msgsToSend = new ArrayList<byte[]>();
			
			Message msg = new DeleteMsg(Message.localVersion, file.getFileID());
			msgsToSend.add(msg.toBytes());
			
			
			file.remove();
			
			// Check Source DB
			String[] files = dbSource.backedFilePaths();

			assertEquals(files.length,0);		
			assertEquals(dbSource.nrChunksStored(),0);
			
			
			
			// PROCESS REQUESTS
			Database.databaseToUse = "supportingFiles/supportingDB_2.db";
			Database.defaultBackupDir = "backups_2/";
			Database dbDest = new Database();

			for (byte[] bs : msgsToSend) {

				Message request = MessageFactory.processMessage(bs);
				assertTrue(request instanceof DeleteMsg);
				
				assertEquals(file.getFileID(),((DeleteMsg) request).getFileID());

				Message returnMessage = request.process();
				assertNull(returnMessage);

			}
			

			// Check Destiny DB
			assertEquals(dbDest.chunksForFile(file.getFileID()).length,0);

			
			
		} catch (Exception e) {
			fail("Error");
		}
	}
	
	
//	@Test
	public void reclaimSpaceSubProtocol() {
		
	}
	
	
	
	
	@Test
	public void z_recievingOwnPutChunk() throws Exception{
		
		
		Database d = new Database(true);
		
		SendChunk[] chunks = new FileToBackup("testFiles/RIGP.pdf",10).getChunks();
		
		PutChunkMsg msg = new PutChunkMsg(chunks[0], "1.0");

		
		Message msgRecieved = MessageFactory.processMessage(msg.toBytes());
		
		assertTrue(msgRecieved instanceof PutChunkMsg);
		
		Message reply = msgRecieved.process();
		
		assertNull(reply);
		
		assertNull(d.getPathForChunk(chunks[0]));
		
	}
	
	@Test
	public void y_revertBeingMadeCauseDegreeIsMet() throws Exception{
		
		
		S_File.availableSpace = 2560000;
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
		changeToDB1();
		new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",1);
		
		SendChunk[] chunksOnOrigin = b.getChunks();
		
		PutChunkMsg putChunkMsg_sender = new PutChunkMsg(chunksOnOrigin[0], "1.0");
		
		byte[] putChunkMsg_bytes = putChunkMsg_sender.toBytes();
		
		changeToDB2();
		Database dest = new Database(true);
		
		Message putChunkMsg_dest = MessageFactory.processMessage(putChunkMsg_bytes);
		
		assertTrue(putChunkMsg_dest instanceof PutChunkMsg);
		

		StoredMsg recieved_store_msg = new StoredMsg("1.0", b.fileID, "0");
		
		assertEquals(dest.nrChunksStored(),1);
		
		RecieveChunk c = dest.chunksForFile(b.fileID)[0];
		
		assertEquals(c.getReplicaCount(), 1);
		
		recieved_store_msg.process();
		
		assertEquals(c.getReplicaCount(),2);
		
		putChunkMsg_dest.process();
		
		assertEquals(dest.nrChunksStored(),0);
		
		

		
		
		
		
		
		
		
		
		
	}

	@Test
	public void w_recieveGetChunkMessageWeCannotFulfill() throws Exception{
		
		S_File.availableSpace = 2560000;
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
		changeToDB1();
		new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",1);
		
		b.getChunks(); // register the chunks
		
		GetChunkMsg getChunkMessage_origin = new GetChunkMsg("1.0", b.fileID, "0");
		
		byte[] getChunkMsgBytes = getChunkMessage_origin.toBytes();
		
		changeToDB2();
		Database db = new Database(true);
		
		
		Message getChunksMsg_dest = MessageFactory.processMessage(getChunkMsgBytes);
		
		assertTrue(getChunksMsg_dest instanceof GetChunkMsg);
		
		assertEquals(db.nrChunksStored(),0);
		
		getChunksMsg_dest.process();
		
		assertEquals(db.nrChunksStored(),0);
		
		
		
	}

	@Test
	public void x_simulateStoreProcessingByPutChunkOrig() throws Exception{
		
		S_File.availableSpace = 2560000;
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
		changeToDB1();
		new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",1);
		
		SendChunk[] chunksOnOrigin = b.getChunks(); //register the chunks

		assertEquals(chunksOnOrigin[0].getReplicaCount(),0);
		
		StoredMsg recieved_store_msg = new StoredMsg("1.0", b.fileID, "0");
		
		recieved_store_msg.process();
		
		assertEquals(chunksOnOrigin[0].getReplicaCount(),1);//check if replica count was actually incremented
		
		assertTrue(chunksOnOrigin[0].desiredReplicationDegreeMet());
		
		assertFalse(chunksOnOrigin[0].desiredReplicationDegreeExceeded());
		
		
	}

	@Test
	public void v_simulateRecievingStoredWeAreNotInterestedIn() throws Exception{
		
		S_File.availableSpace = 2560000;
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
		changeToDB1();
		Database db = new Database(true);
		
		assertEquals(db.nrChunksStored(),0);
		
		StoredMsg stored_recieved = new StoredMsg("1.0", "asdasdasd", "0");
		
		assertEquals(db.nrChunksStored(),0);
		
		stored_recieved.process();
		
		assertEquals(db.nrChunksStored(),0);
		
		
	}
	
	public static void changeToDB1(){
		
		Database.databaseToUse = "supportingFiles/supportingDB.db";
		Database.defaultBackupDir = "backups/";
		
	}
	
	public static void changeToDB2(){
		
		Database.databaseToUse = "supportingFiles/supportingDB_2.db";
		Database.defaultBackupDir = "backups_2/";
		
	}
	
	
}
