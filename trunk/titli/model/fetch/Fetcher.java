/**
 * 
 */
package titli.model.fetch;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import titli.model.Column;
import titli.model.Database;
import titli.model.RDBMSReader;
import titli.model.Table;
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
	 * @return the record corresponding to the specified match
	 */
	public Record fetch(Match match) 
	{
		
		Record record = null;
			
		try
		{
			long start = new Date().getTime();
			
			Map<Column, String> columnMap = new HashMap<Column, String>();
			Table table = getDatabase().getTable(match.getTableName());
			
			ResultSet rs = fetchstmt.executeQuery(match.getQuerystring());
			ResultSetMetaData rsmd = rs.getMetaData();
			
			rs.next();
			
			int columns = rsmd.getColumnCount();
			
			//System.out.println("Database : "+match.getDatabaseName()+"  Table : " +match.getTableName());
			
			//populate the column map
			for(int i=1; i<=columns; i++)
			{
				//System.out.print(rsmd.getColumnName(i)+" : "+rs.getString(i)+"  ");
				
				String columnName = rsmd.getColumnName(i);
								
				Column column = table.getColumn(columnName);
				
				columnMap.put(column, rs.getString(i));
			}
			
			long end = new Date().getTime();
			double time = (end-start)/1000.0;
			
			//create the record
			record = new Record(table, columnMap, time); 
			
			//System.out.println("\n");
			rs.close();
			
		}
		catch(SQLException e)
		{
			System.out.println("SQLException happened "+e);
			e.printStackTrace();
			//System.out.println("String : "+matc);
		}
		
		return record;
		
	}	
			
	

	/**
	 * @param args arguments to main
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
