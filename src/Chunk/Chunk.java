package Chunk;
import java.io.IOException;
import java.sql.SQLException;

import Main.Database;


public abstract class Chunk implements Comparable<Chunk>{
	
	public int nr;
	public String fileID = null;

	protected byte[] content = null;
	

	
	Chunk(String fileID,int nr){
		
		this.fileID = fileID;
		this.nr = nr;
		
	}
	
	Chunk(String fileID,int nr, byte[] content){
		
		this(fileID,nr);
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
		
		try{
			Database d = new Database();
			return d.isOurFile(this.fileID);
			
		}
		catch(Exception e){
			
			return false;
		}
		
	}
	
	/**
	 * 
	 * Returns the path to the file that holds the chunk
	 * @return
	 */
	public abstract String getPath();
	
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

	/**
	 * Increments the replica count of this chunk
	 * 
	 */
	public void incrementReplicationCount(){
		
		try{	
			Database d = new Database();
			
			d.addReplicaCountToChunk(fileID, nr);
		}
		catch(Exception e){
			
		}

		
		
		
	}
	
	/**
	 * 
	 * Decrements the replica count for this chunk
	 * 
	 */
	public  void resetReplicationCount(){
		
		try{	
			Database d = new Database();
			
			d.resetReplicaCountToChunk(fileID, nr);
		}
		catch(Exception e){
			
		}
		
	}





	public static void cleanupChunks(String fileID){
		
		try{
			Database d = new Database();
			
			RecieveChunk[] rc = d.chunksForFile(fileID);
			
			for (RecieveChunk recieveChunk : rc) recieveChunk.cleanup();
		}
		catch(Exception e ){
			e.printStackTrace();
		}

		
		
	}
	
	
	public abstract int desiredReplicationDegree();
	
	//TODO: Method to say if desired replication rate was met
}
