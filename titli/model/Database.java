/**
 * 
 */
package titli.model;



import java.util.ArrayList;
import java.util.List;




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
	
	
	/**
	 * 
	 * @param name name of the database
	 * @param tables the list of tables which are part of the databases
	 */
	public Database(String name, List<Table> tables)
	{
		this.name = name;
		this.tables = new ArrayList<Table>(tables);
		noOfTables = this.tables.size();
	}

	/**
	 * get the name of the database
	 * @return return the name of the database
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * return the table specified by a number 
	 * @param i the number of the table
	 * @return the table
	 */
	public Table getTable(int i)
	{
		return tables.get(i);
	}
	
	/**
	 * @param args args to main
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
