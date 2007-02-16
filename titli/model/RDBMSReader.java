/**
 * 
 */
package titli.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;



/**
 * The class that handles the connections to the database etc.
 * It has the index, search and fetch methods 
 * @author Juber Patel
 *
 */
public class RDBMSReader 
{
	private String url;
	private String dbName;
	private String username; 
	private String password;
	private Connection indexConnection;
	private Connection searchConnection;
	private Database database;
	
	private List<String> invisiblePrefixes;
	private List<String> invisibleTables;
	
	
	/**
	 * 
	 * @param props the properties file containing information for connection to database
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public RDBMSReader(Properties props) 
	{
		
		dbName = props.getProperty("jdbc.database");
		
		url = props.getProperty("jdbc."+dbName+".url");
		username = props.getProperty("jdbc."+dbName+".username");
		password = props.getProperty("jdbc."+dbName+".password");
		
		invisiblePrefixes = new ArrayList<String>();
		invisibleTables = new ArrayList<String>();
		
		//populate lists of tables NOT to be indexed
		Scanner s = new Scanner(props.getProperty("titli."+dbName+".noindex.prefix"));
		
		System.out.println("titli."+dbName+".noindex.prefix ::" +props.getProperty("titli."+dbName+".noindex.prefix"));
		s.useDelimiter("\\s*,\\s*");
		while(s.hasNext())
		{
			invisiblePrefixes.add(s.next());
		}
		
		s = new Scanner(props.getProperty("titli."+dbName+".noindex.table"));
		s.useDelimiter("\\s*,\\s*");
		while(s.hasNext())
		{
			invisibleTables.add(s.next());
		}
		
		
		System.out.println("Reader created for "+dbName+"...");
	
		try
		{
			//both of the calls work well
			indexConnection = DriverManager.getConnection(url+"?user="+username+"&password="+password);
			searchConnection = DriverManager.getConnection(url+"?user="+username+"&password="+password);
			//conn = DriverManager.getConnection(url, username, password);
			//System.out.println("Connection to the database successful...");
			
			
		}
		catch(SQLException e)
		{
			System.out.println("SQLException happened"+e);
			e.printStackTrace();
		}
	
	}
	
	
	/**
	 * 
	 * @return the database for the reader
	 */
	public Database getDatabase()
	{
		//build the database metadata
		if(database==null)
		{
			System.out.println("Meta Data is null");
			List<Table> tables = new ArrayList<Table> ();
			try
			{
				DatabaseMetaData dbmd = indexConnection.getMetaData();
				Statement stmt = indexConnection.createStatement();
				
				//get table names
				ResultSet rs = dbmd.getTables(null, null, null, new String[] {"TABLE"});
				
				
				//for each table
				while(rs.next())
				{
					String tableName = rs.getString("TABLE_NAME");
					
					//ignore invisible tables
					if(!isVisible(tableName))
					{
						continue;
					}
					
					List<String> uniqueKey = new ArrayList<String>();
					List<Column> columns = new ArrayList<Column>();
					//get unique keys
					ResultSet keys = dbmd.getBestRowIdentifier(null, null, tableName,DatabaseMetaData.bestRowSession, true);
					
					//add unique keys
					while(keys.next())
					{
						uniqueKey.add(keys.getString("COLUMN_NAME"));
						
					}
					
					keys.close();
					
					if(uniqueKey.size()==0)
					{
						System.out.println("table  "+tableName+" does not have unique key ! Skipping...");
						continue;
					}
					
					//fire a dummy query to get table metadata
					String query = "select * from "+tableName+" where "+uniqueKey.get(0)+" = null;";
					System.out.println(query);
					ResultSet useless = stmt.executeQuery("select * from "+tableName+" where "+uniqueKey.get(0)+" = null; ");
					ResultSetMetaData tablemd = useless.getMetaData();
					
					int numcols = tablemd.getColumnCount();
					//for each column
					for(int i=1;i<=numcols;i++)
					{
						String columnName = tablemd.getColumnName(i);
						String columnType = tablemd.getColumnTypeName(i);
						
						columns.add(new Column(columnName, columnType));
						
					}
					
					useless.close();
					
					//add the table to the list
					tables.add(new Table(tableName, uniqueKey, columns));
					
				}
				
				rs.close();
				stmt.close();
			
				database = new Database(dbName, tables);
				
				System.out.println("Meta Data created  No of Tables : "+database.noOfTables);
				
			}
			catch(SQLException e)
			{
				System.out.println("SQL Error happened"+e);
				e.printStackTrace();
			}
			
			
		}
		
		return database;
	}
	
	/**
	 * return the connection to be used by an indexer
	 * @return the connection
	 */
	public Connection getIndexConnection()
	{
		return indexConnection;
	}
	
	/**
	 * return the connection to be used by a fetcher
	 * @return the connection
	 * 
	 */
	public Connection getFetchConnection()
	{
		return searchConnection;
	}
	
	
	
	
		
	
	/**
	 * close the database connections
	 * 
	 */
	protected void finalize() 
	{
		try
		{
			indexConnection.close();
			searchConnection.close();
		}
		catch(SQLException e)
		{
			System.out.println("SQL Exception happened"+e);
			e.printStackTrace();
		}
			
	}
	
	/**
	 * whether the given table is visible to TiTLi
	 * @param tableName the table
	 * @return true if this table is to be indexed and searched otherwise false
	 */
	private boolean isVisible(String tableName)
	{
		if(invisibleTables.contains(tableName))
		{
			return false;
		}
		
		for(String prefix : invisiblePrefixes)
		{
			if(tableName.startsWith(prefix))
			{
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * @param args args to main
	 */
	public static void main(String[] args)
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

