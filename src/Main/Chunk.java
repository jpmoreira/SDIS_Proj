package Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Chunk {
	
	private int nr;
	private String fileID = null;
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
		
		nr = number;
		this.fileID = fileID;
		
		
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
	public Chunk(String fileID,int number, byte[] contentBytes) throws Exception {
		
		
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
	 * @param fileID the fileID to wich the chunck belongs
	 * @param path the path were the chunck is stored
	 * @throws Exception an exception that means the file doesn't exist
	 */
	public Chunk(String fileID,int number, String path) throws Exception {
		
		f = new File(path);
		
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		nr = number;
		this.fileID = fileID;
		
		
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
	
	
	
	public void saveToFile(String path) throws Exception{
		
		
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content);
		fos.close();
		
		f = new File(path);
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		content = null;//immediatly free the content if possible
		
		
	}
	

}
