package Files;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Main.Database;

public class FileToRestore implements S_File{

	
	static String pathToRecoveryFolder = null;
	

	ArrayList<RecieveChunk> chunks = new ArrayList<RecieveChunk>();
	
	public String fileID = null;
	String filePath = null;
	int nrChunks = -1;
	
	
	/**
	 * 
	 * This method creates a file in a specific location given all it's chunks. If a file exists on the specified location it will be overwritten
	 * 
	 * @param filePath the path to the file to create 
	 * @param chunks the chunks that constitute the file
	 * @throws Exception 
	 */
	public FileToRestore(String fileID, RecieveChunk[] chunks) throws Exception{
		
		
		this(fileID);
		
		if(chunks != null){
			Arrays.sort(chunks);
			Collections.addAll(this.chunks, chunks);
		}
		
		if(this.chunks.size() == this.nrChunks) this.resconstructFile();
		
		
		
		
		

		
		
	}
	public FileToRestore(String fileID) throws Exception{
		
		
		this.fileID = fileID;
		
		Database d = new Database();
		
		this.nrChunks = d.getNrChunks(this);
		this.filePath = d.getPathForRestoreFile(this);
		
		
		
		
		

		
		
	}
	
	public int getNrChunks(){
		return nrChunks;

	}
	
	public String getFileID(){
		
		return fileID;
	}

	public String getFilePath(){
	
		return filePath;
		
	}
	

	public void addChunk(RecieveChunk chunk){
		
		if(chunk.fileID != fileID) return;
		
		fastInsert(chunk);
		
		if(chunk.inMemory()){//try to save it
			try{
				chunk.saveToFile(pathToRecoveryFolder+fileID+"_"+chunk.nr);
			}
			catch(Exception e){}
		}
		
		
		
		if(nrChunks == chunks.size()){
			
			
			
		}
	
		
		//TODO implement it
		
		
	}


	public void fastInsert(RecieveChunk c) {
	    int pos = Collections.binarySearch(chunks, c);
	    
	    if(chunks.get(pos).nr == c.nr)return;//don't insert if it's already there
	    if (pos < 0) {
	        chunks.add(-pos-1, c);
	    }
	}

	public void resconstructFile() throws Exception{
		
		
		int lastNr = chunks.get(0).nr;
		
		if(lastNr!=0) throw new Exception("Missing first chunk");
		
		lastNr = -1; 
		
		for (RecieveChunk chunk : chunks) {
			if(!chunk.fileID.equals(this.fileID))throw new Exception("found chunk with non suitable chunkID");
			lastNr++;
			if(lastNr!=chunk.nr)throw new Exception("Missing chunk nr "+lastNr);
		}
		
		if(chunks.get(chunks.size()-1).getContent().length == 64000 )throw new Exception("Last provided chunk is not the last chunk");
		
		
		FileOutputStream fos = new FileOutputStream(filePath);
		

		for (Chunk chunk : chunks) {
			
			fos.write(chunk.getContent());
			
		}
		
		fos.close();
		
	}
}
