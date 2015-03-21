import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Chunk {
	
	private int nr;
	private String fileID;
	private File f;
	
	/**
	 * 
	 * Stores a chunk in a given path in the disk.
	 * 
	 * @param number the order number of the chunk
	 * @param fileID the file identifier to wich the chunck belongs to 
	 * @param contentBytes the content of the chunk
	 * @param path the path where the chunk is to be stored
	 * @throws Exception an exception is thrown if the chunk could not be created
	 */
	public Chunk(int number,String fileID, byte[] contentBytes, String path) throws Exception {
		
		
		
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
	 * 
	 * Creates a Chunk object that represents a chunk of a file in a given Path
	 * If the chunk doesn't exist then an exception will be raised.
	 * 
	 * @param number the number of the chunk
	 * @param fileID the fileID to wich the chunck belongs
	 * @param path the path were the chunck is stored
	 * @throws Exception an exception that means the file doesn't exist
	 */
	public Chunk(int number,String fileID, String path) throws Exception {
		
		f = new File(path);
		
		
		if (!f.isFile() || !f.exists()){
			
			throw new Exception();
			
		}
		
		nr = number;
		this.fileID = fileID;
		
		
	}


	
	byte[] getContent() throws IOException{

        byte[] bFile = new byte[(int) f.length()];
            //convert file into array of bytes
	    FileInputStream fileInputStream = new FileInputStream(f);
	    fileInputStream.read(bFile);
	    fileInputStream.close();
	    return bFile;

		
		
		
	}
	
	
	

}
