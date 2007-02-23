/**
 * 
 */
package titli.controller.interfaces;



/**
 * A list of matches for the given search string
 * @author Juber Patel
 *
 */
public interface MatchList 
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

}
