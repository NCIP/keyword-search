/**
 * 
 */
package titli.model;



import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.Date;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.document.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.store.*;

import org.apache.nutch.protocol.*;



/**
 * The class that handles the connections to the database etc.
 * It has the index, search and fetch methods 
 * @author Juber Patel
 *
 */
public class RDBMSReader 
{
	private String url;
	private String database;
	private String username; 
	private String password;
	private Connection indexConnection;
	private Connection searchConnection;
	private DatabaseMetaData dbMetaData;
	private PreparedStatement indexstmt;
	private Statement searchstmt;
	private ResultSet rs1;
	private String indexLocation;
	private File databaseIndexDir;
	private int MAX_STATEMENTS; 
	
	private ArrayList<String> noIndexPrefix;
	private ArrayList<String> noIndexTable;
	
	/**
	 * 
	 * @param propertiesUrl path to the properties file containing information for connection to database
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public RDBMSReader(Properties props)  throws  SQLException
	{
		
		url = props.getProperty("jdbc.url");
		database = props.getProperty("jdbc.database");
		username = props.getProperty("jdbc.username");
		password = props.getProperty("jdbc.password");
		
		System.out.println("Database Properties file read successfully...");
		
		//both of the calls work well
		indexConnection = DriverManager.getConnection(url+"?user="+username+"&password="+password);
		searchConnection = DriverManager.getConnection(url+"?user="+username+"&password="+password);
		//conn = DriverManager.getConnection(url, username, password);
		System.out.println("Connection to the database successful...");
		
		dbMetaData = indexConnection.getMetaData();
		MAX_STATEMENTS = dbMetaData.getMaxStatements();
		
		//number not known 
		if(MAX_STATEMENTS==0)
		{
			MAX_STATEMENTS=10;
			System.out.println("Maximum Concurrent statements : Number not known, setting to 10");
		}
		else
		{
			System.out.println("Maximum Concurrent statements : "+MAX_STATEMENTS );
		}
		
		
		
	}
	
	/*
	private void closeConnection() throws SQLException
	{
		conn.close();
		
	}*/

	/*some test code
	public boolean test(String country) throws SQLException
	{
		stmt.setString(1,country);
		
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next())
		{
			System.out.println(rs.getString("Name")+"     "+rs.getLong("Population"));
		}
			
		
		return true;
	}*/
	
	
	
	
	/**
	 * fetch the actual records from the database
	 * @param matchList the list of matches for which to fetch the records
	 * @throws SQLException
	 */
	public void fetch(MatchList matchList) throws SQLException
	{
		searchstmt = searchConnection.createStatement();
		
		long start = new Date().getTime();
		
		for(Match match : matchList)
		{
			ResultSet rs = searchstmt.executeQuery(match.getQuerystring());
			ResultSetMetaData rsmd = rs.getMetaData();
			
			rs.next();
			
			int columns = rsmd.getColumnCount();
			
			System.out.println("Table : " +match.getTable());
			
			
			for(int i=1; i<=columns; i++)
			{
				System.out.print(rsmd.getColumnName(i)+" : "+rs.getString(i)+"  ");
			}
			
			System.out.println();
			rs.close();
		}
		
		long end = new Date().getTime();
		
		System.out.print("\nFetch took "+(end-start)/1000.0+" seconds");
		searchstmt.close();
	}
	
	
	
	
	
	

	
	
	/**
	 * close the database connections
	 */
	protected void finalize() throws SQLException
	{
		indexConnection.close();
		searchConnection.close();
		
	}
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		
		
		//do not open unless you want to index !
		
		
		
		//r.fetch(r.search("Pari?"));
		//r.fetch(r.search("temple"));
		//r.fetch(r.search("istan*"));
		//r.fetch(r.search("Istanbul"));
		//r.fetch(r.search("P*tan"));
		//r.fetch(r.search("I????"));
		//r.fetch(r.search("8796"));
		//r.fetch(r.search( "temple  AND NOT table:actor"  ));
		//r.fetch(r.search("Pari~"));
		//r.fetch(r.search("+new AND NOT table:(city OR film)" ));
		
		
		//r.fetch(r.search("ajay*"));
		
		

	}

}

