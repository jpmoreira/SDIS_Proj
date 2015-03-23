package Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class S_File {
	
	
	private File file;
	
	/**
	 * 
	 * This method creates a file in a specific location given all it's chuncks
	 * 
	 * @param filePath the path to the file to create 
	 * @param chunks the chunks that constitute the file
	 */
	public S_File(String filePath, Chunk[] chunks){
		
		
		
	}
	
	/**
	 * 
	 * This method creates an S_File object representing a file on the file system. If the file doesn't exist an exception is thrown.
	 * 
	 * @param filePath the path to the file
	 * @throws Exception an exception is thrown in case the file doesn't exist
	 */
	
	public S_File(String filePath) throws Exception{
	
		
		file = new File(filePath);
		
		if(file==null || !file.exists() || !file.isFile()) throw new Exception("File doesn't exist");
		
	}
	
	
	public Chunk getChunk(int nr) throws Exception{
		
		int nrChunks = (int) (file.length()/64000.0);//get integer parts
		nrChunks++;
		
		
		if( nr >= nrChunks) return null;
		
		byte[] b = new byte[64000];
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		
		raf.seek((long) (64000.0*nr));
		int readSize=raf.read(b);
		raf.close();
		
		//necessary cause read will return -1 if 0 chars could be read
		if(readSize == -1)readSize=0;//if didn't read any char, meaning last chunk is empty
		
		if(readSize<64000){//if we don't have 64K to read shorten array before creating chunk
			return new Chunk("", nr, Arrays.copyOfRange(b, 0, readSize));	
		}
		
		return new Chunk("",nr,b);
		
	}
	
	

}
