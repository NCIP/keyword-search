/**
 * TiTLi business model
 */
package titli.model;



import java.util.ArrayList;
import java.util.List;


/**
 * Represents table metadata like table name, columns etc.
 * @author Juber Patel 
 *
 */
public class Table
{
	private String name;
	private String databaseName;
	private ArrayList<String> uniqueKey;
	private ArrayList<Column> columns;
	public final int noOfColumns;
	
	/**
	 * 
	 * @param name name of the table
	 * @param databaseName the name of the database which the table belongs to
	 * @param uniqueKey unique key set for the table
	 * @param columns the list of columns that are to be part of this table
	 */
	Table(String name, String databaseName, List<String> uniqueKey,   List<Column> columns)
	{
		this.name = name;
		this.databaseName = databaseName;
		this.uniqueKey = new ArrayList<String> (uniqueKey);
		this.columns =  new ArrayList<Column> (columns);
		noOfColumns = columns.size();
		
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
		return new ArrayList<String>(uniqueKey); 
	}
	
	/**
	 * 
	 * @param i the number of the column
	 * @return the corresponding column
	 */
	public Column getColumn(int i)
	{
		return columns.get(i);
	}
	
	
	/**
	 * @param args to main
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
