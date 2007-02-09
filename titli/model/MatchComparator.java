/**
 * 
 */
package titli.model;

import java.util.*;



/**
 * @author Juber Patel
 *
 */
public class MatchComparator implements java.util.Comparator<Match>
{
	public int compare(Match a, Match b)
	{
		
		int tableCompare = a.getTable().compareTo(b.getTable());
		
		return tableCompare;
	
	}

	/**
	 * 
	 */
	public MatchComparator()
	{
		// TODO Auto-generated constructor stub
	}

	/*
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}*/

}
