/**
 * 
 */
package titli.model.search;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


/**
 *A class that represents a match in for a given search query
 * @author Juber Patel
 *
 */


public class RecordMatch implements  titli.controller.interfaces.Match
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
	RecordMatch(Document doc)
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
	
	
	
	/**
	 * the database name
	 * @return the name of the database
	 */
	public String getDatabaseName()
	{
		return dbName;
	}
	
	/**
	 * the table name
	 * @return the name of the table
	 */
	public String getTableName()
	{
		return tableName;
	}
	
	/**
	 * The SQL query that must be fired to fetch the record corresponding to the match
	 * @return the relevant SQL query
	 */
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
	
	/**
	 * String representation of the RecordMatch
	 * @return the string representation
	 */
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
	
	
}
