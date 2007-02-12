/**
 * 
 */
package titli.model;

import java.util.*;


/**
 * Represents table metadata like table name, columns etc.
 * @author Juber Patel 
 *
 */
public class Table
{
	private String name;
	private ArrayList<Column> columns;
	public final int noOfColumns;
	
	public Table(String name, List<Column> columns)
	{
		this.name = new String(name);
		this.columns =  new ArrayList<Column> (columns);
		noOfColumns = columns.size();
		
	}
	
	
	/**
	 * returns the name of the table
	 * @return the name of the table
	 */
	public String getName()
	{
		return name;
		
	}
	
	public Column getColumn(int i)
	{
		return columns.get(i);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
