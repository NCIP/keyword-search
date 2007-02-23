/**
 * 
 */
package titli.model.search;

import java.util.ArrayList;

import titli.controller.interfaces.Match;
import titli.controller.interfaces.MatchList;
import titli.controller.interfaces.record.Record;

/**
 * @author Juber Patel
 *
 */
public class RecordMatchList extends ArrayList<RecordMatch> implements MatchList
{

	/**
	 * the constructor
	 */
	public RecordMatchList()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param match the match for which to fetch the record
	 * @return the record for the match
	 */
	public Record fetch(Match match)
	{
		//TO DO add the implementation
		return null;
	}


	/**
	 * Get the number of matches in the matchlist
	 * @return the number of matches in the matchlist
	 */
	public int getNumberOfMatches()
	{
		return size();
	}


	/**
	  * Get the time taken for searching the query string and all the matches
	  * @return time taken in seconds
	  */
	 public double getTimeTaken()
	 {
		// TODO Auto-generated method stub
		return 0;
	}
}
