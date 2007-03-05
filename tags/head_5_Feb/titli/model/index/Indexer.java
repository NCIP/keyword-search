/**
 * 
 */
package titli.model.index;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import titli.controller.interfaces.ColumnInterface;
import titli.controller.interfaces.TableInterface;
import titli.model.Column;
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
	 
	 
	/**
	 * 
	 * @param dbReader The RDBMSReader on which to build the Indexer
	 */ 
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
		
		//long start = new Date().getTime();
		
		//get the map of tables
		Map<String, TableInterface> tables = database.getTables();
		
		//iterate on the map 
		for(String tableName : tables.keySet())
		{
			Table table = database.getTable(tableName);
			
			//System.out.println("Indexing "+table.getName()+"...");
			
			indexTable(table);
			
		}
				
		//long end = new Date().getTime();
		
		//System.out.println("Congrats ! indexing completed successfully !");
		//System.out.println("\nIndexing database "+database.getName()+" took "+(end-start)/1000.0+" seconds");
			
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
			
		//long start = new Date().getTime();
		
		File tableIndexDir = new File(indexDir,table.getName()+"_index");
		
		try
		{
			//RAMDirectory does not have a method to flush to the hard disk ! this is  bad !
			//RAMDirectory indexDir = new RAMDirectory(tableIndexDir);
			Directory dir = FSDirectory.getDirectory(tableIndexDir,true);
			
			//	specify the index directory
			IndexWriter indexWriter = new IndexWriter(dir, new StandardAnalyzer(), true);
			
			//System.out.println("executing :   "+"SELECT * FROM  "+table.getName()+";");
			
			String query = getExtendedQuery(table);
			
			ResultSet rs = indexstmt.executeQuery(query);
			
			while(rs.next())
			{
				//this is for compatibility with Nutch Parsers
				//RDBMSRecordParser parser = new RDBMSRecordParser(rs);
				//String content = parser.getParse(new Content()).getText();
				
				indexWriter.addDocument(makeDocument(rs, table));
				
			}
			
			//long end = new Date().getTime();	
			//System.out.println("Completed in "+(end-start)/1000.0+" seconds\n");
			
			indexWriter.optimize();
			indexWriter.close();
			dir.close();
			
			
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
	 * 
	 */
	private Document makeDocument(ResultSet rs, Table table)
	{
		Document doc = new Document();
		
		try
		{
			//convert the record to a String
			
			StringBuilder record= new StringBuilder("");
			
			int numberOfColumns = rs.getMetaData().getColumnCount();
			
			for(int i=1; i<=numberOfColumns; i++)
			{
				
				record.append(" ");
				record.append(rs.getString(i));
			
			}
			
			String content = new String(record);
			
			//System.out.println("Indexing : "+content);
		
			doc.add(new Field("database", reader.getDatabase().getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
			doc.add(new Field("table", table.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
			
			List<String> uniqueKey = table.getUniqueKey();
			
			for(String key : uniqueKey)
			{
				//sample addition
				//doc.add(new Field("ID",rs1.getString("ID"),Field.Store.YES,Field.Index.TOKENIZED));
				//doc.add(new Field("TableName","City",Field.Store.YES,Field.Index.TOKENIZED));
				
				String value = rs.getString(key);
			
				doc.add(new Field(key, value, Field.Store.YES, Field.Index.NO));
			
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
	 * Get the query that will return a resultset consisting of all the fields of the table as well as of the tables refrenced through joins with this table
	 * @param table the table for which to produce the extended query
	 * @return the query that will return a resultset consisting of all the fields of the table as well as of the tables refrenced through joins with this table 
	 */
	private String getExtendedQuery(Table table)
	{
		StringBuilder fromClause = new StringBuilder(" FROM "+table.getName()+", ");
		StringBuilder whereClause = new StringBuilder(" WHERE ");
		
		int whereLength = whereClause.length();
		
		Map<String, ColumnInterface> columns = table.getColumns();
		
		for(String columnName : columns.keySet())
		{
			Column column = table.getColumn(columnName);
			
			Column column2 = column.getReferredColumn();
			
			//column refers to another column
			if(column2!=null)
			{
				String anotherTable = column2.getTableName();
				String anotherColumn = column2.getName();
	
				fromClause.append(anotherTable+", ");
				whereClause.append(table.getName()+"."+column.getName()+"="+anotherTable+"."+anotherColumn+" AND ");
			}
		}
		
		//remove the last ","
		fromClause.delete(fromClause.lastIndexOf(","), fromClause.lastIndexOf(",")+1);
		
		//remove the last "AND"
		int i= whereClause.lastIndexOf("AND");
		if(i!=-1)
		{
			whereClause.delete(i, i+3);
		}
		
		String query;
		
		//Don't add whereClause  if nothing is appended to it
		if(whereClause.length()==whereLength)
		{
			query = "SELECT * "+fromClause;
		}
		else
		{	
			query = "SELECT * "+fromClause+whereClause;
		}
		
		
		System.out.println("Extended Query : "+query);
		
		return query;
		
	}


	
	

	
	
	
	
	
	
	
	/**
	 * @param args args for main
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
