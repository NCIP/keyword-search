/**
 * 
 */
package titli.model.fetch;

import java.sql.*;

import java.util.*;
import java.util.Date;

import titli.*;
import titli.model.*;
import titli.model.search.Match;
import titli.model.search.MatchList;


/**
 * the class that will fetch the actual matching records from the databases
 * the future of Fetcher depends on what kind of outputs are expected
 * @author Juber Patel
 *
 */
public class Fetcher
{
	public Fetcher()
	{
		
		
	}

	
	/**
	 * fetch the actual records from the database
	 * @param matchList the list of matches for which to fetch the records
	 * @throws SQLException
	 */
	public static void fetch(MatchList matchList, Map<String, RDBMSReader> readers) 
	{
			
		long start = new Date().getTime();
	
		try
		{
			//for each match
			for(Match match : matchList)
			{
				Connection conn = readers.get(match.getDatabaseName()).getFetchConnection();
				Statement searchstmt = conn.createStatement();
			
				ResultSet rs = searchstmt.executeQuery(match.getQuerystring());
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
			
				searchstmt.close();
			}
		}
		catch(SQLException e)
		{
			System.out.println("SQLException happened "+e);
			//System.out.println("String : "+matc);
		}
		

		long end = new Date().getTime();
		
		System.out.print("\nFetch took "+(end-start)/1000.0+" seconds");
	

	}	
			
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
