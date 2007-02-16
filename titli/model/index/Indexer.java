/**
 * 
 */
package titli.model.index;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import titli.model.Database;
import titli.model.RDBMSReader;
import titli.model.Table;




/**
 * @author Juber Patel
 *
 */
public class Indexer
{
	private RDBMSReader reader;
	private File indexDir;
	private Statement indexstmt;
	 
	 
	 
	public Indexer(RDBMSReader dbReader) 
	{
		reader = dbReader;
		
		//create the database index directory
		File parent = new File(System.getProperty("titli.index.location"));
		indexDir = new File(parent, reader.getDatabase().getName()+"_index");
		
		try
		{
			indexstmt = reader.getIndexConnection().createStatement();
		}
		catch(SQLException e)
		{
			
			System.out.println("SQL Exception happend"+e);
			e.printStackTrace();
		}
		
	}
	
	/**
	 *  
	 * @return the corresponding database
	 */
	public Database getDatabase()
	{
		return reader.getDatabase();
	}
	
	/**
	 * index from the scratch
	 *
	 */
	public void index()
	{
		indexDir.mkdirs();
		
		Database database = reader.getDatabase();
		
		long start = new Date().getTime();
		
		for(int i=0; i<database.noOfTables;i++)
		{
			Table table = database.getTable(i);
			
			System.out.println("Indexing "+table.getName()+"...");
			
			indexTable(table);
			
		}
				
		long end = new Date().getTime();
		
		//System.out.println("Congrats ! indexing completed successfully !");
		System.out.println("\nIndexing database "+database.getName()+" took "+(end-start)/1000.0+" seconds");
			
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
	 * @param table the table to be indexed
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
			
			System.out.println("executing :   "+"SELECT * FROM  "+table.getName()+";");
			
			ResultSet rs = indexstmt.executeQuery("SELECT * FROM  "+table.getName()+";");
			
			/*
			if(rs.isClosed())
			{
				System.out.println("rs is closed !!");
				System.exit(0);
			}*/
			while(rs.next())
			{
				//RDBMSRecordParser parser = new RDBMSRecordParser(rs);
				//String content = parser.getParse(new Content()).getText();
				
				indexWriter.addDocument(makeDocument(rs, table));
				
			}
			
			long end = new Date().getTime();	
			System.out.println("Completed in "+(end-start)/1000.0+" seconds\n");
			
			indexWriter.optimize();
			indexWriter.close();
			indexDir.close();
			
			
		}
		catch(IOException e)
		{
			System.out.println("IOException happened"+e);
			e.printStackTrace();
		}
		catch(SQLException e)
		{
			System.out.println("SQLException happened");
			e.printStackTrace();
		}
			
	}
	
	
	/**
	 * make document to be indexed
	 * 
	 * @param rs the corresponding resultset
	 * @param table the table of the corresponding record
	 * @return a Document for the record that can be added to the index
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
		
			doc.add(new Field("database", reader.getDatabase().getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
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
			System.out.println("SQLException happened");
			e.printStackTrace();
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
