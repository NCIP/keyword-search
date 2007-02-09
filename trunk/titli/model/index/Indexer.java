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

import titli.model.RDBMSRecordParser;

/**
 * @author Juber Patel
 *
 */
public class Indexer
{
	private Connection conn;
	private DatabaseMetaData dbmd;
	
	private ArrayList<String> noIndexPrefix;
	private ArrayList<String> noIndexTable;
	 
	private String database;
	private File databaseIndexDir;
	 
	 
	 
	public Indexer(Connection conn, String database, File indexProperties) 
	{
		this.conn = conn;
		this.database = database;
		
		//load the properties from the file
		Properties props = new Properties();
		try{
		FileInputStream in = new FileInputStream(indexProperties);
		props.load(in);
		in.close();
		
		}
		catch(FileNotFoundException e)
		{
			
		}
		catch(IOException e)
		{
			
		}
		
		System.out.println("TiTLi Properties file read successfully...");
		
		//set the properties
		System.setProperties(props);
		
		//keep track of tables not to be indexed
		noIndexPrefix = new ArrayList<String>();
		noIndexTable = new ArrayList<String>();
		
		//populate lists of tables NOT to be indexed
		Scanner s = new Scanner(props.getProperty("titli.noindex.prefix"));
		s.useDelimiter("\\s*,\\s*");
		while(s.hasNext())
		{
			noIndexPrefix.add(s.next());
		}
		
		s= new Scanner(props.getProperty("titli.noindex.table"));
		s.useDelimiter("\\s*,\\s*");
		while(s.hasNext())
		{
			noIndexTable.add(s.next());
		}
		
		try{
		dbmd = conn.getMetaData();
		}
		catch(SQLException e)
		{
			
		}
		
		//create the database index directory	 
		databaseIndexDir = new File(new File(System.getProperty("titli.index.location")),database+"_index");
		
	}
	
	
public void index()
{
	//for each table in the database
	//index it
		
	databaseIndexDir.mkdirs();
	
	long start = new Date().getTime();
	
	ResultSet tables;
	
	try{
	tables = dbmd.getTables(null, null, null, new String[]{"TABLE"});
	
	while(tables.next())
	{
		String tableName = tables.getString("TABLE_NAME");
		
		if(!toIndex(tableName))
			continue;
		
		System.out.println("Indexing "+tableName+"...");
		
		indexTable(tableName);
	}
	
	}
	catch(SQLException e)
	{
		
	}
	
	
	long end = new Date().getTime();
	
	System.out.println("Congrats ! indexing completed successfully !");
	System.out.println("\nIndexing database "+database+" took "+(end-start)/1000.0+" seconds");
		
	
		
}
	
/**
 * index the given table
 * @param tableName the table to be indexed
 * @throws IOException
 * @throws SQLException
 */
private void indexTable(String tableName) 
{
	try{
		
	long start = new Date().getTime();
	
	File tableIndexDir = new File(databaseIndexDir,tableName+"_index");
	
	//RAMDirectory does not have a method to flush to the hard disk ! this is  bad !
	//RAMDirectory indexDir = new RAMDirectory(tableIndexDir);
	Directory indexDir = FSDirectory.getDirectory(tableIndexDir,true);
	
	//	specify the index directory
	IndexWriter indexWriter = new IndexWriter(indexDir, new StandardAnalyzer(), true);
	
	PreparedStatement indexstmt = conn.prepareStatement("SELECT * FROM  "+tableName+";");
	
	System.out.println("executing :   "+indexstmt);
	ResultSet rs = indexstmt.executeQuery();
	
	while(rs.next())
	{
		RDBMSRecordParser parser = new RDBMSRecordParser(rs);
		
		String content = parser.getParse(new Content()).getText();
		indexWriter.addDocument(makeDocument(content, rs, tableName));
		
			
	}
	
	indexWriter.optimize();
	indexWriter.close();
	indexDir.close();
	
	long end = new Date().getTime();	
	
	System.out.println("\nCompleted in "+(end-start)/1000.0+" seconds");
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
private Document makeDocument(String content, ResultSet rs, String tableName) throws SQLException
{
	Document doc = new Document();
	
	//get the set of primary keys
	ResultSet pk = dbmd.getBestRowIdentifier(null, null, tableName, DatabaseMetaData.bestRowSession, false);
	
	doc.add(new Field("table", tableName, Field.Store.YES, Field.Index.UN_TOKENIZED));
	
	while(pk.next())
	{
		//sample addition
		//doc.add(new Field("ID",rs1.getString("ID"),Field.Store.YES,Field.Index.TOKENIZED));
		//doc.add(new Field("TableName","City",Field.Store.YES,Field.Index.TOKENIZED));
		
		String columnName = pk.getString("COLUMN_NAME");
		String columnValue = rs.getString(columnName);
		
		doc.add(new Field(columnName, columnValue, Field.Store.YES, Field.Index.NO ));
	}
	
	pk.close();
	
	
	doc.add(new Field("content", content,Field.Store.NO, Field.Index.TOKENIZED));
	
	
	return doc;
	
}


private boolean toIndex(String tableName)
{
	if(noIndexTable.contains(tableName))
	{
		System.out.println("\nMatched table name : skipping "+tableName+"...");
		return false;
	}
		
	for(String prefix : noIndexPrefix)
	{
		if(tableName.startsWith(prefix))
		{
			System.out.println("\nMatched prefix : skipping "+tableName+"...");
			return false;
		}
	}
	
	return true;
}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
