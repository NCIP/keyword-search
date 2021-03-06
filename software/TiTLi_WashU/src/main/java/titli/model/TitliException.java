/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/keyword-search/LICENSE.txt for details.
 */

/**
 * 
 */
package titli.model;

/**
 * @author Juber Patel
 *
 */
public class TitliException extends Exception 
{
	private String errorCode;

	/**
	 * default constructor
	 */
	public TitliException() 
	{
		super();
	}

	/**
	 * @param message the message
	 */
	public TitliException(String message)
	{
		super(message);
		
	}

	
	/**
	 * @param errorCode the error code
	 * @param message the message
	 * @param cause the cause
	 */
	public TitliException(String errorCode, String message, Throwable cause) 
	{
		super(message, cause);
		
		this.errorCode = errorCode;
	
	}
	
	
	/**
	 * get the error code for this exception
	 * @return the error code
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

}
