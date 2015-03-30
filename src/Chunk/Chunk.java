package Chunk;
import java.io.IOException;
import java.sql.SQLException;

import Main.Database;


public abstract class Chunk implements Comparable<Chunk>{
	
	public int nr;
	public String fileID = null;

	protected byte[] content = null;
	
	boolean own = false;
	
	
	Chunk(String fileID,int nr,boolean own){
		
		this.fileID = fileID;
		this.nr = nr;
		
		this.own = own;
		
		
	}
	
	Chunk(String fileID,int nr, byte[] content,boolean own){
		
		this(fileID,nr,own);
		this.content = content;
		
	}
		
	/**
	 * 
	 * 
	 * Returns the content of the Chunk object
	 * @return the content of the chunk
	 * @throws IOException an exception is thrown if an attempt to read the chunk from a file was made but it didn't succeed 
	 */
	public byte[] getContent() throws IOException{

		
		return content;	
	}


	public boolean isOwn(){
		
		return own;
		
	}
	
	public String getPath(){
		return null;
	}
	
	public final int compareTo(Chunk c){
		
		if(this.nr > c.nr)return 1;
		if(this.nr< c.nr)return -1;
		return 0;
		
		
		
	}

	/**
	 * 
	 * @return the number of replicas detected so far. Zero is returned if either zero replicas were detected or this information is not available.

	 */
	public int getReplicaCount(){
			
		try{
			Database d = new Database();
			
			return d.replicaCountOfChunk(this.fileID, this.nr);
		}
		catch(SQLException e){
			
			return 0;
		}

		
		
	}

	
	public void incrementReplicationCount(){
		
		try{	
			Database d = new Database();
			
			d.addReplicaCountToChunk(fileID, nr);
		}
		catch(Exception e){
			
		}

		
		
		
	}
	
	public  void resetReplicationCount(){
		
		try{	
			Database d = new Database();
			
			d.resetReplicaCountToChunk(fileID, nr);
		}
		catch(Exception e){
			
		}
		
	}
}
