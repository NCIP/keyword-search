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
	public RDBMSReader(String propertiesUrl)  throws FileNotFoundException, IOException, SQLException
	{
		
		Properties props = new Properties();
		FileInputStream in = new FileInputStream(propertiesUrl);
		
		props.load(in);
		in.close();
		
		//set all the private fields to proper values
		System.setProperty("jdbc.drivers", props.getProperty("jdbc.drivers"));
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
	 *  index all the tables in the current database
	 */
	public boolean index() throws SQLException, IOException
	{
			return true;
	}
	
	/**
	 * search for the given query using the index
	 * @param searchString the user query to be searched 
	 * @return a list of matches
	 * @throws IOException
	 * @throws SQLException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */
	public MatchList search(String searchString) throws IOException, SQLException, org.apache.lucene.queryParser.ParseException
	{
		
		//discover table names
		ArrayList<String> tableList = new ArrayList<String>();
		DatabaseMetaData dbmd = searchConnection.getMetaData();
		ResultSet rs = dbmd.getTables(null, null, null, null);
		while(rs.next())
		{
			String tableName = rs.getString("TABLE_NAME");
			/*
			if(!toIndex(tableName))
				continue;*/
			
			tableList.add(tableName);
				
		}
		rs.close();
			
		MatchList matchList = new MatchList();
		
		System.out.println("Searching for " +searchString+"...");
		Analyzer analyzer = new StandardAnalyzer();
		
		int numTables = tableList.size();
		IndexSearcher[] searchers = new IndexSearcher[numTables];
		
		for (int i=0; i<numTables; i++)
		{
			
			RAMDirectory ramDir = new RAMDirectory(new File(databaseIndexDir, tableList.get(i)+"_index"));
			searchers[i] = new IndexSearcher(ramDir);
			
		}
		
		MultiSearcher ms = new MultiSearcher(searchers);
		QueryParser qp = new QueryParser("content", analyzer);
		
		Query query = qp.parse(searchString);
		
		long start = new Date().getTime();
		//search for the query
		Hits hits = ms.search(query);
		long end = new Date().getTime();
		
		int listLength = hits.length();
		
		
		//build the match list	
		for(int i=0;i<listLength;i++)
		{
			matchList.add(new Match(hits.doc(i)));
					
			//matchList.add(new Match(hits.doc(i).get("ID"),hits.doc(i).get("TableName"),"Not Known"));
			//System.out.println(hits.doc(i).get("Population")+"  "+hits.doc(i).get("CountryCode"));
			
		}
		
		System.out.println("\n The search took " + (end-start)/1000.0 + " seconds");
		System.out.println("\n Found "+listLength+" matches");
		
		System.out.println("\nThe matches are : ");
		for(Match match : matchList)
		{
			System.out.println(match);
			System.out.println(match.getQuerystring()+"\n");
		}
		
		System.out.println("\n The search took " + (end-start)/1000.0 + " seconds");
		
		//close all the index searchers : NOT to be called before you are done with Hits etc.
		for (int i=0; i<numTables; i++)
		{
			searchers[i].close();
			
		}
		
		
		return matchList;
	}
	
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
		RDBMSReader r = new RDBMSReader("E:/juber/workspace/TiTLi/titli/model/database.properties");
		
		//do not open unless you want to index !
		r.index();
		
		
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

