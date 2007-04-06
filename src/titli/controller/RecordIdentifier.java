/**
 * 
 */
package titli.controller;

import java.util.Map;

import org.apache.lucene.document.Document;

import titli.model.TitliConstants;

/**
 * This class represents the identification of a record in databases
 * This identification includes database name, table name and a map of column name => column value pairs
 * for the columns that make up the unique key for the record  
 * @author Juber Patel
 *
 */
public class RecordIdentifier 
{
	private String dbName;
	private String tableName;
	private Map<String, String> uniqueKey;
	
	/**
	 * 
	 * @param dbName the database name
	 * @param tableName the table name
	 * @param uniqueKey the map of unique key column name => column value
	 */
	public RecordIdentifier(String dbName, String tableName, Map<String, String>uniqueKey)
	{
		this.dbName = dbName.trim();
		this.tableName = tableName.trim();
		this.uniqueKey = uniqueKey;
		
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() 
	{
		return dbName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() 
	{
		return tableName;
	}

	
	/**
	 * @return the uniqueKey
	 */
	public Map<String, String> getUniqueKey() 
	{
		return uniqueKey;
	}
	
	
	/**
	 * determine whether this identifier represents the specified Document 
	 * @param doc the document
	 * @return true if this identifier represents the specified Document, otherwise false
	 */
	public boolean matches(Document doc)
	{
		
		if(!doc.get(TitliConstants.DOCUMENT_DATABASE_FIELD).trim().equals(getDbName()))
		{
			return false;
		}
		
		if(!doc.get(TitliConstants.DOCUMENT_TABLE_FIELD).trim().equals(getTableName()))
		{
			return false;
		}
		
		for(String column : getUniqueKey().keySet())
		{
			if(doc.get(column)==null)
			{
				return false;
			}
			
			if(!doc.get(column).trim().equals(getUniqueKey().get(column)))
			{
				return false;
			}
		}
		
		return true;
		
	}
	
}
