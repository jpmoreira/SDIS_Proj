package Files;

import java.io.File;

import Main.Database;



public abstract class S_File {
	
	

	


	public abstract int getNrChunks();
	public abstract String getFileID();
	public abstract String getFilePath();
	
	public int consumedSpace(){
		
		File backupDir = new File(Database.defaultBackupDir);
		
		return getFolderSize(backupDir);
		
		
		
	}
	
	private static int getFolderSize(File dir) {
	    int size = 0;
	    for (File file : dir.listFiles()) {
	        if (file.isFile()) {
	            // System.out.println(file.getName() + " " + file.length());
	            size += file.length();
	        } else
	            size += getFolderSize(file);
	    }
	    return size;
	}


}
