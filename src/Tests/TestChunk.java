package Tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.S_File;
import Main.Database;

public class TestChunk {
	
	static final String testOutputFile = "testFiles/chunkTestFileOutput.chunk";

	@Before
	public void setupDB(){
		
		Database.databaseToUse = DatabaseTests.testDBFile;
	}
	
	@Test
	public void testSaveChunkToFileAndRetrive() {
	
		String s = "ola bom dia";
		
		
		try {
			
			new Database(true);//we have to clear the db first
			
			RecieveChunk c  = new RecieveChunk("id",0,s.getBytes(),testOutputFile,1);
			
			assertTrue(s.equals(new String(c.getContent())));//
			
			String contentComingFromFile = new String(c.getContent());
			
			assertEquals(contentComingFromFile,s);
			
			
			
		} catch (Exception e) {
			fail("unable to create chunk and save it");
			e.printStackTrace();
		}
		
		
		
		
	}

	@Test
	public void testSaveChunkToFileAndRetrive2() throws Exception{
		
		String s = "ola bom dia";
		
		
		try{
			new Database(true);//we have to clear the db first
			
		}
		catch (Exception e){
		
			e.printStackTrace();
			
		}
	
		
		
		RecieveChunk c = new RecieveChunk("id",0,s.getBytes(),testOutputFile,5);
		
		assertTrue(s.equals(new String(c.getContent())));//
		
		
		String contentComingFromFile = new String(c.getContent());
		
		assertEquals(contentComingFromFile,s);
		
		
		RecieveChunk retrivalChunk = new RecieveChunk("id",0);
		
		assertEquals(new String(retrivalChunk.getContent()),new String(c.getContent()));
		
	}

	@Test
	public void testIncrementAndResetOfReplicaCount() throws Exception{
		
		
		new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",100);
		
		SendChunk c = b.getChunk(7);
		
		assertEquals(c.getReplicaCount(),0);
		
		c.incrementReplicationCount();
		
		assertEquals(c.getReplicaCount(), 1);
		
		c.incrementReplicationCount();
		
		assertEquals(c.getReplicaCount(),2);
		
		c.resetReplicationCount();
		
		assertEquals(c.getReplicaCount(), 0);
		
	}

	@Test
	public void testCleanupOfChunks() throws Exception{
		
		new Database(true);
		
		
		RecieveChunk c1 = new RecieveChunk("asdasdasff", 0, new String("OLA BOM DIA").getBytes(),3);
		RecieveChunk c2 = new RecieveChunk("asdasdasff", 10, new String("OLA BOA TARDE").getBytes(),5);
		
		
		File f1 = new File(c1.getPath());
		File f2 = new File(c2.getPath());
		
		assertTrue(f1.exists());
		assertTrue(f2.exists());
		
		
		new Database();
		

		assertNotNull(c1.getPath());
		assertNotNull(c2.getPath());
		
		
		
		Chunk.cleanupChunks("asdasdasff");
		
		
		
		assertFalse(f1.exists());
		assertFalse(f2.exists());
		
		
		
		
		
		
		
		

		
	}

	@Test
	public void testLoadingSendChunkFromPreviouslyStoredChunk() throws Exception{
		
		new Database(true);
		
		
		byte[] content = new String("ola bom dia").getBytes("UTF8");
		
		new RecieveChunk("ola", 7, content,3);
		
		try{
			
			new SendChunk("ola", 0);
			fail("failed to throw exception while loadin inexisting chunk to send");
		}
		catch(Exception e){
			
			assertTrue(true);
		}
		
		SendChunk s = new SendChunk("ola",7);
		
		String str = new String(s.getContent());
		
		assertEquals(str,"ola bom dia");
		
		
		
		
	}

	@Test
	public void testReplicationRateFromFile() throws Exception{
		
		Database d = new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",10);
		
		SendChunk[] chunks = b.getChunks();
		
		for (SendChunk sendChunk : chunks) {
			
			assertEquals(sendChunk.desiredReplicationDegree(),b.desiredRepDegree);
			assertEquals(b.desiredRepDegree, d.getDesiredReplicationDegreeForChunk(sendChunk));
		}

		
		
		
	}

