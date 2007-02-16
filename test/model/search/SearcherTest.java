/**
 * 
 */
package test.model.search;


import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import titli.model.Column;
import titli.model.Table;
import titli.model.search.Searcher;


/**
 * @author juberahamad_patel
 *
 */
public class SearcherTest 
{
	private Searcher searcher;
	
	/**
	 * @throws java.lang.Exception for exception
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
	 * @throws java.lang.Exception for exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		
		
	}

	/**
	 * @throws java.lang.Exception for exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		
	}

	/**
	 * @throws java.lang.Exception for exception
	 */
	@After
	public void tearDown() throws Exception
	{
		
		
	}

}
