/**
 * 
 */
package titli.controller.interfaces;

/**
 * Represents database metadata
 * @author Juber Patel
 *
 */
public interface DatabaseInterface
{

	/**
	 * get the name of the database
	 * @return return the name of the database
	 */
	String getName();
	
	
	/**
	 * Get the number of tables in the database
	 * @return the number of tables in the database
	 */
	int getNumberOfTables();
	
	
	
	/**
	 * return the table specified by a number 
	 * @param i the number of the table
	 * @return the table
	 */
	TableInterface getTable(int i);
	
	/**
	 * return the table specified by the name 
	 * @param name the name of the table
	 * @return the table
	 */
	TableInterface getTable(String name);
	
	
}
