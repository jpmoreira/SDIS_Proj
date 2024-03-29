package Files;

import java.io.File;

import Main.Database;



public abstract class S_File {
	
	

	public static long availableSpace = 256000;

	public String fileID = null;

	public abstract int getNrChunks();
	
	public abstract String getFileID();

	public abstract String getFilePath();
	
	public static long consumedSpace(){
		
		File backupDir = new File(Database.defaultBackupDir);
		
		return getFolderSize(backupDir);
		
		
		
	}
	
	private static long getFolderSize(File dir) {
	    long size = 0;
	    for (File file : dir.listFiles()) {
	        if (file.isFile()) {
	            // System.out.println(file.getName() + " " + file.length());
	            size += file.length();
	        } else
	            size += getFolderSize(file);
	    }
	    return size;
	}

	public static void cleanFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isFile()) f.delete();
	        }
	    }
	}

	public static long spaceLeft(){
		
		return availableSpace - consumedSpace();
		
	} 
}
