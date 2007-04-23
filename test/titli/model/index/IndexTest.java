/**
 * 
 */
package titli.model.index;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import titli.controller.interfaces.DatabaseInterface;
import titli.controller.interfaces.TableInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import titli.model.util.IndexUtility;

/**
 * @author Juber Patel
 *
 */
public class IndexTest
{
	private static TitliInterface titli;
	
	/**
	 * 
	 * @throws TitliException if
	 */
	@BeforeClass
	public static void beforeClass() throws TitliException
	{
		titli = Titli.getInstance();
	}
	
	/**
	 * 
	 *
	 */
	@AfterClass
	public static void afterClass()
	{
		
	}
	
	
	/**
	 * index all the databases mentioned in titli.properties
	 * @throws TitliException if
	 *
	 */
	@Test
	public void indexAllDatabases() throws TitliException
	{
		titli.index();
		
		//check if directories have been created for all databases and tables
		
		Map<String, DatabaseInterface>databases = titli.getDatabases();
		//for each database
		for(String dbName : databases.keySet())
		{
			File dbDir = IndexUtility.getIndexDirectoryForDatabase(dbName);
			assertTrue("index directory for database "+dbName+" does not exist !!", dbDir.exists());
			
			Map<String, TableInterface> tables = titli.getDatabase(dbName).getTables();
			//for each table
			for(String tableName : tables.keySet())
			{
				File tableDir = IndexUtility.getIndexDirectoryForTable(dbName, tableName);
				assertTrue("index directory for table "+tableName+" in database "+dbName+" does not exist !!", tableDir.exists());
				
			}
		}

	}
	
	/**
	 * index a valid database
	 * @throws TitliException if
	 * 
	 *
	 */
	@Test
	public void indexValidDatabase() throws TitliException
	{
		String dbName = "catissuecore11";
		titli.index(dbName);
				
		//check if directories for all indexable tables have been created
			
		Map<String, TableInterface> tables = titli.getDatabase(dbName).getTables();
		//for each table
		for(String tableName : tables.keySet())
		{
			File tableDir = IndexUtility.getIndexDirectoryForTable(dbName, tableName);
			assertTrue("index directory for table "+tableName+" in database "+dbName+" does not exist !!", tableDir.exists());
			
		}
	}
	
	
	/**
	 * index an invalid database
	 * @throws TitliException if
	 *
	 */
	@Test(expected=NullPointerException.class)
	public void indexInvalidDatabase() throws TitliException
	{
		String dbName = "garbage";
		
		try
		{
			titli.index(dbName);
		}
		//the index direcotry must not exist for this database
		finally
		{
			File dbDir = IndexUtility.getIndexDirectoryForDatabase(dbName);
			assertFalse("directory created for invalid database "+dbName+" !!",dbDir.exists());
		}
	
	}
	
	
	/**
	 * index a valid table 
	 * @throws TitliException if
	 * 
	 */
	@Test
	public void indexValidTable() throws TitliException
	{
		String dbName = "catissuecore11";
		String tableName = "catissue_institution"; 
		titli.index(dbName, tableName);
		
		File tableDir = IndexUtility.getIndexDirectoryForTable(dbName, tableName);
		assertTrue("index directory for table "+tableName+" in database "+dbName+" does not exist !!", tableDir.exists());
		
	}
	
	
	/**
	 * index an invalid table 
	 * @throws TitliException if
	 * 
	 */
	@Test(expected=NullPointerException.class)
	public void indexInvalidTable() throws TitliException
	{
		String dbName = "catissuecore11";
		String tableName = "catissue_garbage"; 
		
		try
		{
			titli.index(dbName, tableName);
		}
		//the index direcotry must not exist for this table
		finally
		{
			File tableDir = IndexUtility.getIndexDirectoryForTable(dbName, tableName);
			assertFalse("directory created for invalid table "+tableName+" in database "+dbName+" !!", tableDir.exists());
		}
	
	}
	
	
}
