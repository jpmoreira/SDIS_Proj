package Files;

//FIXME what if we want to backup a file that has already been backed and is now changed, what is supposed to happen???? Ask Souto

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;

import Chunk.SendChunk;
import Main.Database;

public class FileToBackup extends S_File {

	public File file;
	
	public int desiredRepDegree;
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static byte[] hexToBytes(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public byte[] sha256() throws NoSuchAlgorithmException, UnsupportedEncodingException{
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		
		String text = file.getName()+file.lastModified()+file.length();

		md.update(text.getBytes("UTF-8"));
		
		return md.digest();
		
	}
	
	public String getFileID() {
		
		
		try{
			
			if(fileID == null)fileID = bytesToHex(this.sha256());
			
		}		
		catch(Exception e){
		}
		return fileID;
		
	}

	/**
	 * 
	 * This method creates an S_File object representing a file on the file system. If the file doesn't exist an exception is thrown.
	 * 
	 * @param filePath the path to the file
	 * @throws Exception an exception is thrown in case the file doesn't exist
	 */ 
	
	public FileToBackup(String filePath,int repDegree) throws Exception{
	
		
		this.desiredRepDegree = repDegree;
		file = new File(filePath);
		
		if(file==null || !file.exists() || !file.isFile()) throw new Exception("File doesn't exist");
		
		this.addToBackupRegistry();
		
	}
	
	
	public FileToBackup(String filePath) throws Exception{
		
		
		
		try{
			Database d = new Database();
			
			
			file = new File(filePath);
			if(file==null || !file.exists() || !file.isFile()) throw new Exception("File doesn't exist");
			
			this.fileID = d.fileIDForBackedFile(new File(filePath).getCanonicalPath());
			if(this.fileID == null)throw new Exception();
			
			this.desiredRepDegree = d.getDesiredReplicationDegreeForFile(fileID);
			
			
		}
		catch(Exception e){
			
			throw new Exception("Unable to load backed file cause it doesn't exist");
		}
		
		
		
	}

	public SendChunk[] getChunks() throws Exception{
		
		SendChunk[] chunks = new SendChunk[getNrChunks()];
		
		for(int i = 0 ; i < chunks.length; i++){
			
			chunks[i] = this.getChunk(i);
			
		}
		
		return chunks;
		
		
	}
	
	public SendChunk getChunk(int nr) throws Exception{
		
		
		
		
		if( nr >= this.getNrChunks()) return null;
		
		return new SendChunk(nr,this);
		
	}
	
	public byte[] contentForChunk(int nr) throws IOException{
		
		if( nr >= this.getNrChunks()) return null;
		
		byte[] b = new byte[64000];
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		
		raf.seek((long) (64000.0*nr));
		int readSize=raf.read(b);
		raf.close();
		
		//necessary cause read will return -1 if 0 chars could be read
		if(readSize == -1)readSize=0;//if didn't read any char, meaning last chunk is empty
		
		if(readSize<64000){//if we don't have 64K to read shorten array before creating chunk
			return Arrays.copyOfRange(b, 0, readSize);	
			//TODO change the id here
		}
		
		return b;
		
	}

	public int getNrChunks(){
		
		int nrChunks = (int) (file.length()/64000.0);//get integer parts
		nrChunks++;
		
		return nrChunks;
		
	}

	public String getFilePath() {
		
		if(file == null) return null;
		try{
			return file.getCanonicalPath();
		}catch(Exception e){
			return null;
		}
		
	}

	private void addToBackupRegistry() throws SQLException, IOException{
		
		
		try{
			Database d = new Database();
			
			d.addBackedUpFile(this);
			
		}catch(Exception e){
			
			System.out.println("Unable to register file to backup");
		}

		
	}


	public static String[] backedFiles(){
		
		try{
			
			return new Database().backedFilePaths();
		}
		catch(SQLException e){
			
			return new String[0];
		}
		
		
		
	} 
	
	public void remove(){		
		try{
			
			Database d = new Database();
			d.removeBackedFile(this.getFilePath());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
