/**
 * 
 */
package titli.model;

import titli.controller.interfaces.ColumnInterface;



/**
 * Represents column metadata for a column 
 * @author Juber Patel
 *
 */
public class Column implements ColumnInterface
{
	private String name;
	private String type;
	private String tableName;
	
	private Column referredColumn;
	
	
	/**
	 * 
	 * @param name name of the column
	 * @param type SQL datatype of the column
	 * @param tableName the name of the table in which the column belongs
	 */
	Column(String name, String type, String tableName)
	{
		this.name = new String(name);
		this.type = new String(type);
		this.tableName = tableName;
	}
	
	/**
	 * 
	 * @return name of the coloumn
	 */
	public String getName()
	{
		return name;
		
	}
	
	/**
	 * Get the name of the table in which the column belongs
	 * @return name of the table
	 * 
	 */
	public String getTableName()
	{
		return tableName;
	}
	
	
	/**
	 * 
	 * @return the SQL datatype of the column
	 */
	public String getType()
	{
		return type;
	}
	
	
	/**
	 * Set the column referred by this column
	 * @param column the column to which this column refers to 
	 */
	public void setReferredColumn(Column column)
	{
		referredColumn = column;
	}
	
	
	/**
	 * Get the column referred by this column
	 * @return the column this column refers to 
	 */
	public Column getReferredColumn()
	{
		return referredColumn;
	}
	
	
	
	
	
	
	
	
	
	

	/**
	 * @param args argument to main
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
