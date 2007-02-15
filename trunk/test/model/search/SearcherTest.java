/**
 * 
 */
package test.model.search;

import java.util.*;

import titli.model.search.*;
import titli.model.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author juberahamad_patel
 *
 */
public class SearcherTest 
{
	private Searcher searcher;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		//create a Database
		
		List<Column> list = new ArrayList<Column>();
		
		list.add(new Column("id","integer"));
		list.add(new Column("name", "string"));
		list.add(new Column("age","real"));
		
		
		List<String> list1 = new ArrayList<String>();
		list1.add("id");
		list1.add("name");
		
		Table t1 = new Table("t1", list1, list);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		
		
	}

}
