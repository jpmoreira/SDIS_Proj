package Main;

import java.sql.*;
import java.util.ArrayList;

import Chunk.Chunk;
import Chunk.FileChunk;

public class Database {
	
	/**
	 * 
	 * The database connection
	 * 
	 */
	Connection con;
	
	public static String databaseToUse = null;
	private static String defaultDeploymentDB = "supportingFiles/supportingDB.db";
	
	/**
	 * 
	 * Default database constructor. Initiates the Database with the default database file
	 * @throws SQLException
	 */
	public Database() throws SQLException {
		
		
		String dbFile = databaseToUse;
		
		if(dbFile == null) dbFile = defaultDeploymentDB;
		
		con = DriverManager.getConnection("jdbc:sqlite:"+dbFile);
		
		
		
	}

	
	/**
	 * 
	 * Allows the creation of a Database object along with the possibility of deleting all the db data. Interesting for testing purposes.
	 * @param databaseFile the file where the sqlite db resides
	 * @param clear a flag to say whether all data should be removed from the database.
	 * @throws SQLException an exception is thrown if a problem occurs
	 */
	public Database(boolean clear) throws SQLException{
		this();
		if(clear) clearData();
		
	}
	
	/**
	 * 
	 * Adds a path to a chunk in a database. This should be used only for testing
	 * @param chunk the chunk to be placed in the db
	 * @param path the path to the chunk file in the file system
	 * @throws Exception An exception is thrown if anything goes wrong
	 */
	public void addPathToChunk(FileChunk chunk,String path) throws SQLException{
		
		Statement stmt = con.createStatement();
		String sql = null;
		if(path==null){
			
			sql = "UPDATE Chunk set path = NULL WHERE fileID='"+chunk.fileID+"' AND nr = "+chunk.nr+";";
			
		}
		else{
		
			sql = "UPDATE Chunk set path = '"+path+"' WHERE fileID='"+chunk.fileID+"' AND nr = "+chunk.nr+";";
			
			
		}
		
		
		
		
		
		//String sql = "INSERT INTO Chunk (path,fileID,nr) VALUES('"+path+"','"+chunk.fileID+"',"+chunk.nr+");";
	    stmt.executeUpdate(sql);
	    stmt.close();
		
		
	}
	

	
	
	/**
	 * 
	 * Puts a chunk into the database
	 * @param chunk the chunk to be placed in the db
	 * @param a flag to state whether this is a restore chunk or not 
	 * @throws Exception An exception is thrown if anything goes wrong
	 */
	public void addChunk(FileChunk chunk,boolean isRestore) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		String path = chunk.getPath();
		String sql = null;
		
		
		
