/**
 * 
 */
package titli.model.util;

import java.io.File;

import titli.model.Titli;
import titli.model.TitliConstants;
import titli.model.TitliException;

/**
 * @author Juber Patel
 *
 */
public class IndexUtility 
{
	/**
	 * get the directory corresponding to the index of the specified table
	 * @param dbName the database name
	 * @param tableName the table name
	 * @return the File object corresponding to the table index directory 
	 * @throws TitliException if problems occur
	 */
	public static File getIndexDirectoryForTable(String dbName, String tableName) throws TitliException
	{
		File indexDir = Titli.getInstance().getIndexLocation();
		
		File dbDir = new File(indexDir, dbName+TitliConstants.INDEX_DIRECTORY_SUFFIX);
		return new File(dbDir, tableName+TitliConstants.INDEX_DIRECTORY_SUFFIX);
		
	}
	
		
	/**
	 * get the directory corresponding to the index of the specified database
	 * @param dbName the database name
	 * @return the File object corresponding to the table index directory 
	 * @throws TitliException if problems occur
	 */
	public static File getIndexDirectoryForDatabase(String dbName) throws TitliException
	{
		File indexDir = Titli.getInstance().getIndexLocation();
		
		return new File(indexDir, dbName+TitliConstants.INDEX_DIRECTORY_SUFFIX);
		
	}


	/**
	 * @param args args for main
	 * @throws TitliException if problems occur
	 */
	public static void main(String[] args) throws TitliException 
	{
		try
		{
			Titli.getInstance();
		}
		catch (TitliException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(IndexUtility.getIndexDirectoryForDatabase("catissuecore41"));

	}

}
