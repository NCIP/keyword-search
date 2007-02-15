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
	private Map<String, String> uniqueKeys ;
	private String self;
	private String queryString;
	
	
	private String dbName;
	private String tableName;
	
		
	/**
	 * @param doc the document that matched the search query
	 */
	//for package use only
	Match(Document doc)
	{
		//get the fields and values
		uniqueKeys = new HashMap<String, String> ();
				
		this.tableName = doc.get("table");
		this.dbName = doc.get("database");
		
		Enumeration e = doc.fields();
		
		while (e.hasMoreElements())
		{
			String name = ((Field)(e.nextElement())).name();
			
			//don't include the table  name and database name
			if(name.equals("table") || name.equals("database"))
			{
				continue;
			}
					
			uniqueKeys.put(name, doc.get(name));
		}
		
		
		
		
			
		
	}
	
	
	public String getDatabaseName()
	{
		return dbName;
	}
	
	
	public String getTableName()
	{
		return tableName;
	}
	
	
	public String getQuerystring()
	{
		if(queryString==null)
		{
			//buld the corresponding SQL query
			StringBuilder query = new StringBuilder("SELECT * FROM ");
			
			query.append(tableName+" WHERE ");
			
			//add each column and value pair 
			for(String colName : uniqueKeys.keySet())
			{
				query.append(colName+" = '"+uniqueKeys.get(colName)+"'  and ");
							
			}
			
			//remove the last 'and'
			queryString  = query.substring(0, query.lastIndexOf("and")) + ";";
		
		}
		
		return queryString;
	}
	
	
	public String toString()
	{
		if(self==null)
		{
			//build string representation
			StringBuilder string = new StringBuilder("Database : "+dbName+"    Table : "+tableName);
			
			string.append("\nUnique Key Set : ");
			
			for(String colName : uniqueKeys.keySet())
			{
				string.append("   "+colName+" : "+uniqueKeys.get(colName));
			}
			
			self = new String(string);
			
		}
		
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
