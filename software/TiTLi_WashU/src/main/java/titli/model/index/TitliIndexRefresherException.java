/*L
 * Copyright Washington University in St. Louis, SemanticBits, Persistent Systems, Krishagni.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/keyword-search/LICENSE.txt for details.
 */

/**
 * 
 */
package titli.model.index;

import titli.model.TitliException;

/**
 * @author Juber Patel
 *
 */
public class TitliIndexRefresherException extends TitliException 
{

	/**
	 * default constructor
	 */
	public TitliIndexRefresherException() 
	{
		super();
	}

	/**
	 * @param message the message
	 */
	public TitliIndexRefresherException(String message) 
	{
		super(message);
		
	}

	/**
	 * @param errorCode the error code
	 * @param message the message
	 * @param cause the cause
	 */
	public TitliIndexRefresherException(String errorCode, String message, Throwable cause) 
	{
		super(errorCode, message, cause);
		
	}

}
