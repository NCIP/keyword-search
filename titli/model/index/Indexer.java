/**
 * 
 */
package titli.model.index;


import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.nutch.protocol.Content;

import titli.model.*;

/**
 * @author Juber Patel
 *
 */
public class Indexer
{
	private Connection conn;
	private Database database;
	private File indexDir;
	 
	 
	 
	public Indexer(RDBMSReader dbReader) 
	{
		this.conn = dbReader.getIndexConnection();
		this.database = dbReader.getDatabase(); 
				
		//create the database index directory
		File parent = new File(System.getProperty("titli.index.location"));
		indexDir = new File(parent, database.getName()+"_index");
		
	}
	
	
	/**
	 * index from the scratch
	 *
	 */
	public void index()
	{
		indexDir.mkdirs();
		
		long start = new Date().getTime();
		
		for(int i=0; i<database.noOfTables;i++)
		{
			Table table = database.getTable(i);
			
			System.out.println("Indexing "+table.getName()+"...");
			
			indexTable(table);
			
		}
				
		long end = new Date().getTime();
		
		//System.out.println("Congrats ! indexing completed successfully !");
		System.out.println("\nIndexing database "+database+" took "+(end-start)/1000.0+" seconds");
			
	}
	
	/**
	 * refresh the index ie reflect the changes in the database
	 *
	 */
	public void refresh()
	{
		
	}
		
	/**
	 * index the given table
	 * @param tableName the table to be indexed
	 * 
	 */
	private void indexTable(Table table) 
	{
			
		long start = new Date().getTime();
		
		File tableIndexDir = new File(indexDir,table.getName()+"_index");
		
		try
		{
			//RAMDirectory does not have a method to flush to the hard disk ! this is  bad !
			//RAMDirectory indexDir = new RAMDirectory(tableIndexDir);
			Directory indexDir = FSDirectory.getDirectory(tableIndexDir,true);
			
			//	specify the index directory
			IndexWriter indexWriter = new IndexWriter(indexDir, new StandardAnalyzer(), true);
			
			PreparedStatement indexstmt = conn.prepareStatement("SELECT * FROM  "+table.getName()+";");
			
			System.out.println("executing :   "+indexstmt);
			ResultSet rs = indexstmt.executeQuery();
			
			while(rs.next())
			{
				//RDBMSRecordParser parser = new RDBMSRecordParser(rs);
				//String content = parser.getParse(new Content()).getText();
				
				indexWriter.addDocument(makeDocument(rs, table));
				
			}
			
			long end = new Date().getTime();	
			System.out.println("\nCompleted in "+(end-start)/1000.0+" seconds");
			
			indexWriter.optimize();
			indexWriter.close();
			indexDir.close();
			indexstmt.close();
			
		}
		catch(IOException e)
		{
			
		}
		catch(SQLException e)
		{
			
		}
			
	}
	
	
	/**
	 * make document to be indexed
	 * @param content the content to be indexed 
	 * @param rs the corresponding resultset
	 * @param tableName the name of the table
	 * @return a Document that can be added to the index
	 * @throws SQLException
	 */
	private Document makeDocument(ResultSet rs, Table table)
	{
		Document doc = new Document();
		
		try
		{
			//convert the record to a String
			
			StringBuilder record= new StringBuilder("");
			
			for(int i=1; i<=table.noOfColumns; i++)
			{
				
				record.append(" ");
				record.append(rs.getString(i));
			
			}
			
			String content = new String(record);
		
			
			doc.add(new Field("table", table.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
			
			List<String> uniqueKey = table.getUniqueKey();
			
			for(String key : uniqueKey)
			{
				//sample addition
				//doc.add(new Field("ID",rs1.getString("ID"),Field.Store.YES,Field.Index.TOKENIZED));
				//doc.add(new Field("TableName","City",Field.Store.YES,Field.Index.TOKENIZED));
				
				String value = rs.getString(key);
			
				doc.add(new Field(key, value, Field.Store.YES, Field.Index.NO ));
			
			}
			
			doc.add(new Field("content", content,Field.Store.NO, Field.Index.TOKENIZED));
			
		}
		catch(SQLException e)
		{
			
		}
		
		return doc;
			
	}
	
	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
