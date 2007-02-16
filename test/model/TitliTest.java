/**
 * 
 */
package test.model;



import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import titli.Titli;

/**
 * @author Juber Patel
 *
 */
public class TitliTest
{

	private Titli titli;
	
	/**
	 * @throws java.lang.Exception dddd
	 */
	@Before
	public void setUp() throws Exception
	{
		titli = new Titli("E:/juber/workspace/TiTLi/titli/model/titli.properties");
		
	}
	
	/**
	 * @throws java.lang.Exception dddd
	 */
	@After
	public void tearDown() throws Exception 
	{
		
	}

	//@Ignore is NOT working ! Not Even with JUnit4.1 !!
	//Eclipse is using JUnit4.1. No need to run form outside !
	
	
	/**
	 * test the set parameters, variables and properties
	 */
	@Test
	public void constructorTest()
	{
		assertEquals("Databases should be 2 !!", titli.noOfDatabases,2);
		
		assertNotSame("No databases read !!", titli.noOfDatabases,0);
		
		assertNotNull("JDBC Drivers String Empty !!", System.getProperty("jdbc.drivers"));
		
		assertNotNull("Index Location String Empty !!", System.getProperty("titli.index.location"));
		
		assertNotNull("instance is still null !!", Titli.getInstance());
	}
	
	/**
	 * test the indexing functionality
	 *
	 */
	@Test
	public void indexTest()
	{
		
	}
	
	
	
	/**
	 * Test method for {@link titli.Titli#main(java.lang.String[])}.
	 * @throws IOException for io
	 * 
	 */
	//@Test(timeout=5000)
	public void testMain() throws IOException
	{
		
		Titli.main(new String[0]);
	}
	
	
		
	
	
}
