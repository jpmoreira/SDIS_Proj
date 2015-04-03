package Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


import Files.FileToBackup;
import Main.Database;


public class SendChunk extends Chunk{

	
	FileToBackup f = null;
	String path = null;
	
//TODO test loading sendchunk
	public SendChunk(String fileID,int number) throws Exception{
		super(fileID,number);
		
		
		try{
			Database d = new Database();
			d.chunkExists(this);
			path = d.getPathForChunk(this);
			
			if(path == null)throw new Exception();
			
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
	public int desiredReplicationDegree() {
		return f.desiredRepDegree;
	}
	
	@Override
	public byte[] getContent() throws IOException {
		
		
		if(f != null){
			return f.contentForChunk(this.nr);
		}
		else{
			
			File file = new File(this.getPath());
			FileInputStream is = new FileInputStream(this.getPath());
			byte[] buffer = new byte[(int)file.length()];
			is.read(buffer);
			is.close();
			return buffer;
		}
		
	}
	
	@Override
	public String getPath() {
		
		if(f != null)return null;
		else return this.path;
		
	}
	
	
	
	//TODO recievechunk é facultativo o repDeg
	

}
