package Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import Main.Database;

public class RecieveChunk extends Chunk {

	protected File f = null;
	
	protected int desiredRepDegree = -1;

	
	/**
	 * 
	 * Loads a FileChunk from disk. If the FileChunk doesn't exist an exception is thrown
	 * @param fileID
	 * @param nr
	 * @param restore
	 * @throws Exception
	 */
	public RecieveChunk(String fileID, int nr) throws Exception {
		super(fileID, nr);
		
		Database d = new Database();

		
		//Check if stored path exists
		String path = d.getPathForChunk(this);
		if(path == null) throw new Exception("Could not locate file");
		
		f = new File(path);

		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		
	}
	
	//TODO: get chunk with most degree
	
	//TODO: verify when creating receive chunk that space is available
	
	/**
	 * 
	 * Creates a FileChunk stored in memory.The chunk must be from a file we backed before. The content of the chunk is stored with the default name on the default folder
	 * @param fileID
	 * @param nr
	 * @param content
	 * @param restore
	 * @throws Exception 
	 */
	public RecieveChunk(String fileID, int nr , byte[] content) throws Exception{
		
		this(fileID,nr,content,Database.defaultBackupDir+fileID+"_"+nr+".chunk");

		
	}
	
	/**
	 * 
	 * Creates a FileChunk stored in memory. The chunk must not be from a file of ours. The content of the chunk is stored with the default name on the default folder
	 * @param fileID
	 * @param nr
	 * @param content
	 * @param restore
	 * @throws Exception 
	 */
	public RecieveChunk(String fileID, int nr , byte[] content,int desiredReplicationDegree) throws Exception{
		
		this(fileID,nr,content,Database.defaultBackupDir+fileID+"_"+nr+".chunk",desiredReplicationDegree);

		
	}
	
	/**
	 * 
	 * Creates a FileChunk but stores it on disk.The chunk must belong to us. If the chunk already exists an exception is thrown
	 * @param fileID
	 * @param number
	 * @param contentBytes
	 * @param path
	 * @param restore
	 * @throws Exception
	 */
	public RecieveChunk(String fileID,int number, byte[] contentBytes, String path) throws Exception{
		
		super(fileID,number,contentBytes);
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
		
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		Database d = new Database();
		
		if(this.isOwn()){
			this.desiredRepDegree = d.getDesiredReplicationDegreeForFile(this.fileID);
			d.setPathForChunk(this);
			
		}
		else throw new Exception("Missing replication degree");
		
	
	}
	

	/**
	 * 
	 * Creates a FileChunk but stores it on disk.The chunk must not belong to us. If the chunk already exists an exception is thrown
	 * @param fileID
	 * @param number
	 * @param contentBytes
	 * @param path
	 * @param restore
	 * @throws Exception
	 */
	public RecieveChunk(String fileID,int number, byte[] contentBytes, String path,int desiredReplicationDegree) throws Exception{
		
		super(fileID,number,contentBytes);
		this.desiredRepDegree = desiredReplicationDegree;
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
		
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		

		
		Database d = new Database();
		
		if(this.isOwn())throw new Exception("Tried to force replication degree on file thats ours");
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
	public RecieveChunk(String fileID,int number, String path) throws Exception{
		
		super(fileID,number);
	
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
	/*
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
		d.setPathForChunk(this);

		content = null;//immediatly free the content if possible
		
		
	}
	
	*/

	public String getPath() {
		
		if(f==null || !f.exists())return null;
		
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

	public void cleanup() throws Exception{
		
		
		if(this.getPath() != null) {
			File f = new File(this.getPath());
			
			f.delete();
			
		}
		try{
			
			new Database().removeChunk(this);
			
		}catch(Exception e){
			throw new Exception("unable to clear itself");
		}
		
		
		
	}
	


	@Override
	public int desiredReplicationDegree() {
		
		return this.desiredRepDegree;
	}

	
	

}
