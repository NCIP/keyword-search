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
	
	public Column(String name, String type)
	{
		this.name = new String(name);
		this.type = new String(type);
	}
		
	public String getName()
	{
		return name;
		
	}
	
	public String getType()
	{
		return type;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
