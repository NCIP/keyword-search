/**
 * 
 */
package titli.model.search;

import java.util.ArrayList;

import titli.controller.interfaces.MatchInterface;
import titli.controller.interfaces.MatchListInterface;


/**
 * @author Juber Patel
 *
 */
public class MatchList extends ArrayList<MatchInterface> implements MatchListInterface
{

	double time;
	
	/**
	 * the constructor
	 * @param time time taken to perform the search and get the matchlist
	 */
	public MatchList(double time)
	{
		super();
		
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
		return time;
	}
	 
}
