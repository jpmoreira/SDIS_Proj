package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.FileToRestore;
import Main.Database;

public class DatabaseTests {
	
	
	
	public final static String testDBFile = "testFiles/testDB.db";

	 @Before 
	 public void initialize() {
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
			
			
			
			RecieveChunk c = new RecieveChunk("bla",10,new String("ola bom dia").getBytes(),originalPath);
			
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
		
		RecieveChunk c = new RecieveChunk("asdsad",1,new String("asdasdasdsa").getBytes(),"testFiles/testChunk2.chunk");
		
		assertEquals(d.nrChunksStored(),1);
		d.clearData();
		assertEquals(d.nrChunksStored(), 0);
		
		
	}
		
	@Test
	public void attemptToPlaceTwoTimesTheSameChunk() throws Exception{
		
		
		String path = "testFiles/testChunk3.chunk";
		String content = "a fantastic content";
		
		RecieveChunk c = new RecieveChunk("dadsad",1,content.getBytes(),path);

		
		try{
			new RecieveChunk("dadsad",1,content.getBytes());//true or false doesn't matter here
		
			fail("failed to throw exception when repeated chunk was writen");
		}
		catch(Exception e){
			assertTrue(true);
			
		}
		
		
		
		
		
	}
	
	@Test
	public void attemptToAddChunksWithSameNumberButDifferentFile() throws Exception{
		
		
		
		
		Database d = new Database(true);
		String content = "a fantastic content";
		new RecieveChunk("dadsad",1,content.getBytes());
		new RecieveChunk("anotherFileID",1,content.getBytes());
		assertEquals(d.nrChunksStored(),2);

		
	}
	
	@Test
	public void attemptToAddChunksWithSameNumberButDifferentFileInTheSamePath() throws Exception{
		
		
		
		
		
		Database d = new Database(true);
		String path1 = "testFiles/testChunk4.chunk";
		String content = "a fantastic content";
		new RecieveChunk("dadsad",1,content.getBytes(),path1);
		
		try{
			
			new RecieveChunk("anotherFileID",1,content.getBytes(),path1);
				
			fail("didn't notice adding two times the same file");
		}
		catch(Exception e){
			
			assertTrue(true);
			
		}
		assertEquals(d.nrChunksStored(),1);
		
		
		
		
		
	}

	@Test
	public void removeChunkThatExists() throws Exception{
		
		Database d = new Database(true);
		
		
		
		RecieveChunk c = new RecieveChunk("fileID",10,new String("contentHere").getBytes(),"testFiles/testChunk6.chunk");
		
		
		assertEquals(d.nrChunksStored(), 1);
		
		
		d.removeChunk(c);
		
		assertEquals(d.nrChunksStored(), 0);
		
		
		
		
	}
	
	@Test
	public void removeAChunkThatDoesntExist() throws Exception{
		
		Database d = new Database(true);
		
		RecieveChunk c = new RecieveChunk("fileID",10,new String("contentHere").getBytes(),"testFiles/testChunk7.chunk");
		RecieveChunk c2 = new RecieveChunk("anotherFileID",10,new String("anotherContent").getBytes());
		
		
		
		
		assertEquals(d.nrChunksStored(), 2);
		
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
		
		new RecieveChunk("id1", 0, new String("ola").getBytes(),"testFiles/testChunk8.chunk");
		
		new RecieveChunk("id1",1,new String("adeus ").getBytes(),"testFiles/testChunk9.chunk");
		
		new RecieveChunk("id2",0,new String("bla bla bla").getBytes(),"testFiles/testChunk10.chunk");
		
		new RecieveChunk("id1",27,new String(" end").getBytes(),"testFiles/testChunk11.chunk");
		

		assertEquals(d.nrChunksStored(),4);
		
		try{
			RecieveChunk[] chunks = d.chunksForFile("id1");
			
			assertEquals(chunks.length,3);
			
			assertEquals(chunks[0].nr,0);
			assertEquals(chunks[1].nr,1);
			assertEquals(chunks[2].nr,27);
			
			chunks = d.chunksForFile("id2");
			
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
		
		new RecieveChunk("id1", 0, new String("ola").getBytes(),"testFiles/testChunk10.chunk");
		
		new RecieveChunk("id1",1,new String("adeus ").getBytes(),"testFiles/testChunk11.chunk");
		
		new RecieveChunk("id2",0,new String("bla bla bla").getBytes(),"testFiles/testChunk12.chunk");
		
		new RecieveChunk("id1",27,new String(" end").getBytes(),"testFiles/testChunk13.chunk");
		
		

		
		
		assertEquals(d.nrChunksStored(),4);
		
		
		RecieveChunk[] chunks = d.chunksForFile("id1");
		
		assertEquals(chunks.length,3);
		
		assertEquals(chunks[0].nr,0);
		assertEquals(chunks[1].nr,1);
		
	}

	@Test
	public void addReplicaCount() throws Exception{
		
		Database d = new Database(true);
		
		new RecieveChunk("blabla",0,new String("content").getBytes(),"testFiles/replicaTest.chunk");
		
		assertEquals(d.replicaCountOfChunk("blabla", 0),0);
		
		d.addReplicaCountToChunk("blabla", 0);
		
		assertEquals(d.replicaCountOfChunk("blabla", 0),1);
		
		
	}


	@Test
	public void addFileToBackupRecord() throws Exception{
		
		
		new Database(true);
		
		
		FileToBackup f = new FileToBackup("testFiles/twoChunkFileWithLastChunkEmpty");
		//f.addToBackupRegistry();
		
		FileToRestore r = new FileToRestore(f.getFileID(), null);
		
		assertEquals(f.getFileID(),r.getFileID());
		assertEquals(f.getFilePath(),r.getFilePath());
		assertEquals(f.getNrChunks(),f.getNrChunks());
		
	}

	@Test
	public void testOwnFileCheck() throws Exception {
		
		Database d = new Database(true);
		
		FileToBackup f = new FileToBackup("testFiles/RIGP.pdf");
		//f.addToBackupRegistry();
		
		boolean isOurOwn = d.isOurFile(f.getFileID());
		
		assertTrue(isOurOwn);
	}


	@Test
	public void testRemovalOfPaths() throws Exception {
		
		
		Database d = new Database(true);
		FileToBackup b = null;
		try{
			b = new FileToBackup("testFiles/twoChunkFileWithLastChunkEmpty");
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
		
		SendChunk[] sc = b.getChunks();
		
		RecieveChunk[] rc = new RecieveChunk[sc.length];
		
		try{
			for(int i = 0 ; i < sc.length ; i++){
				
				rc[i] = new RecieveChunk(sc[i].fileID, sc[i].nr, sc[i].getContent());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		
		FileToRestore r = new FileToRestore(b.getFileID());
		
		for (RecieveChunk recieveChunk : rc) {
			
			r.addChunk(recieveChunk);
			
		}
		
		//r.resconstructFile();
		
		
		
		RecieveChunk[] rrc = d.chunksForFile(b.getFileID());
		
		for (RecieveChunk recieveChunk : rrc) {
			
			assertNotNull(recieveChunk.getPath());
		}
		
		
		d.removePathsForChunksOfFile(b.getFileID());
		
		RecieveChunk[] c = d.chunksForFile(b.getFileID());
		
		assertEquals(c.length, 0);
	
		
	}
}


