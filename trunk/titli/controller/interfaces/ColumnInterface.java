/**
 * 
 */
package titli.controller.interfaces;

/**
 *  Represents a column of a table
 * @author Juber Patel
 *
 */
public interface ColumnInterface 
{

	/**
	 * Get the name of the column
	 * @return name of the column
	 */
	String getName();
	
	
	/**
	 * Get the name of the table in which the column belongs
	 * @return name of the table
	 */
	String getTableName();
	
	
	/**
	 * Get the SQL datatype of the column
	 * @return the SQL datatype of the column
	 */
	String getType();
	
	
}
