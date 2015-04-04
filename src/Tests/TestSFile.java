package Tests;

//TODO test if removal is done properly

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.junit.Test;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.FileToRestore;
import Main.Database;

public class TestSFile {

	@Test
	public void testCreateFileThatExists() {
		
		try {
			new FileToBackup("testFiles/oneChunkFile",5);
		} catch (Exception e) {
			fail("coudn't open file that acctually exists");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testReadInexistentChunk() throws Exception{
		
		
		FileToBackup file = new FileToBackup("testFiles/oneChunkFile",7);
		
		assertNull(file.getChunk(1));
		
	}
	
	
	@Test
	public void testReadExistingChunk() throws Exception{
		
		FileToBackup file = new FileToBackup("testFiles/oneChunkFile",10);
		
		String s = new String(file.getChunk(0).getContent());
		
		//now read from file all content
		String s2 = new String(Files.readAllBytes(Paths.get("testFiles/oneChunkFile")));
		
		//compare results
		assertEquals(s, s2);
		
	}

	@Test
	public void testLastChunkOn64KFile() throws Exception{
		
		
		FileToBackup s = new FileToBackup("testFiles/twoChunkFileWithLastChunkEmpty",5);
		
		assertNotNull(s.getChunk(0));
		assertNotNull(s.getChunk(1));
		assertEquals(s.getChunk(1).getContent().length, 0);
	}
	
	@Test
	public void testLastChunkOn64Kplus1BytesFile() throws Exception{
		
		FileToBackup s = new FileToBackup("testFiles/twoChunkFileWithLastChunkWithOneCharOnly",7);
		
		Chunk c = s.getChunk(1);
		
		String str = new String(c.getContent());
		
		assertEquals(str,"a");
	}


	@Test
	public void testSha256AsHexAndBack() throws Exception{

		
		FileToBackup s = new FileToBackup("testFiles/oneChunkFile",10);
		
		String id = s.getFileID();
		
		byte[] b = FileToBackup.hexToBytes(id);
		byte[] originalB = s.sha256();
		
		
		for ( int i = 0 ; i < b.length ; i++){
			
			assertEquals(b[i],originalB[i]);
		}
		
	}


	@Test
	public void testShaDifferentForDifferentDateFileWithSameContent() throws Exception{
		
		

		FileInputStream fis = new FileInputStream("testFiles/oneChunkFile");
		
		byte[] buffer = new byte[256];
		int readSize=fis.read(buffer);
		
		fis.close();
		
		FileOutputStream fos = new FileOutputStream("testFiles/oneChunkFile2");
		fos.write(buffer,0,readSize);
		fos.close();
		
		FileToBackup s1 = new FileToBackup("testFiles/oneChunkFile",6);
		FileToBackup s3 = new FileToBackup("testFiles/oneChunkFile",5);		
		FileToBackup s2 = new FileToBackup("testFiles/oneChunkFile2",4);
		
		assertEquals(s1.getFileID(),s3.getFileID());
		assertNotEquals(s1.getFileID(),s2.getFileID());
		
		
		
		
	}
	
	@Test
	public void testFilePartitionAndReassembly() throws Exception{
		
		
		
		FileInputStream f1 = new FileInputStream("testFiles/RIGP.pdf");
		byte[] f1Buffer = new byte[(int) new File("testFiles/RIGP.pdf").length()];
		f1.read(f1Buffer);
		f1.close();
		
		FileOutputStream fw = new FileOutputStream("testFiles/RIGP-copy.pdf");
		fw.write(f1Buffer);
		fw.close();
		
		
		new Database(true);
		
		FileToBackup file = new FileToBackup("testFiles/RIGP-copy.pdf",10);
		//file.addToBackupRegistry();
		String fileID = file.getFileID();
		
		
		for(int i = 0 ; i<file.getNrChunks(); i++){
			
			file.getChunk(i);
			
		}
		
		try{
		
			
			FileToRestore r = new FileToRestore(fileID);
			try{
				r.reconstructFile();
				fail("didn't throw exception but should have");
				
			}
			catch(Exception e ){
				
				assertTrue(true);
			}
			
			/*
			RecieveChunk[] chunksArray = new RecieveChunk[file.getNrChunks()];
			for(int i = 0 ;i <file.getNrChunks(); i++){
				chunksArray[i]=new RecieveChunk(fileID,i,file.getChunk(i).getContent(),"testFiles/RIGPChunks/chunk"+i);
			}
		
			*/
		


			r = new FileToRestore(fileID);
			

			
			new RecieveChunk(fileID,0,file.getChunk(0).getContent(),"testFiles/RIGPChunks/chunk0");
			
			
			

			
			for(int i = 1 ;i <file.getNrChunks(); i++){
				new RecieveChunk(fileID,i,file.getChunk(i).getContent(),"testFiles/RIGPChunks/chunk"+i);
			}
			
			new File("testFiles/RIGP-copy.pdf").delete();

			File theFileThatDoesntExist = new File("testFiles/RIGP-copy.pdf");
			
			assertFalse(theFileThatDoesntExist.exists());
			
			assertFalse(r.isRestored());
			r.reconstructFile();
			assertTrue(r.isRestored());
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		
	

	

		FileInputStream f2 = new FileInputStream("testFiles/RIGP.pdf");
		byte[] f2Buffer = new byte[(int) new File("testFiles/RIGP-copy.pdf").length()];
		f2.read(f2Buffer);
		f2.close();
		
		
		assertEquals(f1Buffer.length,f2Buffer.length);
		for(int i = 0 ; i < f1Buffer.length; i++){
			
			assertEquals(f1Buffer[i],f2Buffer[i]);
			
		}
		
		
		
		
	}

	@Test
	public void testMissingChunksMethod() throws Exception {
		
		new Database(true);
		
		FileInputStream fi = new FileInputStream("testFiles/RIGP.pdf");
		byte[] buffer = new byte[(int) new File("testFiles/RIGP.pdf").length()];
		fi.read(buffer);
		
		fi.close();
		
		FileOutputStream fo = new FileOutputStream("testFiles/RIGP-clone.pdf");
		fo.write(buffer);
		
		fo.close();
		
		
		FileToBackup fb = new FileToBackup("testFiles/RIGP-clone.pdf",10);
		
		SendChunk[] chunks = fb.getChunks();
		
		RecieveChunk[] rec_chunks = new RecieveChunk[chunks.length];
		
		/*
		for(int i = 0 ; i < chunks.length ; i++){
			
			rec_chunks[i] = new RecieveChunk(chunks[i].fileID, chunks[i].nr,chunks[i].getContent());
		}
		*/
		
		FileToRestore r = new FileToRestore(fb.getFileID());
		
		Integer[] missing = r.missingChunkNrs();
		
		for(int i = 0 ; i < chunks.length ; i ++) assertEquals(missing[i].intValue(),chunks[i].nr);
		
		
		new RecieveChunk(chunks[0].fileID, chunks[0].nr,chunks[0].getContent());
		
		
		
		missing = r.missingChunkNrs();
		
		for(int i = 0 ; i < missing.length ; i ++) assertEquals(missing[i].intValue(),chunks[i+1].nr);
		
		
		new RecieveChunk(chunks[7].fileID, chunks[7].nr,chunks[7].getContent());
		missing = r.missingChunkNrs();
		assertEquals(r.chunks.size(),2);
		
		for(int i = 0 ; i < 6; i++) assertEquals(missing[i].intValue(),i+1);
		
		for(int i = 6 ; i < missing.length; i++) assertEquals(missing[i].intValue(),i+2);
		
		
		for(int i = 0 ; i < rec_chunks.length; i++){
			
			new RecieveChunk(chunks[i].fileID, chunks[i].nr,chunks[i].getContent());
			
		}
		
		r.missingChunkNrs();//to force chunks array actualization
		assertEquals(r.chunks.size(),rec_chunks.length);
		
		assertEquals(r.missingChunkNrs().length,0);
		
	}

	@Test
	public void testCleanup() throws Exception{
		
		
		Database d = new Database(true);
		
		FileInputStream fi = new FileInputStream("testFiles/RIGP.pdf");
		byte[] buffer = new byte[(int) new File("testFiles/RIGP.pdf").length()];
		fi.read(buffer);
		
		fi.close();
		
		FileOutputStream fo = new FileOutputStream("testFiles/RIGP-clone.pdf");
		fo.write(buffer);
		
		fo.close();
		
		FileToBackup fb = new FileToBackup("testFiles/RIGP-clone.pdf",10);
		
		SendChunk[] chunks = fb.getChunks();
		
		RecieveChunk[] rec_chunks = new RecieveChunk[chunks.length];
		
		for(int i = 0 ; i < chunks.length ; i++){
			
			rec_chunks[i] = new RecieveChunk(chunks[i].fileID, chunks[i].nr,chunks[i].getContent());
		}
		
		FileToRestore r = new FileToRestore(fb.getFileID());
		
		for(int i = 0 ; i < rec_chunks.length; i++){
			File f = new File(rec_chunks[i].getPath());
			
			assertTrue(f.exists());
			assertTrue(f.isFile());
			assertNotNull(d.getPathForChunk(rec_chunks[i]));
			
		}
		
		r.reconstructFile();
		r.cleanup();
		
		
		
		
		for(int i = 0 ; i < rec_chunks.length; i++){
			
			assertNull(rec_chunks[i].getPath());
			
		}
		
	}

	@Test 
	public void testListOfBackedUpFiles() throws Exception{
		

		
		new Database(true);

		assertEquals(FileToBackup.backedFiles().length,0);
		
		new FileToBackup("testFiles/RIGP.pdf",10);
		
		new FileToBackup("testFiles/oneChunkFile",10);
		
		assertEquals(FileToBackup.backedFiles().length, 2);
		
		assertEquals(FileToBackup.backedFiles()[0], new File("testFiles/RIGP.pdf").getCanonicalPath());
		
		assertEquals(FileToBackup.backedFiles()[1], new File("testFiles/oneChunkFile").getCanonicalPath());
		
		
		
		
		
		
		
	}

	@Test
	public void testRemovalOfBackupFiles() throws Exception{
		
		Database d = new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",10);
		new FileToBackup("testFiles/RIGP.pdf");
		
		assertEquals(d.backedFilePaths().length,1);
		
		b.remove();
		
		assertEquals(d.backedFilePaths().length, 0);
		
		assertTrue(true);
		
		
	}

	@Test
	public void testPathToFileID() throws Exception{
		
		new Database(true);
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",10);
		
		String fileID = FileToRestore.fileIDForBackedFile("testFiles/RIGP.pdf");
		
		assertNotNull(fileID);
		
		assertEquals(fileID,b.getFileID());
		
		
		
		
	}

	@Test
	public void testFileDesiredRepDegreeInDB() throws Exception{
		
		
		Database d = new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf", 7);
		
		
		assertEquals(b.desiredRepDegree,7);
		assertEquals(7,d.getDesiredReplicationDegreeForFile(b.getFileID()));
		
		
	}
}
