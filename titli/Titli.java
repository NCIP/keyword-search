/**
 *this package is the top level package 
 */
package titli;

import java.util.*;
import java.io.*;

import titli.model.*;
import titli.model.index.*;
import titli.model.search.*;
import titli.model.fetch.*;


/**
 * The main runner class. It's a singleton
 * @author Juber Patel
 *
 */
public class Titli 
{
	private static Titli instance=null;
	
	public final List<Database> databases;
	public  final int noOfDatabases; 
	private Map<String ,Indexer> indexers;
	private Map<String, Fetcher> fetchers;
	
	
	public Titli(String propertiesUrl) throws FileNotFoundException, IOException
	{
		Properties props = new Properties();
		
		FileInputStream in = new FileInputStream(propertiesUrl);
		props.load(in);
		
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
			
			//add Database to the list
			databases.add(reader.getDatabase());
			
		}
		
		//set remaining class fields
		noOfDatabases = databases.size();
		instance = this;
		
	}
	
	public static Titli getInstance()
	{
		/*
		(instance==null)
		{
			instance = new Titli();
		}*/
		
		return instance;
	}
	
	
	/**
	 * 
	 * @param i the number of the database
	 * @return the corresponding database
	 */
	public Database getDatabase(int i)
	{
		return databases.get(i);
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
			
			System.out.println("Creating indexer for "+dbName);
			Indexer indexer = indexers.get(dbName);
			indexer.index();
		}
		
	}
	
	public MatchList search(String query)
	{
			Searcher searcher = new Searcher(databases);
			
			MatchList matches =searcher.search(query);
			searcher.close();
			
			return matches;
	}
	
	
	public void fetch(MatchList matchList)
	{
		long start = new Date().getTime();
		
		for(Match match : matchList)
		{
			Fetcher fetcher = fetchers.get(match.getDatabaseName());
			
			fetcher.fetch(match);
		}
		
		long end = new Date().getTime();
		System.out.print("\nFetch took "+(end-start)/1000.0+" seconds");
	}
		
	
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
	 * @param args
	 */
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		Titli titli = new Titli("E:/juber/workspace/TiTLi/titli/model/titli.properties");
		
		//titli.index();
		
		//MatchList matchList =titli.search("new +bombay");
		//titli.fetch(matchList);
		
		//Fetcher.fetch(titli.search("Temple"),titli.dbReaders);
		//Fetcher.fetch(titli.search("ajay"),titli.dbReaders);
		//Fetcher.fetch(titli.search("pari~"),titli.dbReaders);
		
		MatchList matchList = titli.search("ajay");
		titli.fetch(matchList);
	}

}
