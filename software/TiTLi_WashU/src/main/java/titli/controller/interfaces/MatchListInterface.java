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

import java.util.List;



/**
 * A list of matches for the given search string
 * @author Juber Patel
 *
 */
public interface MatchListInterface extends List<MatchInterface> 
{
	/**
	 * Get the number of matches in the matchlist
	 * @return the number of matches in the matchlist
	 */
	 int getNumberOfMatches();
	 
	 
	 /**
	  * Get the time taken for searching the query string and all the matches
	  * @return time taken in seconds
	  */
	 double getTimeTaken();
	 
	 /**
	  * get the sorted result map for the given table name
	  * @return the sorted result map for the given table name
	  */
	 SortedResultMapInterface getSortedResultMap();

}
