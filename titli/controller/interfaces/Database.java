/**
 * 
 */
package titli.controller.interfaces;

/**
 * Represents database metadata
 * @author Juber Patel
 *
 */
public interface Database
{

	/**
	 * get the name of the database
	 * @return return the name of the database
	 */
	String getName();
	
	/**
	 * return the table specified by a number 
	 * @param i the number of the table
	 * @return the table
	 */
	Table getTable(int i);
	
	/**
	 * return the table specified by the name 
	 * @param name the name of the table
	 * @return the table
	 */
	Table getTable(String name);
	
	
}
