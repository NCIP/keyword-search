/**
 * 
 */
package titli.controller.interfaces;

import java.util.List;



/**
 * Represents table metadata like table name, columns etc.
 * @author Juber Patel
 *
 */
public interface Table
{
	/**
	 * returns the name of the table
	 * @return the name of the table
	 */
	String getName();
	
	
	/**
	 * returns the name of the table
	 * @return the name of the table
	 */
	String getDatabaseName();
	
	
	/**
	 * 
	 * @return the unique key set for the table
	 */
	List<String> getUniqueKey();
	
	/**
	 * 
	 * @param i the number of the column
	 * @return the corresponding column
	 */
	Column getColumn(int i);
	
}
