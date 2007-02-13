/**
 * 
 */
package titli.model;

/**
 * pareses the resultset to get relevant information
 * @author juberahamad_patel
 *
 */

import java.sql.*;

import org.apache.nutch.parse.*;
import org.apache.nutch.protocol.*;

import org.apache.hadoop.conf.*;

public class RDBMSRecordParser implements Parser
{
	//use StringBuilder since it is modified a lot
	//no synchronization
	private StringBuilder record;
	
	/**
	 * 
	 * @param rs the resultset to be parsed
	 * @throws SQLException
	 */
	public RDBMSRecordParser(ResultSet rs) throws SQLException
	{
		
				
	}
	
	
	//ignore Content for the time being
	/**
	 * @param content ignore for the time being
	 */
	public Parse getParse(Content content)
	{
		
		return new RDBMSRecordParse(new String(record));
	}
	
	public Configuration getConf()
	{
		
		
		return null;
	}
	
	public void setConf(Configuration conf)
	{
		
		
		
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
