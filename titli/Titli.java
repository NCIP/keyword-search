/**
 *this package is the top level package 
 */
package titli;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import titli.model.Database;
import titli.model.RDBMSReader;
import titli.model.fetch.Fetcher;
import titli.model.index.Indexer;
import titli.model.search.RecordMatch;
import titli.model.search.RecordMatchList;
import titli.model.search.Searcher;




/**
 * The main runner class. It's a singleton
 * @author Juber Patel
 *
 */
public class Titli 
{
	private static Titli instance=null;
	
	private List<Database> databases;
	public  final int noOfDatabases; 
	private Map<String ,Indexer> indexers;
	private Map<String, Fetcher> fetchers;
	
	/**
	 * 
	 * @param propertiesUrl filename with the path where the properties file is stored
	 * @throws IOException for e
	 */
	public Titli(String propertiesUrl) throws IOException
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
			
			System.out.println("Creating reader for "+dbName);
			
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
	
	/**
	 * 
	 * @return return the only instance of Titli
	 */
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
	
	/**
	 * 
	 * @param query the search string for which the search is to be performed
	 * @return the list of matches found
	 */
	public RecordMatchList search(String query)
	{
			Searcher searcher = new Searcher(databases);
			
			RecordMatchList matches =searcher.search(query);
			searcher.close();
			
			return matches;
	}
	
	/**
	 * 
	 * @param matchList the list of matches for which records are to be fetched
	 */
	public void fetch(RecordMatchList matchList)
	{
		long start = new Date().getTime();
		
		for(RecordMatch match : matchList)
		{
			Fetcher fetcher = fetchers.get(match.getDatabaseName());
			
			fetcher.fetch(match);
		}
		
		long end = new Date().getTime();
		System.out.print("\nFetch took "+(end-start)/1000.0+" seconds");
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
	 * @throws IOException for e
	 */
	public static void main(String[] args) throws IOException
	{
		Titli titli = new Titli("E:/juber/workspace/TiTLi/titli/model/titli.properties");
		
		//titli.index();
		
		RecordMatchList matchList =titli.search("new +bombay");
		titli.fetch(matchList);
		
		//Fetcher.fetch(titli.search("Temple"),titli.dbReaders);
		//Fetcher.fetch(titli.search("ajay"),titli.dbReaders);
		//Fetcher.fetch(titli.search("pari~"),titli.dbReaders);
		
		//RecordMatchList matchList = titli.search("ajay");
		//titli.fetch(matchList);
		
		//querying a remote databse : cab2b on Vishvesh's machine
		//RecordMatchList matchList = titli.search("1298_1150_1372");
		//titli.fetch(matchList);
		
		//RecordMatchList matchList = titli.search("tilburg");
		//titli.fetch(matchList);
		
	}
	

}
