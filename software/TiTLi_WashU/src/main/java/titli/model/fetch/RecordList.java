/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/keyword-search/LICENSE.txt for details.
 */

/**
 * 
 */
package titli.model.fetch;

import java.util.ArrayList;
import java.util.List;

import titli.controller.Name;
import titli.controller.interfaces.record.RecordInterface;
import titli.controller.interfaces.record.RecordListInterface;

/**
 * this class is a list of Record objects with additional useful methods
 * @author Juber Patel
 *
 */
public class RecordList extends ArrayList<RecordInterface> implements RecordListInterface
{
	private Name tableName;
	private double timeTaken;
	
	/**
	 * 
	 * @param tableName the table name
	 * @param records the list of records
	 * @param timeTaken time taken to fetch the records
	 */
	public RecordList(Name tableName, List<Record> records, double timeTaken)
	{
		super(records);
		
		this.tableName = tableName;
		this.timeTaken = timeTaken;
		
	}
	
	/**
	 * @return the tableName
	 */
	public Name getTableName()
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
