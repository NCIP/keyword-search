/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/keyword-search/LICENSE.txt for details.
 */

/**
 * 
 */
package titli.controller.interfaces;

import titli.controller.Name;

/**
 *  Represents a column of a table
 * @author Juber Patel
 *
 */
public interface ColumnInterface 
{

	/**
	 * Get the name of the column
	 * @return name of the column
	 */
	Name getName();
	
	
	/**
	 * Get the name of the table in which the column belongs
	 * @return name of the table
	 */
	Name getTableName();
	
	
	/**
	 * Get the SQL datatype of the column
	 * @return the SQL datatype of the column
	 */
	String getType();
	
	
}
