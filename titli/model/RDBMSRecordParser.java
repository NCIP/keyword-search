/**
 * 
 */
package titli.model;



import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;

/**
 * pareses the resultset to get relevant information
 * @author juberahamad_patel
 *
 */



public class RDBMSRecordParser implements Parser
{
	//use StringBuilder since it is modified a lot
	//no synchronization
	private StringBuilder record;
	
	/**
	 * 
	 * @param rs the resultset
	 * @throws SQLException if problem with database connection
	 */
	public RDBMSRecordParser(ResultSet rs) throws SQLException
	{
		
				
	}
	
	
	//ignore Content for the time being
	
	 
	 
	 
	 /**
	  * @param content the content to be parsed
	  * @return the result of parsing
	  */
	 public Parse getParse(Content content)
	{
		
		return new RDBMSRecordParse(new String(record));
	}
	
	/**
	 * ignore for the time being
	 * @return nothing right now
	 */ 
	public Configuration getConf()
	{
		
		
		return null;
	}
	
	/**
	 * ignore for the time being
	 * @param conf nothing
	 */
	public void setConf(Configuration conf)
	{
		
		
		
	}
	

	/**
	 * @param args argumnets to main
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
