package Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import Main.Database;

public class RecieveChunk extends Chunk {

	protected File f = null;

	
	boolean own = false;
	
	/**
	 * 
	 * Loads a FileChunk from disk. If the FileChunk doesn't exist an exception is thrown
	 * @param fileID
	 * @param nr
	 * @param restore
	 * @throws Exception
	 */
	public RecieveChunk(String fileID, int nr,boolean own) throws Exception {
		super(fileID, nr,own);
		
		Database d = new Database();

		
		//Check if stored path exists
		String path = d.getPathForChunk(this);
		if(path == null) throw new Exception("Could not locate file");
		
		f = new File(path);

		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		
	}
	
	/**
	 * 
	 * Creates a FileChunk stored in memory. The content of the chunk is not stored in disk, nor is a registry of it's existence.
	 * @param fileID
	 * @param nr
	 * @param content
	 * @param restore
	 * @throws SQLException
	 */
	public RecieveChunk(String fileID, int nr , byte[] content,boolean own) throws SQLException{
		super(fileID,nr,content,own);

		
	}
	
	/**
	 * 
	 * Creates a FileChunk but stores it on disk. If the chunk already exists an exception is thrown
	 * @param fileID
	 * @param number
	 * @param contentBytes
	 * @param path
	 * @param restore
	 * @throws Exception
	 */
	public RecieveChunk(String fileID,int number, byte[] contentBytes, String path,boolean own) throws Exception{
		
		super(fileID,number,contentBytes,own);
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
		
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		Database d = new Database();
		
		if(d.chunkExists(this))d.setPathForChunk(this);
		else d.addChunk(this);
		
	
	}
	
	/**
	 * 
	 * Loads a FileChunk that is already stored on disk . If the referred file doesn't exist, an exception is thrown.
	 * @param fileID
	 * @param number
	 * @param path
	 * @param restore
	 * @throws Exception
	 */
	public RecieveChunk(String fileID,int number, String path,boolean own) throws Exception{
		
		super(fileID,number,own);
	
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
	}
	
	@Override
	public byte[] getContent() throws IOException {
		
		if(this.content == null){
	
			byte[] bFile = new byte[(int) f.length()];
		    //convert file into array of bytes
			FileInputStream fileInputStream = new FileInputStream(f);
			fileInputStream.read(bFile);
			fileInputStream.close();
			return bFile;

			
		}
		return super.getContent();
	}

	/**
	 * Save a chunk to the disk and registers it. If the chunk already has an attributed file to hold it, this call is silently ignored.
	 * @param path the path to the file where the chunk is to be saved.
	 * @throws Exception
	 */
	public void saveToFile(String path) throws Exception{
		
		
		if(f!=null) return;
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
		
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		Database d = new Database();
		d.addChunk(this);

		content = null;//immediatly free the content if possible
		
		
	}


	public String getPath() {
		
		if(f==null)return null;
		
		try{
			return f.getCanonicalPath();
			
			
		}catch(IOException e){
			return null;
		}
		

		
	}


	/**
	 * 
	 * Returns true if the chunk is in memory, false otherwise
	 * @return
	 */
	public boolean inMemory(){
		
		return f==null;
		
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
	 * increments the number of replicaCounts a given file has
	 */
	public void incrementReplicaCount(){
		
		try{
			
			Database d = new Database();
			
			d.addReplicaCountToChunk(this.fileID, this.nr);
			
			
		}catch(SQLException e){
			
		}
		
		
		
	}

	
	public boolean isOwn(){
		
		return false;
	}
}
