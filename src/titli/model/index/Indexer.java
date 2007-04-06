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
import titli.model.TitliConstants;
import titli.model.TitliException;
import titli.model.util.IndexUtility;




/**
 * @author Juber Patel
 *
 */
public class Indexer
{
	private RDBMSReader reader;
	private Statement indexstmt;
	 
	 
	/**
	 * 
	 * @param dbReader The RDBMSReader on which to build the Indexer
	 * @throws TitliIndexException if problems occur
	 */ 
	public Indexer(RDBMSReader dbReader) throws TitliIndexException 
	{
		reader = dbReader;
		
		try
		{
		
			//create the database index directory
			indexstmt = reader.getIndexConnection().createStatement();
		}
		catch(SQLException e)
		{
			throw new TitliIndexException("TITLI_S_004", "problem while trying to get index Statement ", e);
			
		}
	}
	
	/**
	 *  
	 * @return the corresponding database
	 * @throws TitliIndexException if problems occur
	 * 
	 */
	public Database getDatabase() throws TitliIndexException 
	{
		try
		{
			return reader.getDatabase();
		}
		catch(TitliException e)
		{
			throw new TitliIndexException("TITLI_S_006", "Problem in getting Database object", e);
		}
		
	}
	
	
	/**
	 * index from the scratch
	 * @throws TitliIndexException if problems occur
	 *
	 */
	public void index() throws TitliIndexException
	{
		File indexDir;
		try
		{
			indexDir = IndexUtility.getIndexDirectoryForDatabase(reader.getDatabase().getName());
		}
		catch (TitliException e) 
		{
			throw new TitliIndexException("TITLI_S_32", "can't get database", e); 
		}
		
		indexDir.mkdirs();
		
		Database database;
		try
		{
			database = reader.getDatabase();
		}
		catch(TitliException e)
		{
			throw new TitliIndexException("TITLI_S_007", "Problem in getting Database object", e);
		}
		
		for(TableInterface table : database.getTables().values())
		{
			indexTable((Table)table);
			
		}
		
		//long end = new Date().getTime();
		
		//System.out.println("Congrats ! indexing completed successfully !");
		//System.out.println("\nIndexing database "+database.getName()+" took "+(end-start)/1000.0+" seconds");
			
	}
	
	
	/**
	 * index from the sratch the specified table
	 * @param tableName the table name
	 * @throws TitliIndexException if problems occur
	 */
	public void index(String tableName) throws TitliIndexException
	{
		Database database;
		try
		{
			database = reader.getDatabase();
		}
		catch(TitliException e)
		{
			throw new TitliIndexException("TITLI_S_008", "Problem in getting Database object", e);
		}
		
		Table table = (Table)database.getTable(tableName);
		indexTable(table);
	}

	
	/**
	 * index the record represented by the parameters
	 * @param tableName the table name
	 * @param uniqueKey the map of unique key column => value
	 * @throws TitliIndexException if problems occur
	 */
	public void index(String tableName, Map<String, String>uniqueKey) throws TitliIndexException
	{
		Table table =null;
		
		try
		{
			//get the table
			table = (Table)reader.getDatabase().getTable(tableName);
		} 
		catch (TitliException e) 
		{
			throw new TitliIndexException("TITLI", "problem getting Table "+tableName, e);
		}
		
		//build the SQL query
		StringBuilder query = new StringBuilder("Select * from "+tableName+" where ");
		
		for(String column : uniqueKey.keySet())
		{
			query.append(column+"='"+uniqueKey.get(column)+"' AND ");
			
		}
		
		//remove last AND
		query.delete(query.lastIndexOf("AND"), query.length());
		
		ResultSet rs = null;
		try
		{
			//execute query
			rs = indexstmt.executeQuery(query.toString());
			rs.next();
		}
		catch (SQLException e) 
		{
			throw new TitliIndexException("TITLI", "problem executing SQL query on  "+tableName, e);
		}
		
		//make the document
		Document doc = makeDocument(rs, table);
		IndexWriter indexWriter;
		
		try
		{
			File tableDir = IndexUtility.getIndexDirectoryForTable(reader.getDatabase().getName(), tableName);
			Directory dir = FSDirectory.getDirectory(tableDir,false);
			indexWriter = new IndexWriter(dir, new StandardAnalyzer(), false);
			indexWriter.addDocument(doc);
			indexWriter.close();
			dir.close();
			rs.close();
		}
		catch (TitliException e) 
		{
			throw new TitliIndexException("TITLI", "problem getting database  "+tableName, e);
		}
		catch (IOException e) 
		{
			throw new TitliIndexException("TITLI", "problem creating index writer for  "+tableName, e);
		}
		catch (SQLException e) 
		{
			throw new TitliIndexException("TITLI", "SQL problem", e);
		}
		
	}
	
	
	/**
	 * index the given table
	 * @param table the table to be indexed
	 * @throws TitliIndexException if problems occur
	 * 
	 */
	private void indexTable(Table table) throws TitliIndexException 
	{
			
		//long start = new Date().getTime();
		
		File tableIndexDir = IndexUtility.getIndexDirectoryForTable(table.getDatabaseName(), table.getName());
		String query=null;
		
		try
		{
			//RAMDirectory does not have a method to flush to the hard disk ! this is  bad !
			//RAMDirectory indexDir = new RAMDirectory(tableIndexDir);
			Directory dir = FSDirectory.getDirectory(tableIndexDir,true);
			
			//	specify the index directory
			IndexWriter indexWriter = new IndexWriter(dir, new StandardAnalyzer(), true);
			indexWriter.setMergeFactor(TitliConstants.INDEX_MERGE_FACTOR);
			indexWriter.setMaxBufferedDocs(TitliConstants.INDEX_MAX_BUFFERED_DOCS);
			
			//System.out.println("executing :   "+"SELECT * FROM  "+table.getName()+";");
			
			query = getExtendedQuery(table);
			
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
			
			rs.close();
			
			
		}
		catch(IOException e)
		{
			throw new TitliIndexException("TITLI_S_009", "I/O problem with "+tableIndexDir, e);
		}
		catch(SQLException e)
		{
			throw new TitliIndexException("TITLI_S_010", "SQL problem while executing "+query, e);
		}
			
	}
	
	
				
