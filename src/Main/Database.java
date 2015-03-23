package Main;

import java.sql.*;

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
	 * Puts a chunk into the database
	 * @param chunk the chunk to be placed in the db
	 * @param path the path to the chunk file in the file system
	 * @throws SQLException and exception is thrown if an error occurs while inserting the file in the db.
	 */
	public void addChunk(Chunk chunk,String path) throws SQLException{
		
		
		Statement stmt = con.createStatement();
		
		String sql = "INSERT INTO Chunk (path,fileID,nr) VALUES('"+path+"','"+chunk.fileID+"',"+chunk.nr+");";
	    stmt.executeUpdate(sql);
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

}

