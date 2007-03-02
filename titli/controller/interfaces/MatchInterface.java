/**
 * 
 */
package titli.controller.interfaces;

import titli.controller.interfaces.record.RecordInterface;

/**
 * A Document in the index that matched the given search string
 * A MatchInterface only deals with the index and not with the databases  
 * @author juber Patel
 *
 */
public interface MatchInterface
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
	
	/**
	 * 
	 * @return the string representaion of the match
	 */
	String toString();
	
	/**
	 * Fetch the record corresponding to the match
	 * @return the record corresponding to the match
	 */
	RecordInterface fetch();

	
	
}
