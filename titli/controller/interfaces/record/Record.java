/**
 * 
 */
package titli.controller.interfaces.record;



import java.util.Map;

import titli.model.Table;

/**
 * A record from a single table
 * A Record is fetched when fetch is requested on a match     
 * @author Juber Patel
 *
 */
public interface Record 
{
	/**
	 * Get the corresponding table
	 * @return the table for the record
	 */
	Table getTable();
	
	/**
	 * The "Column Name" => "Column Value" Map 
	 * @return a map that contains column names and column values of the record as key-value pairs
	 */
	Map<String,String> getColumnMap();
	
	
	
}
