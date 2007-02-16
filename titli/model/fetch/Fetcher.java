/**
 * 
 */
package titli.model.fetch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import titli.model.Database;
import titli.model.RDBMSReader;
import titli.model.search.Match;



/**
 * the class that will fetch the actual matching records from the databases
 * the future of Fetcher depends on what kind of outputs are expected
 * @author Juber Patel
 *
 */
public class Fetcher
{
	private RDBMSReader reader;
	private Statement fetchstmt;
	
	/**
	 * 
	 * @param dbReader the reader for which the fetcher should be built
	 */
	public Fetcher(RDBMSReader dbReader)
	{
		reader = dbReader;
		try
		{
			fetchstmt = reader.getFetchConnection().createStatement();
		}
		catch(SQLException e)
		{
			
			System.out.println("SQL Exception happend"+e);
			e.printStackTrace();
		}
		
		
		
	}

	
	/**
	 *  
	 * @return the corresponding database
	 */
	public Database getDatabase()
	{
		return reader.getDatabase();
	}
	
	
	/**
	 * fetch the actual records from the database
	 * @param match the match for which to fetch the records
	 * @throws SQLException
	 */
	public void fetch(Match match) 
	{
			
		try
		{
				
			ResultSet rs = fetchstmt.executeQuery(match.getQuerystring());
			ResultSetMetaData rsmd = rs.getMetaData();
			
			rs.next();
			
			int columns = rsmd.getColumnCount();
			
			System.out.println("Database : "+match.getDatabaseName()+"  Table : " +match.getTableName());
			
			
			for(int i=1; i<=columns; i++)
			{
				System.out.print(rsmd.getColumnName(i)+" : "+rs.getString(i)+"  ");
			}
			
			System.out.println("\n");
			rs.close();
		
		}
		catch(SQLException e)
		{
			System.out.println("SQLException happened "+e);
			e.printStackTrace();
			//System.out.println("String : "+matc);
		}
		
	}	
			
	

	/**
	 * @param args arguments to main
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
