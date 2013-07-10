/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/keyword-search/LICENSE.txt for details.
 */

package titli.controller.interfaces.record;

import java.util.List;

import titli.controller.Name;

/**
 * a list of records
 * @author Juber Patel
 *
 */
public interface RecordListInterface extends List<RecordInterface>
{
	/**
	 * @return the tableName
	 */
	Name getTableName();
	

	/**
	 * @return the timeTaken to retrieve these records
	 */
	double getTimeTaken(); 
	


}
