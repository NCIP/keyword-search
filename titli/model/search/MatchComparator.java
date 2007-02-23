/**
 * 
 */
package titli.model.search;

/**
 * @author Juber Patel
 *
 */
public class MatchComparator implements java.util.Comparator<RecordMatch>
{
	/**
	 * compares the table names lexicographically
	 * @param a the first match
	 * @param b the second match
	 * @return negative value if a is less than b, positive value if a is greater than b, 0 if they are equal
	 */
	public int compare(RecordMatch a, RecordMatch b)
	{
		
		int tableCompare = a.getTableName().compareTo(b.getTableName());
		
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
