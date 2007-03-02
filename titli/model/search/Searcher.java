/**
 * 
 */
package titli.model.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;

import titli.controller.interfaces.TableInterface;
import titli.model.Database;
import titli.model.Table;
import titli.model.fetch.Fetcher;


/**
 * @author Juber Patel
 *
 */
public class Searcher
{
	private ArrayList<IndexSearcher> searcherList;
	private MultiSearcher ms;
	private Map<String, Fetcher> fetchers;
	
	
	/**
	 * setup a multiseracher for the given databases
	 * @param databases the list of databases to be searched 
	 * @param fetchers a map of fetchers so that appropriate Fetcher is attached to each match
	 */
	public Searcher(Map<String, Database> databases, Map<String, Fetcher> fetchers)
	{
		this.fetchers = fetchers;
		
		try
		{
			//the current directory
			File indexDir = new File(System.getProperty("titli.index.location"));
			
			//create the multisearcher
			searcherList = new ArrayList<IndexSearcher> (); 
			
			//for each database
			for(String dbName : databases.keySet())
			{
				Database db = databases.get(dbName);
				
				File dbDir = new File(indexDir, db.getName()+"_index");
				
				Map<String, TableInterface> tables = db.getTables();
				
				//for each table
				for(String tableName :  tables.keySet())
				{
					Table table = db.getTable(tableName);
					
					File tableDir = new File(dbDir, table.getName()+"_index");
									
					RAMDirectory ramDir = new RAMDirectory(tableDir);
					searcherList.add(new IndexSearcher(ramDir));
				}
				
			}
			
			IndexSearcher[] searchers = searcherList.toArray(new IndexSearcher[0]);		
			
			ms = new MultiSearcher(searchers);
		}
		catch(IOException e)
		{
			System.out.println("IOException happened"+ e);
			e.printStackTrace();
		}
			
	}
	
	
	/**
	 * search for the given query using the index
	 * @param searchString the user query to be searched 
	 * @return a list of matches
	 * 
	 */
	public MatchList search(String searchString) 
	{
		
		//System.out.println("Searching for " +searchString+"...");
		Analyzer analyzer = new StandardAnalyzer();
				
		QueryParser qp = new QueryParser("content", analyzer);
		Query query=null;
		
		try
		{
			query = qp.parse(searchString);
		}
		catch(ParseException e)
		{
			System.out.println("ParseException happened"+e);
			e.printStackTrace();
		}
		
		
		long start = new Date().getTime();
		
		Hits hits = null;
		
		try
		{
			//search for the query
			hits = ms.search(query);
		}
		catch(IOException e)
		{
			System.out.println("IOException happened"+e);
			e.printStackTrace();
			
		}
			
		long end = new Date().getTime();
		
		double time = (end-start)/1000.0;
		
		MatchList matchList = new MatchList(time);
		
		int listLength = hits.length();
		
		Fetcher fetcher=null;
		
		//build the match list	
		for(int i=0;i<listLength;i++)
		{
			Document document = null;
			try
			{
				document = hits.doc(i);
			}
			catch(IOException e)
			{
				System.out.println("IOException happened"+e);
				e.printStackTrace();
				
			}
			
			fetcher = fetchers.get(document.get("database"));
			
			matchList.add(new Match(document, fetcher));
					
			//matchList.add(new Match(hits.doc(i).get("ID"),hits.doc(i).get("TableName"),"Not Known"));
			//System.out.println(hits.doc(i).get("Population")+"  "+hits.doc(i).get("CountryCode"));
			
		}
		
		//System.out.println("\n The search took " + (end-start)/1000.0 + " seconds");
		//System.out.println("\n Found "+listLength+" matches");
		
		//System.out.println("\nThe matches are : ");
		
		/*
		for(MatchInterface match : matchList)
		{
			System.out.println(match);
			System.out.println(match.getQuerystring()+"\n");
		}*/
		
		//System.out.println("\n The search took " + (end-start)/1000.0 + " seconds\n");
		
		return matchList;
		
	}

	
	
	/**
	 * close all the searchers
	 *
	 */
	public void close()
	{
		try
		{
			//close all the index searchers : NOT to be called before you are done with Hits etc.
			for (IndexSearcher searcher : searcherList)
			{
				searcher.close();
				
			}
		}
		catch(IOException e)
		{
			System.out.println("IOException happened"+e);
			e.printStackTrace();
		}
		
			
	}
	
}
