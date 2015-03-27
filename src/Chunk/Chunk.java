package Chunk;
import java.io.IOException;


public class Chunk implements Comparable<Chunk>{
	
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


	
	public final int compareTo(Chunk c){
		
		if(this.nr > c.nr)return 1;
		if(this.nr< c.nr)return -1;
		return 0;
		
		
		
	}
}
