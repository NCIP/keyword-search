/**
 * 
 */
package titli.model.index;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import titli.controller.RecordIdentifier;
import titli.controller.interfaces.IndexRefresherInterface;
import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.ResultGroupInterface;
import titli.controller.interfaces.SortedResultMapInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.Titli;
import titli.model.TitliException;
import titli.model.fetch.TitliFetchException;
import titli.model.search.ResultGroup;
import titli.model.search.TitliSearchException;
import titli.model.util.IndexUtility;

/**
 * @author Juber Patel
 *
 */
public class IndexRefresher implements IndexRefresherInterface 
{
	
	private Map<String, Indexer> indexers;
	
	/**
	 * default constructor
	 * @param indexers the map of indexers
	 * @throws TitliException if problems occur
	 *
	 */
	public IndexRefresher(Map<String, Indexer> indexers) throws TitliException
	{
		this.indexers = indexers;
	}
	
	
	/**
	 * insert the record identified by parameters into the index
	 * the unique key for the table
	 * @param identifier the record identifier
	 * @throws TitliIndexRefresherException if problems occur
	 */
	public void insert(RecordIdentifier identifier) throws TitliIndexRefresherException
	{
		//check if document already exists in the index
		if(isIndexed(identifier))
		{
			return;
		}
		
		Indexer indexer = indexers.get(identifier.getDbName());
		try 
		{
			indexer.index(identifier.getTableName(), identifier.getUniqueKey());
		}
		catch (TitliIndexException e) 
		{
			throw new TitliIndexRefresherException("TITLI_S_030", "problem while indexing record for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
		}
		
	}
	
	/**
	 * update the record identified by parameters from in index
	 * the unique key for the table
	 * @param identifier the record identifier
	 * @throws TitliIndexRefresherException if problems occur
	 */
	public void update(RecordIdentifier identifier) throws TitliIndexRefresherException
	{
		delete(identifier);
		insert(identifier);
		
	}
	
	
	/**
	 * delete the record identified by parameters from the index 
	 * the unique key for the table
	 * @param identifier the record identifier
	 * @throws TitliIndexRefresherException if problems occur
	 */
	public void delete(RecordIdentifier identifier) throws TitliIndexRefresherException
	{
		File indexDir = IndexUtility.getIndexDirectoryForTable(identifier.getDbName(), identifier.getTableName());
		IndexReader reader;
		
		try 
		{
			reader = IndexReader.open(indexDir);
		}
		catch (IOException e) 
		{
			throw new TitliIndexRefresherException("TITLI_S_030", "problem while creating index reader for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
		}
		
		int maxDoc = reader.maxDoc();
		Document doc=null;
		
		int i;
		
		//find the doc with given columns and values
		for(i=0; i<maxDoc; i++)
		{
			try 
			{
				//ignore documents marked deleted
				if(reader.isDeleted(i))
				{
					continue;	
				}
				
				doc = reader.document(i);
			}
			catch (IOException e) 
			{
				throw new TitliIndexRefresherException("TITLI_S_030", "problem reading document from the index reader for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
			}
			
			//this is not the doc we are looking for
			if(identifier.matches(doc))
			{
				break;
			}
						
		}
		
		//delete the document
		try
		{
			if(i<maxDoc)
			{
				reader.deleteDocument(i);
			}
			
			reader.close();
		}
		catch (IOException e) 
		{
			throw new TitliIndexRefresherException("TITLI_S_030", "problem while deleting document from the index reader for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
		}
		
	}
	
	
	/**
	 * check if a record with given unique key values already in the index  
	 * @param identifier the record identifier
	 * @return true if this record is already indexed otherwise false
	 * @throws TitliIndexRefresherException if problems occur
	 */
	public boolean isIndexed(RecordIdentifier identifier) throws TitliIndexRefresherException
	{
		boolean isIndexed=false;
		File indexDir = IndexUtility.getIndexDirectoryForTable(identifier.getDbName(), identifier.getTableName());
		IndexReader reader;
		
		try 
		{
			FSDirectory dir = FSDirectory.getDirectory(indexDir, false);
			reader = IndexReader.open(dir);
		}
		catch (IOException e) 
		{
			throw new TitliIndexRefresherException("TITLI_S_030", "problem while creating index reader for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
		}
		
		int maxDoc = reader.maxDoc();
		Document doc=null;
		int i;
		
		//find the doc with given columns and values
		for(i=0; i<maxDoc; i++)
		{
			try 
			{
				//ignore documents marked deleted
				if(reader.isDeleted(i))
				{
					continue;	
				}
								
				doc = reader.document(i);
			}
			catch (IOException e) 
			{
				throw new TitliIndexRefresherException("TITLI_S_030", "problem reading document from the index reader for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
			}
			
			//this is not the doc we are looking for
			if(identifier.matches(doc))
			{
				isIndexed=true;
				break;
			}
			
		}
		
		try 
		{
			reader.close();
		}
		catch (IOException e) 
		{
			throw new TitliIndexRefresherException("TITLI_S_030", "problem closing reader for database  :"+identifier.getDbName()+" table : "+identifier.getTableName(), e);
		}
		
		return isIndexed;
		
		
	}
	
	
	/**
	 * insert the list of records identified the list of Record identifiers 
	 * the unique key for the table
	 * @param identifiers the list record identifiers
	 * @throws TitliIndexRefresherException if problems occur
	 */
	public void insert(List<RecordIdentifier> identifiers) throws TitliIndexRefresherException
	{
		//check if document already exists in the index
		removeIndexed(identifiers);
		
		for(RecordIdentifier identifier : identifiers)
		{
			insert(identifier);
		}
		
	}
	
	
	/**
	 * remove from the list the record identifiers whose records are already indexed
	 * @param identifiers the list of record identifiers
	 */
	public void removeIndexed(List<RecordIdentifier> identifiers)
	{
		
	}
	

	/**
	 * 
	 * @param args args for main
	 */
	public static void main(String[] args)
	{
		TitliInterface titli = null;
		IndexRefresherInterface refresher;
		
		try
		{
			titli = Titli.getInstance();
			refresher = titli.getIndexRefresher();
			
			LinkedHashMap<String, String> uniqueKey = new LinkedHashMap<String, String>();
			uniqueKey.put("IDENTIFIER", "2");
			
			RecordIdentifier identifier = new RecordIdentifier("catissuecore41", "catissue_cancer_research_group", uniqueKey);
			
			long start = new Date().getTime();
			
			refresher.delete(identifier);
			
			long end = new Date().getTime();
			
			System.out.println("refresh time : "+(end-start)/1000.0);
		}
		catch (TitliException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MatchListInterface matchList = null;
		try 
		{
			matchList = titli.search("p*");
		}
		catch (TitliSearchException e) 
		{
			System.out.println(e+"\n"+e.getCause());
		}  
		
		SortedResultMapInterface map = matchList.getSortedResultMap();
		
		for(ResultGroupInterface groupInterface : map.values())
		{
			ResultGroup group = (ResultGroup)groupInterface;
			
			try 
			{
				System.out.println(group.fetch());
			}
			catch (TitliFetchException e) 
			{
				System.out.println(e);
			}
			
			
		}

	}
	
	
}
