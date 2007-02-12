/**
 * 
 */
package titli.model.search;

import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;

import titli.Titli;
import titli.model.*;
import titli.model.Match;
import titli.model.MatchList;

/**
 * @author juberahamad_patel
 *
 */
public class Searcher
{
	private MultiSearcher ms;
	private List<IndexSearcher> searcherList;
	
	public Searcher()
	{
		int numDatabases = Titli.getInstance().noOfDatabases;
		
		//create the multisearcher
		
		ArrayList<IndexSearcher> searcherList = new ArrayList<IndexSearcher> (); 
		
		//for each database
		for(int i=0;i<numDatabases;i++)
		{
			Database db = Titli.getInstance().getDatabase(i);
			
			//for each table
			for(int j=0;j<db.noOfTables;j++)
			{
				Table t = db.getTable(j);
				
				RAMDirectory ramDir = new RAMDirectory(new File(databaseIndexDir, tableList.get(i)+"_index"));
			}
			
		}
		
		IndexSearcher[] searchers = new IndexSearcher[numTables];
		
		for (int i=0; i<numTables; i++)
		{
			
			
			searchers[i] = new IndexSearcher(ramDir);
			
		}
		
		MultiSearcher ms = new MultiSearcher(searchers);
		
		
	}
	
	
	/**
	 * search for the given query using the index
	 * @param searchString the user query to be searched 
	 * @return a list of matches
	 * @throws IOException
	 * @throws SQLException
	 * @throws org.apache.lucene.queryParser.ParseException
	 */
	public MatchList search(String searchString) throws IOException, SQLException, org.apache.lucene.queryParser.ParseException
	{
		
		MatchList matchList = new MatchList();
		
		System.out.println("Searching for " +searchString+"...");
		Analyzer analyzer = new StandardAnalyzer();
		
		
		QueryParser qp = new QueryParser("content", analyzer);
		
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
		
		//close all the index searchers : NOT to be called before you are done with Hits etc.
		for (int i=0; i<numTables; i++)
		{
			searchers[i].close();
			
		}
		
		
		return matchList;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
