/**
 * 
 */
package titli.controller.interfaces;

/**
 * A Document in the index that matched the given search string
 * A Match only deals with the index and not with the databases  
 * @author juber Patel
 *
 */
public interface Match
{
	/**
	 * Get the table in which the matched record belongs
	 * @return the table
	 */	
	String getTableName();
	
	/**
	 * Get the database in which the matched record belongs
	 * @return the database
	 */	
	String getDatabaseName();
	
	
	
}
