package Tests;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import Main.Chunk;
import Main.Database;

public class DatabaseTests {
	
	
	
	public final static String testDBFile = "testFiles/testDB.db";

	 @Before public void initialize() {
	      Database.databaseToUse = testDBFile; 
	}
	@Test
	public void testNoProblemCreatingDBConnection() {
		try {
			Database s = new Database();
		} catch (SQLException e) {
			fail("couldn't open db");
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testDeploymentDBConnection(){
		
		try {
			Database s = new Database();
		} catch (SQLException e) {
			fail("couldn't open deployment db");
			e.printStackTrace();
		}
		
		
	}

	@Test
	public void testPlacementAndRetrievalOfPlace(){
		
		try {
			Database d = new Database(true);
			
			String originalPath = "a/given/path/to/a/file.chunk";
			
			Chunk c = new Chunk("bla",10,new String("ola bom dia").getBytes());
			
			d.addChunk(c, originalPath);
			
			String path = d.getPathForChunk(c);
			
			assertEquals(path, originalPath);
		
			
		} catch (Exception e) {
			fail("Unable open db or clear it");
			e.printStackTrace();
		}
		
	}

	@Test
	public void testDBClearMethod() throws SQLException{
		
		
		Database d = new Database(true);
		assertEquals(d.nrChunksStored(),0);
		
		Chunk c = new Chunk("asdsad",1,new String("asdasdasdsa").getBytes());
		
		d.addChunk(c, "some/path.chunk");
		
		assertEquals(d.nrChunksStored(),1);
		
		d.clearData();
		
		assertEquals(d.nrChunksStored(), 0);
		
		
	}
	
	
	@Test
	public void attemptToPlaceTwoTimesTheSameChunk() throws Exception{
		
		
		Database d = null;
		String path = "a/given/path/to/be/repeted.chunk";
		String content = "a fantastic content";
		Chunk c = new Chunk("dadsad",1,content.getBytes());
		try {
			d = new Database(true);
		} catch (SQLException e) {
			fail("failed to create db");
			e.printStackTrace();
		}
		
		try {
			d.addChunk(c, path);
		} catch (SQLException e) {
			fail("failed while adding first file");
			e.printStackTrace();
		}
		
		try {
			d.addChunk(c, path);
			fail("failed to throw exception when repeated chunk was writen");
		} catch (SQLException e) {
			assertTrue(true);
		}
		
		
		
		
		
	}
	
	@Test
	public void attemptToAddChunksWithSameNumberButDifferentFile() throws Exception{
		
		
		
		
		Database d = null;
		String path1 = "a/given/path/to/be/tested.chunk";
		String path2 = "another/path/that/cannot/be/the/same.chunk";
		String content = "a fantastic content";
		Chunk c1 = new Chunk("dadsad",1,content.getBytes());
		Chunk c2 = new Chunk("anotherFileID",1,content.getBytes());
		try {
			d = new Database(true);
		} catch (SQLException e) {
			fail("failed to create db");
			e.printStackTrace();
		}
		
		try {
			d.addChunk(c1, path1);
		} catch (SQLException e) {
			fail("failed while adding first file");
			e.printStackTrace();
		}
		
		try {
			d.addChunk(c2, path2);//path cannot be the same!
			assertTrue(true);
		} catch (SQLException e) {
			fail("didn't allow to add a perfectly valid chunk to be added. Probably thinking it was a repeted one");
		}
		
		
		
	}
	
	@Test
	public void attemptToAddChunksWithSameNumberButDifferentFileInTheSamePath() throws Exception{
		
		
		
		
		Database d = null;
		String path = "a/given/path/to/be/tested.chunk";
		String content = "a fantastic content";
		Chunk c1 = new Chunk("dadsad",1,content.getBytes());
		Chunk c2 = new Chunk("anotherFileID",1,content.getBytes());
		try {
			d = new Database(true);
		} catch (SQLException e) {
			fail("failed to create db");
			e.printStackTrace();
		}
		
		try {
			d.addChunk(c1, path);
		} catch (SQLException e) {
			fail("failed while adding first file");
			e.printStackTrace();
		}
		
		try {
			d.addChunk(c2, path);//path cannot be the same!
			fail("failed to notice that the path where the chunk is being added is already in use!");
		} catch (SQLException e) {
			assertTrue(true);
			
		}
		
		
		
	}

	@Test
	public void removeChunkThatExists() throws SQLException{
		
		Database d = new Database(true);
		
		Chunk c = new Chunk("fileID",10,new String("contentHere").getBytes());
		
		d.addChunk(c, "a/path/to/the/file/that/has/the/chunk/in/it.chunk");
		
		assertEquals(d.nrChunksStored(), 1);
		
		
		d.removeChunk(c);
		
		assertEquals(d.nrChunksStored(), 0);
		
		
		
		
	}
	
	@Test
	public void removeAChunkThatDoesntExist() throws SQLException{
		
		Database d = new Database(true);
		
		Chunk c = new Chunk("fileID",10,new String("contentHere").getBytes());
		Chunk c2 = new Chunk("anotherFileID",10,new String("anotherContent").getBytes());
		
		d.addChunk(c, "a/path/to/the/file/that/has/the/chunk/in/it.chunk");
		
		assertEquals(d.nrChunksStored(), 1);
		
		d.removeChunk(c2);
		
		assertEquals(d.nrChunksStored(), 1);
		
	}
	
	
	

}

