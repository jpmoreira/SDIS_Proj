import java.io.File;
import java.io.FileReader;


public class S_File {
	
	
	private File file;
	private FileReader reader;
	
	
	public S_File(String filePath) throws Exception {
		
		file = new File(filePath);
		
		if (!file.isFile()) throw new Exception();
		
		reader = new FileReader(file);
		
		
		
	}
	
	/**
	 * 
	 * This method creates a file in a specific location given all it's chuncks
	 * 
	 * @param filePath the path to the file to create 
	 * @param chunks the chuncks that constitute the file
	 */
	public S_File(String filePath, Chunk[] chunks){
		
		
		
	}
	
	
	public Chunk getChunk(int nr){
		
		int nrChunks = (int) Math.ceil(file.length()/64000.0);
		
		
		if( nr > nrChunks) return null;
		
		
		return 
		
		
		
		
		
		return null;
		
	}
	
	

}
