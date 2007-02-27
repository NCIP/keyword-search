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
public interface TableInterface
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
	 * Get the number of columns in the table
	 * @return the number of columns in the table
	 */
	int getNumberOfColumns();
	
	
	/**
	 * 
	 * @param i the number of the column
	 * @return the corresponding column
	 */
	ColumnInterface getColumn(int i);
	
}
