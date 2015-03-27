package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import Chunk.FileChunk;
import Main.Database;

public class DatabaseTests {
	
	
	
	public final static String testDBFile = "testFiles/testDB.db";

	 @Before public void initialize() {
	      Database.databaseToUse = testDBFile; 
	}
	@Test
	public void testNoProblemCreatingDBConnection() {
		try {
			new Database();
		} catch (SQLException e) {
			fail("couldn't open db");
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testDeploymentDBConnection(){
		
		try {
			new Database();
		} catch (SQLException e) {
			fail("couldn't open deployment db");
			e.printStackTrace();
		}
		
		
	}

	@Test
	public void testPlacementAndRetrievalOfPath(){
		
		try {
			Database d = new Database(true);
			
			String originalPath = "testFiles/testChunk1.chunk";
			
			
			
			FileChunk c = new FileChunk("bla",10,new String("ola bom dia").getBytes(),false);
			c.saveToFile(originalPath);
			
			String path = d.getPathForChunk(c);
			
			File f = new File(originalPath);
			
			assertEquals(path, f.getCanonicalPath());
		
			
		} catch (Exception e) {
			fail("Unable open db or clear it");
			e.printStackTrace();
		}
		
	}

	@Test
	public void testDBClearMethod() throws Exception{
		
		
		Database d = new Database(true);
		assertEquals(d.nrChunksStored(),0);
		
		FileChunk c = new FileChunk("asdsad",1,new String("asdasdasdsa").getBytes(),false);
		
		c.saveToFile("testFiles/testChunk2.chunk");
		assertEquals(d.nrChunksStored(),1);
		d.clearData();
		assertEquals(d.nrChunksStored(), 0);
		
		
	}
		
	@Test
	public void attemptToPlaceTwoTimesTheSameChunk() throws Exception{
		
		
		String path = "testFiles/testChunk3.chunk";
		String content = "a fantastic content";
		
		FileChunk c = new FileChunk("dadsad",1,content.getBytes(),false);
		FileChunk c2 = new FileChunk("dadsad",1,content.getBytes(),true);//true or false doesn't matter here
		
		c.saveToFile(path);
		
		
		try{
			c2.saveToFile(path);
			fail("failed to throw exception when repeated chunk was writen");
		}
		catch(Exception e){
			assertTrue(true);
			
		}
		
		
		
		
		
	}
	
	@Test
	public void attemptToAddChunksWithSameNumberButDifferentFile() throws Exception{
		
		
		
		
		Database d = new Database(true);
		String path1 = "testFiles/testChunk4.chunk";
		String path2 = "testFiles/testChunk5.chunk";
		String content = "a fantastic content";
		FileChunk c1 = new FileChunk("dadsad",1,content.getBytes(),false);
		FileChunk c2 = new FileChunk("anotherFileID",1,content.getBytes(),false);
		

		assertEquals(d.nrChunksStored(),0);
		
		try {
			c1.saveToFile(path1);
			assertEquals(d.nrChunksStored(),1);
		} catch (SQLException e) {
			fail("failed while adding first file");
			e.printStackTrace();
		}
		
		try {
			c2.saveToFile(path2);
			assertTrue(true);
		} catch (SQLException e) {
			fail("didn't allow to add a perfectly valid chunk to be added. Probably thinking it was a repeted one");
		}
		
		
		assertEquals(d.nrChunksStored(),2);
		
		
		
	}
	
	@Test
	public void attemptToAddChunksWithSameNumberButDifferentFileInTheSamePath() throws Exception{
		
		
		
		
		
		Database d = new Database(true);
		String path1 = "testFiles/testChunk4.chunk";
		String content = "a fantastic content";
		FileChunk c1 = new FileChunk("dadsad",1,content.getBytes(),false);
		FileChunk c2 = new FileChunk("anotherFileID",1,content.getBytes(),false);
		

		assertEquals(d.nrChunksStored(),0);
		
		try {
			c1.saveToFile(path1);
			assertEquals(d.nrChunksStored(),1);
		} catch (SQLException e) {
			fail("failed while adding first file");
			e.printStackTrace();
		}
		
		try {
			c2.saveToFile(path1);
			fail("didn't notice adding two times the same file");
		} catch (SQLException e) {
			assertTrue(true);
			
		}
		
		
		assertEquals(d.nrChunksStored(),1);
		
		
		
		
		
	}

	@Test
	public void removeChunkThatExists() throws Exception{
		
		Database d = new Database(true);
		
		
		
		FileChunk c = new FileChunk("fileID",10,new String("contentHere").getBytes(),false);
		c.saveToFile("testFiles/testChunk6.chunk");
		
		
		assertEquals(d.nrChunksStored(), 1);
		
		
		d.removeChunk(c);
		
		assertEquals(d.nrChunksStored(), 0);
		
		
		
		
	}
	
	@Test
	public void removeAChunkThatDoesntExist() throws Exception{
		
		Database d = new Database(true);
		
		FileChunk c = new FileChunk("fileID",10,new String("contentHere").getBytes(),"testFiles/testChunk7.chunk",false);
		FileChunk c2 = new FileChunk("anotherFileID",10,new String("anotherContent").getBytes(),false);
		
		
		
		
		assertEquals(d.nrChunksStored(), 1);
		
		d.removeChunk(c2);
		
		assertEquals(d.nrChunksStored(), 1);
		
		d.removeChunk(c);
		
		assertEquals(d.nrChunksStored(), 0);
		
		
	}
	
	@Test
	public void retrieveAllChunksOfAFile() throws Exception{
		
		Database d = new Database(true);
		
		
		
		
		//this calls automatically add the files to the db
		
		assertEquals(d.nrChunksStored(), 0);
		
		new FileChunk("id1", 0, new String("ola").getBytes(),"testFiles/testChunk8.chunk",false);
		
		new FileChunk("id1",1,new String("adeus ").getBytes(),"testFiles/testChunk9.chunk",false);
		
		new FileChunk("id2",0,new String("bla bla bla").getBytes(),"testFiles/testChunk10.chunk",false);
		
		new FileChunk("id1",27,new String(" end").getBytes(),"testFiles/testChunk11.chunk",false);
		

		assertEquals(d.nrChunksStored(),4);
		
		try{
			FileChunk[] chunks = d.chunksForFile("id1", false);
			
			assertEquals(chunks.length,3);
			
			assertEquals(chunks[0].nr,0);
			assertEquals(chunks[1].nr,1);
			assertEquals(chunks[2].nr,27);
			
			chunks = d.chunksForFile("id2", false);
			
			assertEquals(chunks.length,1);
			assertEquals(chunks[0].nr,0);
		}
		catch(Exception e){
			fail("Thrown exception where it shouldn't");
			e.printStackTrace();
		}
		
		
	
		
		
	}
	
	@Test
	public void retriveAllChunksOnlyRestoreOnes() throws Exception{
		
		
		Database d = new Database(true);
		
		
		
		
		//this calls automatically add the files to the db
		
		assertEquals(d.nrChunksStored(), 0);
		
		new FileChunk("id1", 0, new String("ola").getBytes(),"testFiles/testChunk10.chunk",true);
		
		new FileChunk("id1",1,new String("adeus ").getBytes(),"testFiles/testChunk11.chunk",true);
		
		new FileChunk("id2",0,new String("bla bla bla").getBytes(),"testFiles/testChunk12.chunk",false);
		
		new FileChunk("id1",27,new String(" end").getBytes(),"testFiles/testChunk13.chunk",false);
		
		

		
		
		assertEquals(d.nrChunksStored(),4);
		
		
		FileChunk[] chunks = d.chunksForFile("id1", true);
		
		assertEquals(chunks.length,2);
		
		assertEquals(chunks[0].nr,0);
		assertEquals(chunks[1].nr,1);
		
	}

	@Test
	public void addReplicaCount() throws Exception{
		
		Database d = new Database(true);
		
		new FileChunk("blabla",0,new String("content").getBytes(),"testFiles/replicaTest.chunk",false);
		
		assertEquals(d.replicaCountOfChunk("blabla", 0),0);
		
		d.addReplicaCountToChunk("blabla", 0);
		
		assertEquals(d.replicaCountOfChunk("blabla", 0),1);
		
		
	}
}

