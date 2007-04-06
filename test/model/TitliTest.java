/**
 * 
 */
package test.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import titli.controller.interfaces.MatchInterface;
import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.TitliInterface;
import titli.controller.interfaces.record.RecordInterface;
import titli.model.Titli;
import titli.model.TitliConstants;
import titli.model.TitliException;


/**
 * The class to test the Titli Model functionality
 * @author Juber Patel
 *
 */
public class TitliTest
{

	private static TitliInterface titli;
	
	/**
	 * 
	 */
	@BeforeClass
	public static void setUp() 
	{
		
		try 
		{
			titli = Titli.getInstance();
		}
		catch (TitliException e) 
		{
			System.out.println(e+"\n"+e.getCause());
			System.exit(0);
		}
		
	}
	
	/**
	 * 
	 */
	@AfterClass
	public static void tearDown()  
	{
		
	}

	
	//Eclipse is using JUnit4.1. No need to run form outside !
	//And @Ignore Tag is also working !!
	
	
	/**
	 * test the set parameters, variables and properties
	 */
	@Test
	public void constructorTest()
	{
		assertNotSame("No databases read !!", titli.getNumberOfDatabases(),0);
		
		assertNotNull("JDBC Drivers String Empty !!", System.getProperty(TitliConstants.JDBC_DRIVERS));
		
		assertNotNull("Index Location String Empty !!", System.getProperty(TitliConstants.TITLI_INDEX_LOCATION));
		
		
	}
	
	
	/**
	 * test the indexing functionality
	 *
	 */
	@Ignore("Don't do it everytime !")
	@Test
	public void indexTest()
	{
		try
		{
			titli.index();
		}
		catch (TitliException e) 
		{
			System.out.println(e+"\n"+e.getCause());
			System.exit(0);
		}
		
				
	}
	
	
	
	/**
	 * 
	 * test the match and fecth functionality
	 * 
	 */
	@Test(timeout=5000)
	public void matchAndFetchTest() 
	{
		//MatchList matchList =titli.search("new +bombay");
		//titli.fetch(matchList);
		
		//Fetcher.fetch(titli.search("Temple"),titli.dbReaders);
		//Fetcher.fetch(titli.search("ajay"),titli.dbReaders);
		//Fetcher.fetch(titli.search("pari~"),titli.dbReaders);
		
		MatchListInterface matchList;
		try
		{
			matchList = titli.search("new +bombay");
		
			for(MatchInterface match : matchList)
			{
				RecordInterface record = match.fetch();
				
				System.out.println(record);
			}
		}
		catch (TitliException e) 
		{
			System.out.println(e+"\n"+e.getCause());
			System.exit(0);
		}
		
		
			
		
		//querying a remote databse : cab2b on Vishvesh's machine
		//MatchList matchList = titli.search("1298_1150_1372");
		//titli.fetch(matchList);
		
		//MatchList matchList = titli.search("tilburg");
		//titli.fetch(matchList);
		
	}
	
	
		
	
	
}
