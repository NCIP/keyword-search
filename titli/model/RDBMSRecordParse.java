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
	
	/**
	 * 
	 * @param record the record to be parsed
	 */
	public RDBMSRecordParse(String record)
	{
		this.record = record;
		
	}

	/* (non-Javadoc)
	 * @see org.apache.nutch.parse.Parse#getData()
	 */
	/**
	 * @return the metadata for the content
	 */
	public ParseData getData() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.nutch.parse.Parse#getText()
	 */
	/**
	 * @return the text for the content
	 */
	public String getText() 
	{
		
		return record;
		
	}

}
