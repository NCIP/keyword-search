/**
 * 
 */
package titli.model.fetch;

import java.util.ArrayList;
import java.util.List;

import titli.controller.interfaces.record.RecordInterface;
import titli.controller.interfaces.record.RecordListInterface;

/**
 * @author juberahamad_patel
 *
 */
public class RecordList extends ArrayList<RecordInterface> implements RecordListInterface
{
	String tableName;
	double timeTaken;
	
	/**
	 * 
	 * @param tableName the table name
	 * @param records the list of records
	 * @param timeTaken time taken to fetch the records
	 */
	public RecordList(String tableName, List<Record> records, double timeTaken)
	{
		super(records);
		
		this.tableName = tableName;
		this.timeTaken = timeTaken;
		
	}
	
	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}


	/**
	 * @return the timeTaken
	 */
	public double getTimeTaken() 
	{
		return timeTaken;
	}

	
	
}
