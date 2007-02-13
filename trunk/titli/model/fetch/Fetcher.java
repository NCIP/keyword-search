/**
 * 
 */
package titli.model.fetch;

import java.sql.*;

import java.util.Date;

import titli.model.*;


/**
 * @author juberahamad_patel
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
	public void fetch(MatchList matchList, Connection conn) throws SQLException
	{
		Statement searchstmt = conn.createStatement();
		
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
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
