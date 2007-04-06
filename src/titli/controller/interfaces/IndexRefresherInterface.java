package titli.controller.interfaces;

import titli.controller.RecordIdentifier;
import titli.model.index.TitliIndexRefresherException;

public interface IndexRefresherInterface 
{

	/**
	 * insert the record identified by parameters into the index
	 * 
	 * @param identifier the record identifier
	 * @throws TitliIndexRefresherException if problems occur
	 */
	void insert(RecordIdentifier identifier)
			throws TitliIndexRefresherException;
	

	/**
	 * update the record identified by parameters in the the index 
	 * 
	 * @param identifier the record identifier
	 * @throws TitliIndexRefresherException if problems occur
	 */
	void update(RecordIdentifier identifier) throws TitliIndexRefresherException;

	/**
	 * delete the record identified by parameters from the index 
	 * @param identifier the record identifier
	 * @throws TitliIndexRefresherException if problems occur
	 */
	void delete(RecordIdentifier identifier)
			throws TitliIndexRefresherException;

}