	/**
	 * make document to be indexed from current record
	 * 
	 * @param rs the corresponding resultset
	 * @param table the table of the corresponding record
	 * @return a Document for the record that can be added to the index
	 * @throws TitliIndexException if problems occur
	 * 
	 */
	private Document makeDocument(ResultSet rs, Table table) throws TitliIndexException
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
		
			doc.add(new Field(TitliConstants.DOCUMENT_DATABASE_FIELD, reader.getDatabase().getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
			doc.add(new Field(TitliConstants.DOCUMENT_TABLE_FIELD, table.getName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
			
			List<String> uniqueKey = table.getUniqueKey();
			
			for(String key : uniqueKey)
			{
				//sample addition
				//doc.add(new Field("ID",rs1.getString("ID"),Field.Store.YES,Field.Index.TOKENIZED));
				//doc.add(new Field("TableName","City",Field.Store.YES,Field.Index.TOKENIZED));
				
				String value = rs.getString(key);
			
				doc.add(new Field(key, value, Field.Store.YES, Field.Index.NO));
			
			}
			
			doc.add(new Field(TitliConstants.DOCUMENT_CONTENT_FIELD, content,Field.Store.NO, Field.Index.TOKENIZED));
			
		}
		catch(SQLException e)
		{
			throw new TitliIndexException("TITLI_S_011", "SQL problem while trying to access a record from the result set", e);
		}
		catch(TitliException e)
		{
			throw new TitliIndexException("TITLI_S_012", "Problem in getting Database object", e);
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
			Column column = (Column)table.getColumn(columnName);
			
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


}
