/**
 * 
 */
package titli.model;

import java.util.*;

/**
 * Represents database metadata
 * @author Juber Patel
 *
 */
public class Database 
{
	private String name;
	private ArrayList<Table> tables;
	public final int noOfTables;
	
	public Database(String name, List<Table> tables)
	{
		this.name = name;
		this.tables = new ArrayList<Table>(tables);
		noOfTables = this.tables.size();
	}

	
	public String getName()
	{
		return name;
	}
	
	
	public Table getTable(int i)
	{
		return tables.get(i);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
