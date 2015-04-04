package Main;


//TODO: space management
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import Chunk.Chunk;
import Chunk.RecieveChunk;
import Files.FileToBackup;
import Files.FileToRestore;

public class Database {
	
	/**
	 * 
	 * The database connection
	 * 
	 */
	Connection con;
	
	public static String databaseToUse = null;
	private static String defaultDeploymentDB = "supportingFiles/supportingDB.db";
	
	public static String defaultBackupDir = "backups/";
	
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
	 * Puts a chunk into the database
	 * @param chunk the chunk to be placed in the db
	 * @param a flag to state whether this is a restore chunk or not 
	 * @throws Exception An exception is thrown if anything goes wrong
	 */
	public void addChunk(Chunk chunk) throws SQLException{
		
		
		
		Statement stmt = null;
		
		try{
			stmt = con.createStatement();
			
			String path = chunk.getPath();
			String sql = null;
			
			if(path == null){
				
				sql = "INSERT INTO Chunk (fileID,nr,isOwn,desiredReplicas) VALUES('"+chunk.fileID+"',"+chunk.nr+",'"+Boolean.toString(chunk.isOwn()).toUpperCase()+"',"+chunk.desiredReplicationDegree()+");";
				
			}
			else{
				System.out.println(chunk.isOwn());
				sql = "INSERT INTO Chunk (path,fileID,nr,isOwn,desiredReplicas) VALUES('"+path+"','"+chunk.fileID+"',"+chunk.nr+",'"+Boolean.toString(chunk.isOwn()).toUpperCase()+"',"+chunk.desiredReplicationDegree()+");";
				
			}
			
			stmt.execute(sql); 
		}
		catch(Exception e){}
		finally{
			
			closeSTMT(stmt);
		}

		
		
	}

	public void setPathForChunk(RecieveChunk chunk) throws Exception{
		
		
		if(!chunk.isOwn())throw new Exception("Impossible to set path of a chunk backed up by us");
		
		Statement stmt = null;
		try{
			stmt = con.createStatement();
			
			String sql = "UPDATE Chunk SET path = '"+ chunk.getPath()+"' WHERE fileID = '"+chunk.fileID+"' AND nr = "+chunk.nr+";";
			
			stmt.execute(sql);
			

			
		}
		catch(Exception e){}
		finally{
			closeSTMT(stmt);
		}
		
		

		
	}
	
	public boolean chunkExists(Chunk c) {
		
		Statement stmt = null;
		boolean result =false;
		try {
			
			stmt = con.createStatement();
			
			String sql = "SELECT * FROM Chunk WHERE fileID = '"+c.fileID+"' AND nr = "+c.nr+";";
			
			ResultSet set = stmt.executeQuery(sql);
			
			
			
			if(set.next()) result = true;
			
			
			
			
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}
		return result;
		
	}
	
	/**
	 * 
	 * Gets the path where a given chunk is located
	 * @param chunk the chunk to be located
	 * @return the path where the chunk is supposed to be located
	 * @throws SQLException
	 */
	public String getPathForChunk(Chunk chunk) throws SQLException{
		
		Statement stmt = null;
		String path = null;
		try{
			
			stmt = con.createStatement();
			
			
			
			String sql = "SELECT path from Chunk WHERE fileID='"+chunk.fileID+"' AND nr = "+chunk.nr+";";
			
			
			ResultSet resultSet = stmt.executeQuery(sql);
			while(resultSet.next()){
				
				path = resultSet.getString("path");
				
				break;
				
			}
			
			
		}catch(Exception e){}
		finally{
			closeSTMT(stmt);
		}
		

		return path;
		
	
		
	}
	
