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
	 * @return name of the coloumn
	 */
	String getName();
	
	/**
	 * Get the SQL datatype of the column
	 * @return the SQL datatype of the column
	 */
	String getType();
	
	
}
