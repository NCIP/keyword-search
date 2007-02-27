/**
 * 
 */
package titli.model;



import java.util.Map;




/**
 * Represents database metadata
 * @author Juber Patel
 *
 */
public class Database 
{
	private String name;
	private Map<String, Table> tables;
	
	
	
	/**
	 * 
	 * @param name name of the database
	 * @param tables the list of tables which are part of the databases
	 */
	public Database(String name, Map<String, Table> tables)
	{
		this.name = name;
		this.tables = tables;
		
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
	 * Get the number of tables in the database
	 * @return the number of tables in the database
	 */
	public int getNumberOfTables()
	{
		return tables.size();
	}
	
	
	/**
	 * return the table specified by name  
	 * @param name the name of the table
	 * @return the table
	 */
	public Table getTable(String name)
	{
		return tables.get(name);
	}
	
	/**
	 * Get a map of all the tables in the database
	 * @return a map of all the table names and tables in the database
	 */
	public Map<String, Table> getTables()
	{
		return tables;
	}
	
	
	/**
	 * @param args args to main
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