	/**
	 * 
	 * Gets the restore flag for a given chunk
	 * @param chunk the chunk whose flag is to be returned
	 * @return the flag value for the chunk
	 * @throws SQLException an exception is thrown if the chunk doesn't exist
	 */
	public boolean getRestoreFlagForChunk(Chunk chunk) {
		
		Statement stmt = null;
		
		boolean value = false;
		
		try {
			stmt = con.createStatement();
			
			String sql = "SELECT isOwn from Chunk WHERE fileID='"+chunk.fileID+"' AND nr = "+chunk.nr+";";
			
			ResultSet resultSet = stmt.executeQuery(sql);
			

			
			while(resultSet.next()){
				
				if(resultSet.getString(1).toUpperCase().equals("TRUE")) value = true;
				break;
				
			}
		} catch (SQLException e) {
			
		}
		finally{
			
			closeSTMT(stmt);
		}
		return value;
		
	}
	/**
	 * 
	 * A method that removes a chunk from the db.
	 * 
	 * @param chunk the chunk to be removed
	 * @throws SQLException an exception is thrown if an error occurs
	 */
	public void removeChunk(Chunk chunk) {
		
		Statement stmt = null;
				
		try {
			stmt = con.createStatement();

			String sql = "DELETE FROM Chunk where fileID = '"+chunk.fileID+"' AND nr = "+chunk.nr+";";

			stmt.execute(sql);
		} catch (SQLException e) {}
		finally{
			closeSTMT(stmt);
		}
	
		
	}

	/**
	 * 
	 * 
	 * A method that returns the number of Chunks stored.
	 * @return The number of the chunks stored.
	 * @throws SQLException an exception is thrown if a problem connecting to the db occurs
	 */
	public int nrChunksStored(){
		
		int nrChunks = -1;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String sql = "SELECT Count(*) FROM Chunk";
			
			ResultSet r = stmt.executeQuery(sql);
			
			while(r.next()){
				
				nrChunks = r.getInt(1);//0 is rowID I think
				
				
			}
		} catch (SQLException e) {}
		finally{
			closeSTMT(stmt);
		}

