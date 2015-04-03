package Chunk;

import java.io.IOException;

import Files.FileToBackup;
import Main.Database;


public class SendChunk extends Chunk{

	
	FileToBackup f = null;
	
	
	public SendChunk(String fileID,int number) throws Exception{
		super(fileID,number);
		
		try{
			Database d = new Database();
			d.chunkExists(this);
		}
		catch(Exception e){
			
			throw new Exception("Attempt to load inexisting SendChunk");
		}

	
		
	}
	
	public SendChunk(int nr, FileToBackup file){
		
		
		super(file.getFileID(),nr);
		
		f = file;
		
		try{
			
			Database d = new Database();
			d.addChunk(this);
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		
		
		
		
		
	}
	
	@Override
	public byte[] getContent() throws IOException {
		
		return f.contentForChunk(this.nr);
	}
	
	
	
	

}
