/**
 * 
 */
package titli.model.search;

import org.apache.lucene.document.*;
import java.util.*;


/**
 *A class that represents a match in for a given search query
 * @author Juber Patel
 *
 */


public class Match
{
	//stores columns and and values alternately
	private StringBuilder uniqueKeys ;
	private String self;
	private String queryString;
	
	
	private String database;
	private String table;
	
	//private String column;
	
	/**
	 * @param doc the document that matched the search query
	 */
	//for package use only
	Match(Document doc)
	{
		//get the fields and values
		uniqueKeys = new StringBuilder();
				
		this.table = doc.get("table");
		this.database = doc.get("database");
		
		Enumeration e = doc.fields();
		
		while (e.hasMoreElements())
		{
			String name = ((Field)(e.nextElement())).name();
			
			uniqueKeys.append(name+" ");
			uniqueKeys.append(doc.get(name)+" ");
		}
		
		
		//build string representation
		StringBuilder string = new StringBuilder("Table : "+table);
		
		string.append("\nUnique Key Set : ");
		
		Scanner scanner = new Scanner(new String(uniqueKeys));
		
		while(scanner.hasNext())
		{
			string.append(scanner.next()+" : "+scanner.next()+"   ");
			
		}
		
		self = new String(string+"\n\n");
		scanner.close();
		
		//buld the corresponding SQL query
		StringBuilder query = new StringBuilder("SELECT * FROM ");
		
		query.append(table+" WHERE ");
		
		scanner = new Scanner(new String(uniqueKeys));
		
		while(scanner.hasNext())
		{
			String key = scanner.next();
			
			
			//don't include the table name
			if(key.equals("table") || key.equals("database"))
			{
				scanner.next();
				continue;
				
			}
			
			query.append(key+" = '"+scanner.next()+"'  and ");
						
		}
		
		queryString  = query.substring(0, query.lastIndexOf("and")) + ";";
		
		
	}
	
	
	public String getDatabase()
	{
		return database;
	}
	
	
	public String getTable()
	{
		return table;
	}
	
	public String getQuerystring()
	{
		return queryString;
	}
	
	
	public String toString()
	{
		return self;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
