package Files;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import Chunk.Chunk;
import Chunk.FileChunk;

public class FileToRestore extends S_File{

	
	static String pathToRecoveryFolder = null;
	

	ArrayList<FileChunk> chunks = new ArrayList<FileChunk>();
	
	String fileID = null;
	
	
	/**
	 * 
	 * This method creates a file in a specific location given all it's chunks. If a file exists on the specified location it will be overwritten
	 * 
	 * @param filePath the path to the file to create 
	 * @param chunks the chunks that constitute the file
	 * @throws Exception 
	 */
	public FileToRestore(String filePath, FileChunk[] chunks) throws Exception{
		
		Arrays.sort(chunks);
		
		String presumedFileID = chunks[0].fileID;
		
		int lastNr = chunks[0].nr;
		
		if(lastNr!=0) throw new Exception("Missing first chunk");
		
		lastNr = -1; 
		
		for (FileChunk chunk : chunks) {
			
			if(!chunk.fileID.equals(presumedFileID))throw new Exception("Chunks with multiple file ids provided");
			lastNr++;
			if(lastNr!=chunk.nr)throw new Exception("Missing chunk nr "+lastNr);
		}
		
		if(chunks[chunks.length-1].getContent().length == 64000 )throw new Exception("Last provided chunk is not the last chunk");
		
		
		FileOutputStream fos = new FileOutputStream(filePath);
		

		for (Chunk chunk : chunks) {
			
			fos.write(chunk.getContent());
			
		}
		
		fos.close();
		
		
		

		
		
	}
	
	public int getNrChunks(){
		
		//TODO implement it
		
		
		return -1;
	}
	
	public String getFileID(){
		
		return fileID;
	}


	public void addChunk(FileChunk chunk){
		
		if(fileID == null)fileID = chunk.fileID;//if we don't have a fileID set it
		
		chunks.add(chunk);
		
		if(chunk.inMemory()){//try to save it
			try{
				chunk.saveToFile(pathToRecoveryFolder+fileID+"_"+chunk.nr);
			}
			catch(Exception e){}
		}
		
		
		
		//TODO implement it
		
		
	}
}
