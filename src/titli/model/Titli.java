/**
 *this package is the top level package 
 */
package titli.model;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import titli.controller.interfaces.DatabaseInterface;
import titli.controller.interfaces.IndexRefresherInterface;
import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.ResultGroupInterface;
import titli.controller.interfaces.TitliInterface;
import titli.model.fetch.Fetcher;
import titli.model.fetch.TitliFetchException;
import titli.model.index.IndexRefresher;
import titli.model.index.Indexer;
import titli.model.index.TitliIndexException;
import titli.model.search.MatchList;
import titli.model.search.Searcher;
import titli.model.search.TitliSearchException;


/**
 * The main runner class. It's a singleton
 * @author Juber Patel
 *
 */
public final class Titli implements TitliInterface
{
	/**
	 * the only instance of this class
	 */
	private static Titli instance;
	
	/**
	 * a map of "database name" => "database object"
	 */
	private Map<String, Database> databases;
	
	/**
	 * a map of "database name" => "indexer object" 
	 */
	private Map<String ,Indexer> indexers;
	
	/**
	 * a map of "database name" => "fetcher object"
	 */
	private Map<String, Fetcher> fetchers;
	
	
	/**
	 * the index refresher used to refresh indexes
	 */
	private IndexRefresherInterface indexRefresher;
	
	
	/**
	 * private constructor for singleton behavaiour
	 * @throws TitliException if problems occur  
	 */
	private Titli() throws TitliException
	{
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(TitliConstants.PROPERTIES_FILE);
		Properties props = new Properties();
		
		try
		{
			props.load(in);
			in.close();
		}
		catch(IOException e)
		{
			throw new TitliException("TITLI_S_001", "I/O problem while opening titli.properties", e);
		}
		
		initSystemProperties(props);
		
		initResources(props);
		
		//set the column references
		//setReferences("world41", "E:/juber/workspace/TiTLi/titli/model/world_joins");
		//setReferences("sakila", "E:/juber/workspace/TiTLi/titli/model/sakila_joins");
		
	}
	
	
	/**
	 * 
	 * @return the only instance of Titli
	 * @throws TitliException if problems occur
	 */
	public static TitliInterface getInstance() throws TitliException
	{
		
		if(instance==null)
		{
			instance = new Titli();
		}
		
		return instance;
	}
	
	
	/**
	 * Get the number of databases 
	 * @return the number of databases
	 */
	public int getNumberOfDatabases()
	{
		return databases.size();
	}
	
	
	/**
	 * 
	 * @return the corresponding database
	 */
	public Map<String, DatabaseInterface> getDatabases()
	{
		return new LinkedHashMap<String, DatabaseInterface>(databases);
	}

	
	/**
	 * Get the database specified byname 
	 * @param dbName the name of the database
	 * @return the database
	 */
	public DatabaseInterface getDatabase(String dbName)
	{
		return databases.get(dbName);
	}
	
	
	
