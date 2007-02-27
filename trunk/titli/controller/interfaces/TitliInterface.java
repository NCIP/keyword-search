/**
 * 
 */
package titli.controller.interfaces;

import java.util.List;


/**
 * The main interface. This interface is used to initialize Titli, index databases and fire searches 
 * @author Juber Patel
 *
 */
public interface TitliInterface
{
	
	/**
	 * Get the number of databases 
	 * @return the number of databases
	 */
	int getNumberOfDatabases();
	
	
	/**
	 * 
	 * @return the list of databases
	 */
	List<DatabaseInterface> getDatabases();
	
	
	
	
	/****************            index methods    *********************/  
	
	/**
	 * index all the databases from the scratch
	 *
	 */
	void index();
	
	/**
	 * index from the scratch the specified database
	 * @param databaseName the database name
	 */
	//void index(String databaseName);
	
	
	/**
	 * index from the scratch the specified table of the specified database
	 * @param databaseName the database name
	 * @param tableName the table name
	 */
	//void index(String databaseName, String tableName);
	
	
	
	/**
	 * refresh the indexes of all the databases (ie modify the index if there is any change in the databases)
	 *
	 */
	//void refreshIndex();
	
	
	/**
	 * refresh the index of the specified database
	 * @param databaseName the database name 
	 */
	//void refreshIndex(String databaseName);
	
	
	/**
	 * refresh the index of the specified table of the specified database
	 * @param databaseName the database name
	 * @param tableName the table name
	 */
	//void refreshIndex(String databaseName, String tableName);
	
	
	
	
	
	
	
	
	/****************            search methods     *********************/
	
	
	/**
	 *serach the specified query 
	 * @param query the search string for which the search is to be performed
	 * @return the list of matches found
	 */
	MatchListInterface search(String query);
	
	
}
