/**
 * 
 */
package titli.model;



/**
 * Represents column metadata for a column 
 * @author Juber Patel
 *
 */
public class Column
{
	private String name;
	private String type;
	
	/**
	 * 
	 * @param name name of the column
	 * @param type SQL datatype of the column
	 */
	public Column(String name, String type)
	{
		this.name = new String(name);
		this.type = new String(type);
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
	 * 
	 * @return the SQL datatype of the column
	 */
	public String getType()
	{
		return type;
	}
	
	

	/**
	 * @param args argument to main
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
