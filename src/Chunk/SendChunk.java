package Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Files.FileToBackup;
import Main.Database;


public class SendChunk extends Chunk{

	
	FileToBackup f = null;
	String path = null;
	
	public SendChunk(String fileID,int number) throws Exception{
		super(fileID,number);
		
		
		try{
			Database d = new Database();
			if(!d.chunkExists(this))throw new Exception();
			path = d.getPathForChunk(this);
			
			//if(path == null)throw new Exception();
			
			if(path == null)f = new FileToBackup(d.filePathForBackedFile(fileID));
			
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
	public byte[] getContent() {
		
		
		if(f != null){
			
			try{
				return f.contentForChunk(this.nr);
				
			}
			catch(IOException e){return new byte[0];}
			
		}
		else{
			
			if(this.path == null)return null;
			
			File file = new File(this.getPath());
			byte[] buffer = new byte[0];
			try {
				FileInputStream is = new FileInputStream(this.getPath());
				buffer = new byte[(int)file.length()];
				is.read(buffer);
				is.close();
			} catch (IOException e) {}
			
			return buffer;
		}
		
	}
	
	@Override
	public String getPath() {
		
		if(f != null)return null;
		else return this.path;
		
	}
	
	

	

}