	@Test
	public void testReplicationRateWithoutFile() throws Exception{
		
		Database d = new Database(true);
		
		try{
			
			new RecieveChunk("aFileID", 0, new String("aContent").getBytes());
			fail("failed to notice that shouldn't allow creation of recievechunk cause file doesn't exist");
		}
		catch(Exception e ){
			
			assertTrue(true);
		}
		
		RecieveChunk r = new RecieveChunk("aFileID", 0, new String("aContent").getBytes(),15);
		
		assertEquals(d.getDesiredReplicationDegreeForChunk(r),15);
		
		
		
	}

	@Test
	public void testReplicaCountMetOrNot() throws Exception{
		
		S_File.availableSpace = 2560000;
		
		S_File.cleanFolder(new File("backups/"));
		
		
		Database d = new Database(true);
		RecieveChunk r = new RecieveChunk("ola", 1, new String("aasdasd").getBytes(),10);
		
		assertFalse(r.desiredReplicationDegreeMet());
		
		assertEquals(r.getReplicaCount(),1);
		assertEquals(d.replicaCountOfChunk(r.fileID, r.nr),1);
		
		r.incrementReplicationCount();
		
		assertEquals(r.getReplicaCount(),2);
		assertEquals(d.replicaCountOfChunk(r.fileID, r.nr),2);
		
		assertFalse(r.desiredReplicationDegreeMet());
		
		r.incrementReplicationCount();
		r.incrementReplicationCount();
		r.incrementReplicationCount();
		r.incrementReplicationCount();
		r.incrementReplicationCount();
		r.incrementReplicationCount();
		r.incrementReplicationCount();
		
		assertEquals(r.getReplicaCount(),9);
		assertEquals(d.replicaCountOfChunk(r.fileID, r.nr),9);
		
		assertFalse(r.desiredReplicationDegreeMet());
		
		r.incrementReplicationCount();
		
		assertTrue(r.desiredReplicationDegreeMet());
		assertEquals(r.getReplicaCount(),10);
		assertEquals(d.replicaCountOfChunk(r.fileID, r.nr),10);
		
		r.incrementReplicationCount();
		
		assertTrue(r.desiredReplicationDegreeMet());
		assertEquals(r.getReplicaCount(),11);
		assertEquals(d.replicaCountOfChunk(r.fileID, r.nr),11);
		
		
		
		
		
		
	}
	
	@Test 
	public void testChunkSpaceRemoval() throws Exception{
		
		
		
		S_File.availableSpace = 2560000;
		
		S_File.cleanFolder(new File("backups/"));
		
		new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",1);
		
		SendChunk[] chunks = b.getChunks();
		
		new Database(true);
		
		for (SendChunk sendChunk : chunks) {
			
			new RecieveChunk(sendChunk.fileID, sendChunk.nr, sendChunk.getContent(),1);
		}
		
		RecieveChunk bestToRemove = new RecieveChunk(chunks[7].fileID, chunks[7].nr);
		
		assertEquals(bestToRemove.getReplicaCount(),1);
		
		bestToRemove.incrementReplicationCount();
		
		RecieveChunk bestToRemoveCalculated = RecieveChunk.chunkToRemove();
		
		assertEquals(bestToRemoveCalculated.fileID,bestToRemove.fileID);
		assertEquals(bestToRemove.nr,bestToRemoveCalculated.nr);
		
		assertTrue(new File("backups/"+bestToRemove.fileID+"_"+bestToRemove.nr+".chunk").exists());
		
		bestToRemoveCalculated.cleanup();
		
		assertFalse(new File("backups/"+bestToRemove.fileID+"_"+bestToRemove.nr+".chunk").exists());
		
		try{
			new RecieveChunk(chunks[7].fileID, chunks[7].nr);
			fail("Should have thrown an exception");
		}
		catch(Exception e){
			assertTrue(true);
		}
		
		
	}
	



	
}