		if(path == null){
			
			sql = "INSERT INTO Chunk (fileID,nr,isRestore) VALUES('"+chunk.fileID+"',"+chunk.nr+",'"+Boolean.toString(chunk.restore).toUpperCase()+"');";
			
		}
		else{
			sql = "INSERT INTO Chunk (path,fileID,nr,isRestore) VALUES('"+path+"','"+chunk.fileID+"',"+chunk.nr+",'"+Boolean.toString(chunk.restore).toUpperCase()+"');";
		}
		
		
		stmt.execute(sql);
	    stmt.close();
		
		
	}
	
	
	
	
	/**
	 * 
	 * Gets the path where a given chunk is located
	 * @param chunk the chunk to be located
	 * @return the path where the chunk is supposed to be located
	 * @throws SQLException
	 */
	public String getPathForChunk(Chunk chunk) throws SQLException{
		
		
		Statement stmt = con.createStatement();
		
		String path = null;
		
		String sql = "SELECT path from Chunk WHERE fileID='"+chunk.fileID+"' AND nr = "+chunk.nr+";";
		
		
		ResultSet resultSet = stmt.executeQuery(sql);
		while(resultSet.next()){
			
			path = resultSet.getString("path");
			
			break;
			
		}
		
		stmt.close();
		return path;
		
	
		
	}
	
	/**
	 * 
	 * Gets the restore flag for a given chunk
	 * @param chunk the chunk whose flag is to be returned
	 * @return the flag value for the chunk
	 * @throws SQLException an exception is thrown if the chunk doesn't exist
	 */
	public boolean getRestoreFlagForChunk(Chunk chunk) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		String sql = "SELECT isRestore from Chunk WHERE fileID='"+chunk.fileID+"' AND nr = "+chunk.nr+";";
		
		ResultSet resultSet = stmt.executeQuery(sql);
		
		boolean value = false;
		
		while(resultSet.next()){
			
			if(resultSet.getString(1).toUpperCase().equals("TRUE")) value = true;
			break;
			
		}
		stmt.close();
		return value;
		
	}
	/**
	 * 
	 * A method that removes a chunk from the db.
	 * 
	 * @param chunk the chunk to be removed
	 * @throws SQLException an exception is thrown if an error occurs
	 */
	public void removeChunk(Chunk chunk) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		String sql = "DELETE FROM Chunk where fileID = '"+chunk.fileID+"' AND nr = "+chunk.nr+";";
		
		stmt.execute(sql);
		
		stmt.close();
		
	}

	
	
	/**
	 * 
	 * 
	 * A method that returns the number of Chunks stored.
	 * @return The number of the chunks stored.
	 * @throws SQLException an exception is thrown if a problem connecting to the db occurs
	 */
	public int nrChunksStored() throws SQLException{
		
		int nrChunks = -1;
		Statement stmt = con.createStatement();
		String sql = "SELECT Count(*) FROM Chunk";
		
		ResultSet r = stmt.executeQuery(sql);
		
		while(r.next()){
			
			nrChunks = r.getInt(1);//0 is rowID I think
			
			
		}
		
		stmt.close();
		
		return nrChunks;
		
		
		
	}
		
	/**
	 * 
	 * Deletes all the records in the database. Useful for testing purposes.
	 * @throws SQLException and exception is thrown if it wasn't possible to delete the database content.
	 * 
	 */
	public void clearData() throws SQLException{
		
		
		Statement stmt = con.createStatement();
		
		String sql = "DELETE from Chunk WHERE 1=1;";
		stmt.execute(sql);
		stmt.close();
		
	}

	/**
	 * 
	 * Returns all the chunks of a given file in an orderly fashion
	 * @param fileID the id of the file whose chucks are to be retrieved
	 * @param restoreChunks a boolean saying whether the restore or backup chunks are to be returned
	 * @return an array with the found chunks, ordered in ascending order of chunk number
	 * @throws Exception an exception thrown if anything goes wrong
	 */
	public FileChunk[] chunksForFile(String fileID,boolean restoreChunks) throws Exception{
	
		Statement stmt = con.createStatement();
		
		String sql = "SELECT nr,path from Chunk where isRestore = '"+Boolean.toString(restoreChunks).toUpperCase()+"' AND fileID='"+fileID+"' ORDER BY nr ASC;";
		
		ResultSet set = stmt.executeQuery(sql);
		
		//TODO: maybe discover the size before and then allocate the whole array before. More efficient
		ArrayList<FileChunk> chunkList = new ArrayList<FileChunk>();
		
		while(set.next()){
			
			chunkList.add(new FileChunk(fileID,set.getInt("nr"),set.getString("path"),restoreChunks));
		}
		
		FileChunk[] array = new FileChunk[chunkList.size()];
		chunkList.toArray(array);
		
		return array;
		
		
		
		
	}

	
	public void addReplicaCountToChunk(String fileID,int nr) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		String sql = "UPDATE Chunk SET replicas = replicas+1 WHERE fileID = '"+fileID+"' AND nr = "+nr+";";
		
		stmt.execute(sql);
		stmt.close();
		
		
	}
	
	public int replicaCountOfChunk(String fileID,int nr) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		String sql = "SELECT replicas from Chunk WHERE fileID ='"+fileID+"' AND nr = "+nr+";";
		
		ResultSet r = stmt.executeQuery(sql);
		int count = -1;
		while(r.next()){
			
			count = r.getInt(1);//0 is rowid
			
		}
		stmt.close();
		return count;
		
		
	}

}

