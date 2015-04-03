package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Main.Database;

import org.junit.Before;
import org.junit.Test;

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
			
			RecieveChunk c  = new RecieveChunk("id",0,s.getBytes(),testOutputFile);
			
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
	
		
		
		RecieveChunk c = new RecieveChunk("id",0,s.getBytes(),testOutputFile);
		
		assertTrue(s.equals(new String(c.getContent())));//
		
		
		String contentComingFromFile = new String(c.getContent());
		
		assertEquals(contentComingFromFile,s);
		
		
		RecieveChunk retrivalChunk = new RecieveChunk("id",0);
		
		assertEquals(new String(retrivalChunk.getContent()),new String(c.getContent()));
		
	}

	@Test
	public void testIncrementAndResetOfReplicaCount() throws Exception{
		
		
		new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf");
		
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
		
		
		RecieveChunk c1 = new RecieveChunk("asdasdasff", 0, new String("OLA BOM DIA").getBytes());
		RecieveChunk c2 = new RecieveChunk("asdasdasff", 10, new String("OLA BOA TARDE").getBytes());
		
		
		File f1 = new File(c1.getPath());
		File f2 = new File(c2.getPath());
		
		assertTrue(f1.exists());
		assertTrue(f2.exists());
		
		
		Database d = new Database();
		

		assertNotNull(c1.getPath());
		assertNotNull(c2.getPath());
		
		
		
		Chunk.cleanupChunks("asdasdasff");
		
		
		
		assertFalse(f1.exists());
		assertFalse(f2.exists());
		
		
		
		
		
		
		
		

		
	}


	
}
