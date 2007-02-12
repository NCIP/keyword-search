/**
 * 
 */
package titli;

import java.util.*;
import java.io.*;

import titli.model.*;


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
			
			//add Database
			databases.add(reader.getDatabase());
			//add the reader to the map
			dbReaders.put(dbName, reader);
			
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
	 * @return the populated list of metadata of available databases;
	 */
	private ArrayList<Database> getDatabases()
	{
		//TO DO write the code
		return null;
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
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
