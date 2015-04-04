package Files;
//TODO: how to remove file

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Main.Database;

public class FileToRestore implements S_File{

	

	public ArrayList<RecieveChunk> chunks = new ArrayList<RecieveChunk>();
	
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
	/*
	public FileToRestore(String fileID, RecieveChunk[] chunks) throws Exception{
		
		
		this(fileID);
		
		if(chunks != null){
			Arrays.sort(chunks);
			Collections.addAll(this.chunks, chunks);
		}
		
		if(this.chunks.size() == this.nrChunks) this.reconstructFile();
		
		
		
		
		

		
		
	}
	*/
	public FileToRestore(String fileID) throws Exception{
		
		
		this.fileID = fileID;
		
		Database d = new Database();
		
		this.nrChunks = d.getNrChunks(this);
		this.filePath = d.getPathForRestoreFile(this);
		
		
		if(this.filePath == null) throw new Exception("Attempt to restore non backedup file"); 
		
		RecieveChunk[] r = d.chunksForFile(fileID);
		
		for (RecieveChunk recieveChunk : r) {
			
			this.addChunk(recieveChunk);
		}
		
		
		

		
		
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

	
	private void addChunk(RecieveChunk chunk){
		
		if(!chunk.fileID.equals(fileID)) return;
		
		fastInsert(chunk);
		
		if(nrChunks == chunks.size())return;
		
	
		
		
	}


	
	public Integer[] missingChunkNrs(){
		
		try{
			Database d = new Database();
		
			RecieveChunk[] r = d.chunksForFile(fileID);
		
			for (RecieveChunk recieveChunk : r) this.addChunk(recieveChunk);
			
		}
		catch(Exception e){}
		
		
		ArrayList<Integer> missingOnes = new ArrayList<Integer>();
		
		for(int i = 0 ; i < this.chunks.size() -1 ; i++){
			
			int a = this.chunks.get(i).nr;
			int b = this.chunks.get(i+1).nr;
			if (b == a + 1) continue;
			else{
				for(int x = a + 1 ; x < b ; x++) missingOnes.add(x);	//add the ones between a and b
			}

		}
		
		
		int lastNr;
		if(this.chunks.size()==0) lastNr = -1;
		else lastNr = this.chunks.get(this.chunks.size()-1).nr;
		
		if (lastNr + 1 != this.nrChunks){//add missing ones at the end
			for(int i = lastNr + 1 ; i < this.nrChunks ; i++) missingOnes.add(i);
		}
		
		
		Integer [] array = new Integer[missingOnes.size()];
		missingOnes.toArray(array);
		return array;
		
	}
	
	public void fastInsert(RecieveChunk c) {
	    int pos = Collections.binarySearch(chunks, c);
	    
	    if (pos < 0) {
	        chunks.add(-pos-1, c);
	        return;
	    }
	    if(chunks.get(pos).nr == c.nr)return;//don't insert if it's already there

	}

	public void reconstructFile() throws Exception{
		
		
		Database d = new Database();
		
		RecieveChunk[] r = d.chunksForFile(fileID);
		
		for (RecieveChunk recieveChunk : r) {
			
			this.addChunk(recieveChunk);
		}
		
		
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

	public boolean isRestored(){
		
		
		if(filePath == null)return false;
		
		File f = new File(filePath);
		
		if(f.exists() && f.isFile()) return true;
		
		return false;
		

		
		
	}
	
	public void cleanup(){
		


		for (RecieveChunk recieveChunk : chunks) {
			
			String path = recieveChunk.getPath();
			
			File f = new File(path);
			
			f.delete();
			
			
		}
		
		try{
			Database d = new Database();
			d.removePathsForChunksOfFile(this.fileID);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public static String fileIDForBackedFile(String path){
		
		File f = new File(path);
		
		
		try{
			Database d = new Database();
			
			return d.fileIDForBackedFile(f.getCanonicalPath());	
			
		}
		catch(Exception e){
			
			return null;
			
		}

		
	}
}
