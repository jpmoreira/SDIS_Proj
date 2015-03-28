package Tests;

import static org.junit.Assert.*;

import Chunk.RecieveChunk;
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
	public void testLoadOfChunkRestoreFlag() throws Exception{
		
		
		String s = "ola bom dia";
		
		new Database(true);//we have to clear the db first
		
		
		RecieveChunk c = new RecieveChunk("id",0,s.getBytes(),"testFiles/testChunk14.chunk",false);
		RecieveChunk c2 = new RecieveChunk("id2",27,s.getBytes(),"testFiles/testChunk15.chunk",true);
		
		RecieveChunk c3 = new RecieveChunk("id",0,true);
		RecieveChunk c4 = new RecieveChunk("id2",27,true);
		
		
		assertEquals(c.isOwn(),c3.isOwn());
		assertEquals(c2.isOwn(),c4.isOwn());
		
		
	}
}
