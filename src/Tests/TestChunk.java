package Tests;

import static org.junit.Assert.*;

import Chunk.FileChunk;
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
			
			FileChunk c  = new FileChunk("id",0,s.getBytes(),false);
			
			
			
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
	
		
		
		FileChunk c = new FileChunk("id",0,s.getBytes(),testOutputFile,false);
		
		assertTrue(s.equals(new String(c.getContent())));//
		
		c.saveToFile(testOutputFile);
		
		String contentComingFromFile = new String(c.getContent());
		
		assertEquals(contentComingFromFile,s);
		
		
		FileChunk retrivalChunk = new FileChunk("id",0);
		
		assertEquals(new String(retrivalChunk.getContent()),new String(c.getContent()));
		
	}

	@Test
	public void testLoadOfChunkRestoreFlag() throws Exception{
		
		
		String s = "ola bom dia";
		
		new Database(true);//we have to clear the db first
		
		
		FileChunk c = new FileChunk("id",0,s.getBytes(),"testFiles/testChunk14.chunk",false);
		FileChunk c2 = new FileChunk("id2",27,s.getBytes(),"testFiles/testChunk15.chunk",true);
		
		FileChunk c3 = new FileChunk("id",0);
		FileChunk c4 = new FileChunk("id2",27);
		
		
		assertEquals(c.restore,c3.restore);
		assertEquals(c2.restore,c4.restore);
		
		
	}
}
