package Chunk;
import java.io.IOException;


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
}
