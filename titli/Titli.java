/**
 * 
 */
package titli;

import java.util.*;
import java.util.logging.*;

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
	
	public final ArrayList<Database> databases;
	public  final int noOfDatabases; 
	private Map<String,RDBMSReader> dbReaders;
	
	private Titli(String propertiesUrl) throws FileNotFoundException, IOException
	{
		Properties props = new Properties();
		
		FileInputStream in = new FileInputStream(propertiesUrl);
		props.load(in);
		
		//set system properties
		System.setProperty("jdbc.drivers", props.getProperty("jdbc.drivers"));
		System.setProperty("titli.index.location", props.getProperty("titli.index.location"));
		
		//initialize the class fields 
		dbReaders = new HashMap<String, RDBMSReader>();
		databases = new ArrayList<Database>();
		
		//read database names
		Scanner s =new Scanner(props.getProperty("jdbc.databases"));
		s.useDelimiter("\\s*,\\s*");
		while(s.hasNext())
		{
			String dbName =s.next();
			
			//make db readers
			RDBMSReader reader = getRDBMSReader(dbName, props);
			
			//add the reader to the map
			dbReaders.put(dbName, reader);
			
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
		if(instance==null)
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
		for(String dbName : dbReaders.keySet())
		{
			System.out.println("Creating indexer for "+dbName);
			Indexer indexer = new Indexer(dbReaders.get(dbName));
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
		
	
	private RDBMSReader getRDBMSReader(String dbName, Properties props)
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
		Titli titli = new Titli("E:/juber/workspace/TiTLi/titli/model/database.properties");
		
		//titli.index();
		Fetcher.fetch(titli.search("york"),titli.dbReaders);

	}

}
