package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Files.FileToBackup;
import Files.FileToRestore;
import Main.Database;

public class TestSFile {

	@Test
	public void testCreateFileThatExists() {
		
		try {
			new FileToBackup("testFiles/oneChunkFile");
		} catch (Exception e) {
			fail("coudn't open file that acctually exists");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testReadInexistentChunk() throws Exception{
		
		
		FileToBackup file = new FileToBackup("testFiles/oneChunkFile");
		
		assertNull(file.getChunk(1));
		
	}
	
	
	@Test
	public void testReadExistingChunk() throws Exception{
		
		FileToBackup file = new FileToBackup("testFiles/oneChunkFile");
		
		String s = new String(file.getChunk(0).getContent());
		
		//now read from file all content
		String s2 = new String(Files.readAllBytes(Paths.get("testFiles/oneChunkFile")));
		
		//compare results
		assertEquals(s, s2);
		
	}

	@Test
	public void testLastChunkOn64KFile() throws Exception{
		
		
		FileToBackup s = new FileToBackup("testFiles/twoChunkFileWithLastChunkEmpty");
		
		assertNotNull(s.getChunk(0));
		assertNotNull(s.getChunk(1));
		assertEquals(s.getChunk(1).getContent().length, 0);
	}
	
	@Test
	public void testLastChunkOn64Kplus1BytesFile() throws Exception{
		
		FileToBackup s = new FileToBackup("testFiles/twoChunkFileWithLastChunkWithOneCharOnly");
		
		Chunk c = s.getChunk(1);
		
		String str = new String(c.getContent());
		
		assertEquals(str,"a");
	}


	@Test
	public void testSha256AsHexAndBack() throws Exception{

		
		FileToBackup s = new FileToBackup("testFiles/oneChunkFile");
		
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
		
		FileToBackup s1 = new FileToBackup("testFiles/oneChunkFile");
		FileToBackup s3 = new FileToBackup("testFiles/oneChunkFile");		
		FileToBackup s2 = new FileToBackup("testFiles/oneChunkFile2");
		
		assertEquals(s1.getFileID(),s3.getFileID());
		assertNotEquals(s1.getFileID(),s2.getFileID());
		
		
		
		
	}
	
	@Test
	public void testFilePartitionAndReassembly() throws Exception{
		
		
		
		FileInputStream f1 = new FileInputStream("testFiles/RIGP.pdf");
		byte[] f1Buffer = new byte[(int) new File("testFiles/RIGP.pdf").length()];
		f1.read(f1Buffer);
		f1.close();
		
		
		new Database(true);
		
		FileToBackup file = new FileToBackup("testFiles/RIGP.pdf");
		file.addToBackupRegistry();
		String fileID = file.getFileID();
		
		
		for(int i = 0 ; i<file.getNrChunks(); i++){
			
			file.getChunk(i);
			
		}
		
		try{
		
			RecieveChunk[] chunksArray = new RecieveChunk[file.getNrChunks()];
			for(int i = 0 ;i <file.getNrChunks(); i++){
				chunksArray[i]=new RecieveChunk(fileID,i,file.getChunk(i).getContent(),"testFiles/RIGPChunks/chunk"+i,true);
			}
		
		

			new File("testFiles/RIGP.pdf").delete();
			new FileToRestore(fileID, chunksArray);
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		
	

	

		FileInputStream f2 = new FileInputStream("testFiles/RIGP.pdf");
		byte[] f2Buffer = new byte[(int) new File("testFiles/RIGP.pdf").length()];
		f2.read(f2Buffer);
		f2.close();
		
		
		assertEquals(f1Buffer.length,f2Buffer.length);
		for(int i = 0 ; i < f1Buffer.length; i++){
			
			assertEquals(f1Buffer[i],f2Buffer[i]);
			
		}
		
		
	}

}
