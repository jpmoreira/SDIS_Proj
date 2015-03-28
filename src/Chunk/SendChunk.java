package Chunk;

import java.io.IOException;

import Files.FileToBackup;
import Main.Database;


public class SendChunk extends Chunk{

	
	FileToBackup f = null;
	
	
	public SendChunk(String fileID,int number,boolean own) throws Exception{
		super(fileID,number,own);
	
		
	}
	
	public SendChunk(int nr, FileToBackup file){
		
		
		super(file.getFileID(),nr,true);// a chunk that holds a pointer to a file is indeed a chunk that is from a file of ours
		
		f = file;
		
		try{
			
			Database d = new Database();
			d.addChunk(this);
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		
		
		
		
		
	}
	

	@Override
	public boolean isOwn() {
		return true;
	}
	
	
	@Override
	public byte[] getContent() throws IOException {
		
		return f.contentForChunk(this.nr);
	}
	
	
	
	

}
