/**
 * 
 */
package test.model;



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
	@Ignore("not ready yet")
	@Test
	public void constructorTest()
	{
		assertNotSame("No databases read !!", titli.noOfDatabases,0);
		
		assertNotNull("JDBC Drivers String Empty !!", System.getProperty("jdbc.drivers"));
		
		assertNotNull("Index Location String Empty !!", System.getProperty("titli.index.location"));
		
		assertNotNull("instance is still null !!", Titli.getInstance());
	}
	
	/**
	 * test the indexing functionality
	 *
	 */
	@Ignore("not ready yet")
	@Test
	public void indexTest()
	{
		titli.index();
				
	}
	
	
	
	/**
	 * Test method for {@link titli.Titli#main(java.lang.String[])}.
	 * @throws IOException for io
	 * 
	 */
	@Ignore("not ready yet")
	@Test(timeout=5000)
	public void testMain() throws IOException
	{
		
		Titli.main(new String[0]);
	}
	
	
		
	
	
}
