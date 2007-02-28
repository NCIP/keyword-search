/**
 *this package is the top level package 
 */
package titli.model;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import titli.controller.interfaces.DatabaseInterface;
import titli.controller.interfaces.MatchInterface;
import titli.controller.interfaces.MatchListInterface;
import titli.controller.interfaces.TitliInterface;
import titli.controller.interfaces.record.RecordInterface;
import titli.model.fetch.Fetcher;
import titli.model.index.Indexer;
import titli.model.search.MatchList;
import titli.model.search.Searcher;




/**
 * The main runner class. It's a singleton
 * @author Juber Patel
 *
 */
public final class Titli implements TitliInterface
{
	private static Titli instance;
	
	private List<Database> databases;
	private Map<String ,Indexer> indexers;
	private Map<String, Fetcher> fetchers;
	
	/**
	 * private constructor for singleton behavaiour
	 * 
	 */
	private Titli() 
	{
		String propertiesUrl = System.getProperty("titli.properties.location");
		
		Properties props = new Properties();
		
		try
		{
			FileInputStream in = new FileInputStream(propertiesUrl);
			props.load(in);
		}
		catch(IOException e)
		{
			System.out.println("IOException in Titli constructor");
			e.printStackTrace();
		}
			
		//set system properties
		System.setProperty("jdbc.drivers", props.getProperty("jdbc.drivers"));
		System.setProperty("titli.index.location", props.getProperty("titli.index.location"));
		
		//initialize the class fields 
		databases = new ArrayList<Database>();
		indexers = new HashMap<String, Indexer> ();
		fetchers = new HashMap<String, Fetcher> ();
		
		//read database names
		Scanner s =new Scanner(props.getProperty("jdbc.databases"));
		s.useDelimiter("\\s*,\\s*");
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
			databases.add(reader.getDatabase());
			
		}
		
		
		
	}
	
	/**
	 * 
	 * @return return the only instance of Titli
	 */
	public static Titli getInstance()
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
	public List<DatabaseInterface> getDatabases()
	{
		return Collections.unmodifiableList(new ArrayList<DatabaseInterface>(databases));
	}

	/**
	 * start the indexing threads
	 * one thread per database
	 *
	 */
	public void index()
	{
		//index all databases
		for(Database db : databases)
		{
			String dbName = db.getName();
			
			//System.out.println("Creating indexer for "+dbName);
			Indexer indexer = indexers.get(dbName);
			indexer.index();
		}
		
	}
	
	/**
	 * 
	 * @param query the search string for which the search is to be performed
	 * @return the list of matches found
	 */
	public MatchList search(String query)
	{
			Searcher searcher = new Searcher(databases, fetchers);
			
			MatchList matches =searcher.search(query);
			searcher.close();
			
			return matches;
	}
	
		
	/**
	 * create an RDBMSReader for given name and properties
	 * @param dbName name of the database
	 * @param props the properties related to the reader
	 * @return the newly created RDBMSReader
	 */
	private RDBMSReader createRDBMSReader(String dbName, Properties props)
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
	 * @param args args for main
	 * 
	 */
	public static void main(String[] args)
	{
		//set the system property that will be read by the Tilti constructor 
		System.setProperty("titli.properties.location", "E:/juber/workspace/TiTLi/titli/model/titli.properties");
		
		Titli titli = Titli.getInstance();
		
		//titli.index();
		
		MatchListInterface  matchList =titli.search("new +bombay");
		
		for(MatchInterface match : matchList)
		{
			RecordInterface record = match.fetch();
			
			System.out.println(record);
		}
		
		
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
