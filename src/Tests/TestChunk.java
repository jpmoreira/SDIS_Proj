package Tests;

import static org.junit.Assert.*;
import Main.Chunk;

import org.junit.Test;

public class TestChunk {

	@Test
	public void testSaveChunkToFileAndRetrive() {
	
		String s = "ola bom dia";
		
		
		try {
			Chunk c = new Chunk("id",0,s.getBytes());
			
			assertTrue(s.equals(new String(c.getContent())));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}
	

}