	/**
	 * index all the databases from the scratch
	 * @throws TitliIndexException if problems occur
	 *
	 */
	public void index() throws TitliIndexException
	{
		//index all databases
		for(String dbName : databases.keySet())
		{
			//Database db = databases.get(dbName);
			
			//System.out.println("Creating indexer for "+dbName);
			Indexer indexer = indexers.get(dbName);
			indexer.index();
		}
		
	}
	
	
	/**
	 * index from the scratch the specified database
	 * @param databaseName the database name
	 * @throws TitliIndexException if problems occur
	 */
	public void index(String databaseName) throws TitliIndexException
	{
		Indexer indexer = indexers.get(databaseName);
		indexer.index();
		
	}
	
	
	/**
	 * index from the scratch the specified table of the specified database
	 * @param databaseName the database name
	 * @param tableName the table name
	 * @throws TitliIndexException if problems occur
	 */
	public void index(String databaseName, String tableName) throws TitliIndexException
	{
		Indexer indexer = indexers.get(databaseName);
		indexer.index(tableName);
		
	}
	
	
	/**
	 * 
	 * @param query the search string for which the search is to be performed
	 * @return the list of matches found
	 * @throws TitliSearchException if problems occur
	 */
	public MatchListInterface search(String query) throws TitliSearchException
	{
			Searcher searcher = new Searcher(databases, fetchers);
			
			MatchList matches =searcher.search(query);
			searcher.close();
			
			return matches;
	}
	
	
	/**
	 *get the index refresher 
	 * @return the index refresher
	 */
	public IndexRefresherInterface getIndexRefresher()
	{
		return indexRefresher;
	}
	
	
	/**
	 * initialise System properties that should be avilable everywhere
	 * @param props the properties
	 */
	private void initSystemProperties(Properties props)
	{
		//set system properties
		System.setProperty(TitliConstants.JDBC_DRIVERS, props.getProperty(TitliConstants.JDBC_DRIVERS));
		System.setProperty(TitliConstants.TITLI_INDEX_LOCATION, props.getProperty(TitliConstants.TITLI_INDEX_LOCATION));
		
	}
	
	
	/**
	 * initialise the maps, lists etc. that will be used for titli
	 * @param props the properties
	 * @throws TitliException if problems occur
	 */
	private void initResources(Properties props) throws TitliException
	{

		//initialize the class fields 
		databases = new LinkedHashMap<String, Database>();
		indexers = new LinkedHashMap<String, Indexer> ();
		fetchers = new LinkedHashMap<String, Fetcher> ();
		indexRefresher = new IndexRefresher(indexers);
		
		//read database names
		Scanner s =new Scanner(props.getProperty(TitliConstants.JDBC_DATABASES));
		s.useDelimiter(TitliConstants.PROPERTIES_FILE_DELIMITER_PATTERN);
		while(s.hasNext())
		{
			String dbName =s.next();
			
			//make db reader
			RDBMSReader reader = createRDBMSReader(dbName, props);
			
			//create Indexer for the reader
			indexers.put(dbName, new Indexer(reader));
			
			//create Fetcher for the reader
			fetchers.put(dbName, new Fetcher(reader));
			
			//add DatabaseInterface to the list
			databases.put(dbName, reader.getDatabase());
		}
		
	}

	
	/**
	 * create an RDBMSReader for given name and properties
	 * @param dbName name of the database
	 * @param props the properties related to the reader
	 * @return the newly created RDBMSReader
	 * @throws TitliException if problems occur
	 */
	private RDBMSReader createRDBMSReader(String dbName, Properties props) throws TitliException
	{
		Properties dbProps = new Properties();
		
		String propName = "jdbc.database";
		dbProps.setProperty(propName, dbName);
				
		propName = "jdbc."+dbName+".url";
		dbProps.setProperty(propName, props.getProperty(propName));
		
		propName = "jdbc."+dbName+".username";
		dbProps.setProperty(propName, props.getProperty(propName));
		
		propName = "jdbc."+dbName+".password";
		dbProps.setProperty(propName, props.getProperty(propName));
		
		propName = "titli."+dbName+".noindex.prefix";
		dbProps.setProperty(propName, props.getProperty(propName));
		
		propName = "titli."+dbName+".noindex.table";
		dbProps.setProperty(propName, props.getProperty(propName));
		
		return new RDBMSReader(dbProps);
		
		
	}
	
	
	/**
	 * 
	 * @param dbName the name of the database
	 * @param location the location of joins file
	 * @throws TitliException if problems occur
	 *
	 */
	private void setReferences(String dbName, String location) throws TitliException
	{
		Database db = (Database)getDatabase(dbName);
		
		Scanner scanner=null;
		
		try
		{
			scanner = new Scanner(new File(location));
		}
		catch (FileNotFoundException e)
		{
			throw new TitliException("UNDEFINED", "the joins file not found : "+location, e);
		}
		
		while(scanner.hasNext())
		{
			String first = scanner.next();
			String second = scanner.next();
			
			int dot = first.indexOf(".");
			
			String firstTable = first.substring(0,dot);
			String firstColumn = first.substring(dot+1);
			
			
			dot = second.indexOf(".");
			
			String secondTable = second.substring(0,dot);
			String secondColumn = second.substring(dot+1);
			
			Column c1 = (Column)db.getTable(firstTable).getColumn(firstColumn);
			
			c1.setReferredColumn((Column)db.getTable(secondTable).getColumn(secondColumn));
			
			
					
		}
		
	}
	
	
	
	
	
	/**
	 * 
	 * @param args args for main
	 * 
	 */
	public static void main(String[] args)
	{
		TitliInterface titli=null;
		try
		{
			titli = Titli.getInstance();
		}
		catch(TitliException e)
		{
			System.out.println(e+"\n"+e.getCause());
		}
		
		
		
		long start = new Date().getTime();
		
		
		/*
		try 
		{
			titli.index("catissuecore41");
		}
		catch (TitliIndexException e) 
		{
			System.out.println(e+"\n"+e.getCause());
		}
		*/
		
		long end = new Date().getTime();
		
		
		System.out.println("Indexing took "+(end-start)/1000.0+" seconds");
		
		
		MatchListInterface  matchList=null;
		try 
		{
			
			matchList =titli.search("p*");
		}
		catch (TitliSearchException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  //AND (table:(+countrylanguage))");
		
		//MatchListInterface  matchList =titli.search("kalmykia");
		
		
		
		start = new Date().getTime();
		
		//MatchListInterface  matchList =titli.search("+united +states ");
		
		end = new Date().getTime();
		
		for(Map.Entry<String, ResultGroupInterface> e : matchList.getSortedResultMap().entrySet())
		{
			//if(e.getKey().equals("catissue_participant"))
			//{	
				try
				{
					System.out.println(e.getValue().fetch());
				}
				catch (TitliFetchException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			//}
			
			
		}
		
		System.out.println("\n\nMatches : "+matchList.size()+"   Time : "+matchList.getTimeTaken()+" seconds   Time :  "+(end-start)/1000.0);
		
		
		//Fetcher.fetch(titli.search("Temple"),titli.dbReaders);
		//Fetcher.fetch(titli.search("ajay"),titli.dbReaders);
		//Fetcher.fetch(titli.search("pari~"),titli.dbReaders);
		
		//MatchList matchList = titli.search("ajay");
		//titli.fetch(matchList);
		
		//querying a remote databse : cab2b on Vishvesh's machine
		//MatchList matchList = titli.search("1298_1150_1372");
		//titli.fetch(matchList);
		
		//MatchList matchList = titli.search("tilburg");
		//titli.fetch(matchList);
		
	}
	

}
