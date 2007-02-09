/**
 * 
 */
package titli.model;

import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;

/**
 * @author juberahamad_patel
 *
 */
public class RDBMSRecordParse implements Parse
{
	private String record;
	
	public RDBMSRecordParse(String record)
	{
		this.record = record;
		
	}

	/* (non-Javadoc)
	 * @see org.apache.nutch.parse.Parse#getData()
	 */
	public ParseData getData() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.nutch.parse.Parse#getText()
	 */
	public String getText() 
	{
		
		return record;
		
	}

}
