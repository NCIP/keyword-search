/**
 * 
 */
package titli.model.search;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import org.apache.lucene.queryParser.ParseException;

import titli.model.*;

/**
 * @author juberahamad_patel
 *
 */
public class Searcher
{
	private ArrayList<IndexSearcher> searcherList;
	private MultiSearcher ms;
	
	//setup a multiseracher for the given databases
	public Searcher(List<Database> databases)
	{
		try
		{
			//the current directory
			File indexDir = new File(System.getProperty("titli.index.location"));
			
			//create the multisearcher
			searcherList = new ArrayList<IndexSearcher> (); 
			
			//for each database
			for(Database db : databases)
			{
				File dbDir = new File(indexDir, db.getName()+"_index");
				
				//for each table
				for(int i=0;i<db.noOfTables;i++)
				{
					Table table = db.getTable(i);
					
					File tableDir = new File(dbDir, table.getName()+"_index");
									
					RAMDirectory ramDir = new RAMDirectory(tableDir);
					searcherList.add(new IndexSearcher(ramDir));
				}
				
			}
			
			IndexSearcher[] searchers = searcherList.toArray(new IndexSearcher[0] );		
			
			ms = new MultiSearcher(searchers);
		}
		catch(IOException e)
		{
			System.out.println("IOException happened"+ e);
		}
		
			
		
	}
	
	
	/**
	 * search for the given query using the index
	 * @param searchString the user query to be searched 
	 * @return a list of matches
	 * @throws IOException
	 * @throws SQLException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */
	public MatchList search(String searchString) 
	{
		
		MatchList matchList = new MatchList();
		
		System.out.println("Searching for " +searchString+"...");
		Analyzer analyzer = new StandardAnalyzer();
		
		
		QueryParser qp = new QueryParser("content", analyzer);
		
		try
		{
			Query query = qp.parse(searchString);
		
			long start = new Date().getTime();
			
			//search for the query
			Hits hits = ms.search(query);
			
			long end = new Date().getTime();
			
			int listLength = hits.length();
			
			
			//build the match list	
			for(int i=0;i<listLength;i++)
			{
				matchList.add(new Match(hits.doc(i)));
						
				//matchList.add(new Match(hits.doc(i).get("ID"),hits.doc(i).get("TableName"),"Not Known"));
				//System.out.println(hits.doc(i).get("Population")+"  "+hits.doc(i).get("CountryCode"));
				
			}
			
			System.out.println("\n The search took " + (end-start)/1000.0 + " seconds");
			System.out.println("\n Found "+listLength+" matches");
			
			System.out.println("\nThe matches are : ");
			for(Match match : matchList)
			{
				System.out.println(match);
				System.out.println(match.getQuerystring()+"\n");
			}
			
			System.out.println("\n The search took " + (end-start)/1000.0 + " seconds");
			
		}
		catch(ParseException e)
		{
			System.out.println("ParseException happened"+e);
		}
		catch(IOException e)
		{
			System.out.println("IOException happened"+e);
		}
		
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
		}
		
			
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
