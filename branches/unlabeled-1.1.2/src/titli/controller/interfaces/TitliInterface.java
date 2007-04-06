/**
 * 
 */
package titli.controller.interfaces;

import java.util.Map;

import titli.model.index.TitliIndexException;
import titli.model.search.TitliSearchException;


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
	 * Get the database specified byname 
	 * @param dbName the name of the database
	 * @return the database
	 */
	DatabaseInterface getDatabase(String dbName);
	
	/**
	 * 
	 * @return the list of databases
	 */
	Map<String, DatabaseInterface> getDatabases();
	
	
	
	
	/****************            index methods    *********************/  
	
	/**
	 * index all the databases from the scratch
	 * @throws TitliIndexException if problems occur
	 *
	 */
	void index() throws TitliIndexException;
	
	/**
	 * index from the scratch the specified database
	 * @param databaseName the database name
	 * @throws TitliIndexException if problems occur
	 */
	void index(String databaseName) throws TitliIndexException;
	
	
	/**
	 * index from the scratch the specified table of the specified database
	 * @param databaseName the database name
	 * @param tableName the table name
	 * @throws TitliIndexException if problems occur
	 */
	void index(String databaseName, String tableName) throws TitliIndexException;
	
	
	
	/**
	 *get the index refresher 
	 * @return the index refresher
	 */
	IndexRefresherInterface getIndexRefresher();
	
	
	
	
	/****************            search methods     *********************/
	
	
	/**
	 *serach the specified query 
	 * @param query the search string for which the search is to be performed
	 * @return the list of matches found
	 * @throws TitliSearchException if problems occur
	 */
	MatchListInterface search(String query) throws TitliSearchException;
	
	
}