		return nrChunks;
		
		
		
	}
		
	/**
	 * 
	 * Deletes all the records in the database. Useful for testing purposes.
	 * @throws SQLException and exception is thrown if it wasn't possible to delete the database content.
	 * 
	 */
	public void clearData() {
		
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			
			String sql = "DELETE from Chunk WHERE 1=1;";
			stmt.execute(sql);
			stmt.execute("DELETE from BackedFiles WHERE 1=1;");
		} catch (SQLException e) {}
		finally{
			closeSTMT(stmt);
		}
		
		
		
	}

	/**
	 * 
	 * Returns all the chunks of a given file in an orderly fashion
	 * @param fileID the id of the file whose chucks are to be retrieved
	 * @param restoreChunks a boolean saying whether the restore or backup chunks are to be returned
	 * @return an array with the found chunks, ordered in ascending order of chunk number
	 * @throws Exception an exception thrown if anything goes wrong
	 */
	public RecieveChunk[] chunksForFile(String fileID) {
	
		Statement stmt = null;
		RecieveChunk[] array = null;
		try {
			stmt = con.createStatement();
			
			String sql = "SELECT nr,path from Chunk where fileID='"+fileID+"' ORDER BY nr ASC;";
			
			ResultSet set = stmt.executeQuery(sql);
			
			
			ArrayList<RecieveChunk> chunkList = new ArrayList<RecieveChunk>();
			
			try{
			
				while(set.next()){
					
					try{
						chunkList.add(new RecieveChunk(fileID,set.getInt("nr"),set.getString("path")));
						
					}catch(Exception e){}
					
				}
				
			}
			catch(Exception e){}
			
			
			array = new RecieveChunk[chunkList.size()];
			chunkList.toArray(array);
		} catch (Exception e) {}
		finally{
			
			closeSTMT(stmt);
		}
		
		
		return array;
		
		
		
		
	}

	public void addReplicaCountToChunk(String fileID,int nr) {
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			
			String sql = "UPDATE Chunk SET replicas = replicas+1 WHERE fileID = '"+fileID+"' AND nr = "+nr+";";
			
			stmt.execute(sql);
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}
		
		
		
	}
	
	public void resetReplicaCountToChunk(String fileID,int nr) {
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			
			String sql = "UPDATE Chunk SET replicas = 0 WHERE fileID = '"+fileID+"' AND nr = "+nr+";";
			
			stmt.execute(sql);
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}
		
		
	}
	
	public int replicaCountOfChunk(String fileID,int nr) {
		
		Statement stmt = null;
		int count = -1;
		try {
			stmt = con.createStatement();
			
			String sql = "SELECT replicas from Chunk WHERE fileID ='"+fileID+"' AND nr = "+nr+";";
			
			ResultSet r = stmt.executeQuery(sql);
			
			while(r.next()){
				
				count = r.getInt(1);//0 is rowid
				
			}
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}
		return count;
		
		
	}
	
	public void addBackedUpFile(FileToBackup f) {
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			
			String sql = "INSERT INTO BackedFiles (path,nrChunks,fileID,rep) VALUES('"+f.file.getCanonicalPath()+"',"+f.getNrChunks()+",'"+f.getFileID()+"',"+f.desiredRepDegree+");";
			
			stmt.execute(sql);
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}

		
	}
	
	public int getNrChunks(FileToRestore f) {
		
		
		Statement stmt = null;
		int nr = -1;
		try {
			stmt = con.createStatement();
			
			String sql = "SELECT nrChunks from BackedFiles WHERE fileID = '"+f.fileID+"' ;";
			
			
			ResultSet resultSet = stmt.executeQuery(sql);
			
			
			if(resultSet.next()){
				
				nr = resultSet.getInt("nrChunks");
				
			}
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}
		
		return nr;
		
	}

	public String getPathForRestoreFile(FileToRestore f) throws SQLException{
			
		Statement stmt = null;
		String result = null;
		try {
			stmt = con.createStatement();
			
			String sql = "SELECT path from BackedFiles WHERE fileID = '"+f.fileID+"';";
			
			ResultSet set = stmt.executeQuery(sql);

			if(set.next()){
				
				result = set.getString("path");
			}
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}

		return result;
		
		
	}
	
	public void removePathsForChunksOfFile(String fileID){
		
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			
			stmt.execute("UPDATE Chunk SET path = null WHERE fileID = '"+fileID+"';");
		} catch (Exception e) {}
		finally{
			closeSTMT(stmt);
		}

	}

	public boolean isOurFile(String fileID) throws SQLException{
		
		
		boolean returnValue = false;
		Statement stmt = null;
		try{
			
			stmt = con.createStatement();
			ResultSet set = stmt.executeQuery("SELECT * from BackedFiles WHERE fileID = '"+fileID+"';");
			
			if (set.next())returnValue = true;
			
			stmt.close();
			return returnValue;
		}
		catch(Exception e){
			
			if(stmt != null)stmt.close();
			return returnValue;
		}
		
		
		
	}

	public String[] backedFilePaths() throws SQLException{
		
		ArrayList<String> paths = new ArrayList<>();
		
		Statement stmt = con.createStatement();
		ResultSet set = stmt.executeQuery("SELECT path from BackedFiles;");
		
		while(set.next()){
			
			paths.add(set.getString("path"));
		}
		
		
		stmt.close();
		String[] pathsArray = new String[paths.size()];
		paths.toArray(pathsArray);
		return pathsArray;
		
		
		
		
	}
	
	public void removeBackedFile(String path) throws SQLException {
		
		
		Statement stmt = con.createStatement();
		
		stmt.execute("DELETE FROM BackedFiles WHERE path = '"+path+"';");
		
		stmt.close();
		
		
		
	}


	public String fileIDForBackedFile(String path) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		ResultSet set = stmt.executeQuery("SELECT fileID from BackedFiles WHERE path = '"+path+"';");
		
		if(set.next())return set.getString("fileID");
		
		return null;
		
	}


	public int getDesiredReplicationDegreeForChunk(Chunk rc) throws SQLException{
		
		Statement stmt = con.createStatement();
		
		ResultSet set = stmt.executeQuery("SELECT desiredReplicas from Chunk WHERE fileID = '"+rc.fileID+"';");
		
		int toRet = 0;
		if(set.next())toRet = set.getInt("desiredReplicas");
		
		stmt.close();
		return toRet;
		
	}


	public int getDesiredReplicationDegreeForFile(String fileID) throws SQLException{
		
		
		Statement stmt = con.createStatement();
		
		ResultSet set = stmt.executeQuery("SELECT rep from BackedFiles WHERE fileID = '"+fileID+"';");
		
		int toRet = 0;
		
		if(set.next())toRet = set.getInt("rep");
		
		stmt.close();
		return toRet;
		
		
	}


	public RecieveChunk getChunkInBetterConditionToBeDeleted() throws SQLException{
	
		
		Statement stmt = con.createStatement();
		
		
		stmt.executeQuery("SELECT * from Chunk WHERE (SELECT Count(*) FROM BackedFiles WHERE BackedFiles.fileID = Chunk.fileID) = 0");
		
		
		return null;
	}


	private void closeSTMT(Statement stmt){
		
		
		if(stmt == null)return;
		try{
			
			stmt.close();
			
		}
		catch(Exception e){
			
		}
		
	}
}

