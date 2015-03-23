package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import Main.Chunk;
import Main.S_File;

public class TestSFile {

	@Test
	public void testCreateFileThatExists() {
		
		try {
			S_File file = new S_File("testFiles/oneChunkFile");
		} catch (Exception e) {
			fail("coudn't open file that acctually exists");
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testReadInexistentChunk() throws Exception{
		
		
		S_File file = new S_File("testFiles/oneChunkFile");
		
		assertNull(file.getChunk(1));
		
	}
	
	
	@Test
	public void testReadExistingChunk() throws Exception{
		
		S_File file = new S_File("testFiles/oneChunkFile");
		
		String s = new String(file.getChunk(0).getContent());
		
		//now read from file all content
		String s2 = new String(Files.readAllBytes(Paths.get("testFiles/oneChunkFile")));
		
		//compare results
		assertEquals(s, s2);
		
	}

	@Test
	public void testLastChunkOn64KFile() throws Exception{
		
		
		S_File s = new S_File("testFiles/twoChunkFileWithLastChunkEmpty");
		
		assertNotNull(s.getChunk(0));
		assertNotNull(s.getChunk(1));
		assertEquals(s.getChunk(1).getContent().length, 0);
	}
	
	@Test
	public void testLastChunkOn64Kplus1BytesFile() throws Exception{
		
		S_File s = new S_File("testFiles/twoChunkFileWithLastChunkWithOneCharOnly");
		
		Chunk c = s.getChunk(1);
		
		String str = new String(c.getContent());
		
		assertEquals(str,"a");
	}
}
