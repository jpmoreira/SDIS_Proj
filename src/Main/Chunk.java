package Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Chunk {
	
	public int nr;
	public String fileID = null;
	private File f = null;
	private byte[] content = null;
	
	/**
	 * 
	 * Creates a chunk object and stores it as a file in a given path in the disk.
	 * 
	 * @param number the order number of the chunk (zero based)
	 * @param fileID the file identifier to which the chunk belongs to 
	 * @param contentBytes the content of the chunk
	 * @param path the path where the chunk is to be stored
	 * @throws Exception an exception is thrown if the chunk could not be created
	 */
	public Chunk(String fileID,int number, byte[] contentBytes, String path) throws Exception {
		
		
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(contentBytes);
		fos.close();
		
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		Database d = new Database();
		
		nr = number;
		this.fileID = fileID;
		d.addChunk(this, path);
		

		
		
	}
	
	
	/**
	 * 
	 * Creates a chunk object but doesn't save it to disk.
	 * 
	 * @param number the order number of the chunk (zero based)
	 * @param fileID the file identifier to which the chunk belongs to 
	 * @param contentBytes the content of the chunk
	 * @throws Exception an exception is thrown if the chunk could not be created
	 */
	public Chunk(String fileID,int number, byte[] contentBytes) {
		
		
		this.content = contentBytes;
		nr = number;
		this.fileID = fileID;
		
		
	}
	
	
	
	/**
	 * 
	 * 
	 * Creates a Chunk object that represents a chunk of a file in a given Path
	 * If the chunk doesn't exist then an exception will be raised.
	 * 
	 * @param number the number of the chunk
	 * @param fileID the fileID to which the chunck belongs
	 * @throws Exception an exception that means the file doesn't exist
	 */
	public Chunk(String fileID,int number) throws Exception {
		
		nr = number;
		this.fileID = fileID;
		
		Database d = new Database();
		
		String path = d.getPathForChunk(this);
		
		if(path == null) throw new Exception("Could not locate file");
		
		f = new File(path);
		
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		

		
		
	}

	
	/**
	 * 
	 * 
	 * Returns the content of the Chunk object
	 * @return the content of the chunk
	 * @throws IOException an exception is thrown if an attempt to read the chunk from a file was made but it didn't succeed 
	 */
	public byte[] getContent() throws IOException{

		
		if(content!=null){//if we have the content ourselfs
			
			return content;
		}
		
		
		else{
			
	    byte[] bFile = new byte[(int) f.length()];
        //convert file into array of bytes
	    FileInputStream fileInputStream = new FileInputStream(f);
	    fileInputStream.read(bFile);
	    fileInputStream.close();
	    return bFile;
			
		}


		
		
		
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
		d.addChunk(this, path);
		content = null;//immediatly free the content if possible
		
		
	}
	
}
