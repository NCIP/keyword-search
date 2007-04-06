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
	 */
	public static File getIndexDirectoryForTable(String dbName, String tableName)
	{
		String path = System.getProperty(TitliConstants.TITLI_INDEX_LOCATION);
		
		path = path +"/"+dbName+TitliConstants.INDEX_DIRECTORY_SUFFIX+"/"+tableName+TitliConstants.INDEX_DIRECTORY_SUFFIX;
		
		return new File(path);
		
		/*
		File directory = new File(System.getProperty(TitliConstants.TITLI_INDEX_LOCATION));
		
		directory = new File(directory, dbName+TitliConstants.INDEX_DIRECTORY_SUFFIX);
		
		directory = new File(directory, tableName+TitliConstants.INDEX_DIRECTORY_SUFFIX);
		*/
	}
	
		
	/**
	 * get the directory corresponding to the index of the specified database
	 * @param dbName the database name
	 * @return the File object corresponding to the table index directory 
	 */
	public static File getIndexDirectoryForDatabase(String dbName)
	{
		String path = System.getProperty(TitliConstants.TITLI_INDEX_LOCATION);
		
		path = path +"/"+dbName+TitliConstants.INDEX_DIRECTORY_SUFFIX;
		
		return new File(path);
		
	}


	/**
	 * @param args args for main
	 */
	public static void main(String[] args) 
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
