/**
 * TiTLi business model
 */
package titli.model;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import titli.controller.interfaces.ColumnInterface;
import titli.controller.interfaces.TableInterface;


/**
 * Represents table metadata like table name, columns etc.
 * @author Juber Patel 
 *
 */
public class Table implements TableInterface
{
	private String name;
	private String databaseName;
	private ArrayList<String> uniqueKey;
	private Map<String, Column> columns;
	
	
	/**
	 * 
	 * @param name name of the table
	 * @param databaseName the name of the database which the table belongs to
	 * @param uniqueKey unique key set for the table
	 * @param columns the list of columns that are to be part of this table
	 */
	Table(String name, String databaseName, List<String> uniqueKey,   Map<String, Column> columns)
	{
		this.name = name;
		this.databaseName = databaseName;
		this.uniqueKey = new ArrayList<String> (uniqueKey);
		this.columns =  columns;
		
		
	}
	
	
	/**
	 * returns the name of the table
	 * @return the name of the table
	 */
	public String getName()
	{
		return name;
		
	}
	
	
	/**
	 * returns the name of the table
	 * @return the name of the table
	 */
	public String getDatabaseName()
	{
		return databaseName;
		
	}
	
	
	/**
	 * 
	 * @return the unique key set for the table
	 */
	public List<String> getUniqueKey()
	{
		return Collections.unmodifiableList(uniqueKey); 
	}
	
	/**
	 * Get the number of columns in the table
	 * @return the number of columns in the table
	 */
	public int getNumberOfColumns()
	{
		return columns.size();
	}
	
	
	
	
	/**
	 * Get the column specified name 
	 * @param name the name of the column
	 * @return the corresponding column
	 */
	public Column getColumn(String name)
	{
		return columns.get(name);
	}
	
	
	/**
	 * Get a map of  "column name" => "column"
	 * @return a map of  "column name => "column"
	 */
	public Map<String, ColumnInterface> getColumns()
	{
		return Collections.unmodifiableMap(new HashMap<String, ColumnInterface>(columns));
		
	}
	
	
	
	
	/**
	 * @param args to main
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
