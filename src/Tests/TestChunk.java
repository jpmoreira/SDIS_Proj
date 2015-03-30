package Tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

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
			
			RecieveChunk c  = new RecieveChunk("id",0,s.getBytes(),false);
			
			
			
			assertTrue(s.equals(new String(c.getContent())));//
			
			c.saveToFile(testOutputFile);
			
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
	
		
		
		RecieveChunk c = new RecieveChunk("id",0,s.getBytes(),testOutputFile,false);
		
		assertTrue(s.equals(new String(c.getContent())));//
		
		c.saveToFile(testOutputFile);
		
		String contentComingFromFile = new String(c.getContent());
		
		assertEquals(contentComingFromFile,s);
		
		
		RecieveChunk retrivalChunk = new RecieveChunk("id",0,true);
		
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
}
