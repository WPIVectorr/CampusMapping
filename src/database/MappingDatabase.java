package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MappingDatabase 
{
	//-------------------------------------------------Constants---------------------------------------------------------------
	private static String DATABASE_NAME = "campusMapping.db";
	
	private static String MAP_TABLE_NAME = "Maps";
	private static String POINT_TABLE_NAME = "Points";
	private static String EDGE_TABLE_NAME = "Edges";
	
	private static String MAP_SCHEMA = "id integer";
	private static String POINT_SCHEMA = "id integer, xCoord integer, yCoord integer, name String";
	private static String EDGE_SCHEMA = "id integer";
	
	//----------------------------------------------Global Variables-----------------------------------------------------------
	private static Connection connection;
	
	//--------------------------------------------Function Definitions---------------------------------------------------------
	public static void main(String args[]) throws ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");													//Look into exactly what this is intended to do
		connection = null;																	//Initialize connection
		try
		{							
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);			//Create a database connection
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.

			initDatabase();
			System.out.println ("Inserting into table");
			statement.executeUpdate("insert into points values(1, 23, 45, 'SL105')");
			statement.executeUpdate("insert into points values(2, 46, 98, 'AK206')");
			statement.executeUpdate("insert into points values(3, 90, 12, 'FL120')");
			statement.executeUpdate("insert into points values(4, 16, 25, 'KV121')");
			printDatabase(true, true, true);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												//If the error message is "out of memory", 
																							//it probably means no database file is found
		}
		finally
		{
			try
			{
			 if(connection != null)
			   connection.close();
			}
			catch(SQLException e)
			{
			 // connection close failed.
			 System.err.println(e);
			}
		}
	}
	public static void initDatabase()
	{
		try
		{							
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);			//Create a database connection
			
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.

			System.out.println("Creating tables");
			statement.executeUpdate("drop table if exists "+MAP_TABLE_NAME);
			statement.executeUpdate("drop table if exists "+POINT_TABLE_NAME);
			statement.executeUpdate("drop table if exists "+EDGE_TABLE_NAME);
			statement.executeUpdate("create table "+ MAP_TABLE_NAME +" ("+ MAP_SCHEMA + ")");
			statement.executeUpdate("create table "+ POINT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");
			statement.executeUpdate("create table "+ EDGE_TABLE_NAME +" ("+ EDGE_SCHEMA + ")");
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												//If the error message is "out of memory", 
		}
	}
	public void insertMap()
	{
		//Get all information for new map
		//Check if map exists
		//If map does not exist, and id is unique, insert as map with unique id
	}
	public void insertPoint()
	{
		
	}
	public void insertEdge()
	{
		
	}
	public static void printDatabase(boolean printMaps, boolean printPoints, boolean printEdges)
	{
		try
		{
			Statement statement = connection.createStatement();
			if (printMaps)
			{
				ResultSet rs = statement.executeQuery("select * from "+MAP_TABLE_NAME);
				System.out.println("Printing "+MAP_TABLE_NAME+" from "+DATABASE_NAME);
				while(rs.next())														// read the result set
				{
					System.out.println("");
				}
			}
			if (printPoints)
			{
				ResultSet rs = statement.executeQuery("select * from "+POINT_TABLE_NAME);
				System.out.println("Printing "+POINT_TABLE_NAME+" from "+DATABASE_NAME);
				while(rs.next())														// read the result set
				{
					System.out.println("name = " + rs.getString("name"));
					System.out.println("id = " + rs.getInt("id"));
					System.out.print("xCoord = " + rs.getInt("xCoord"));
					System.out.println(", yCoord = " + rs.getInt("yCoord"));
					System.out.println();
				}
			}
			if (printEdges)
			{
				ResultSet rs = statement.executeQuery("select * from "+EDGE_TABLE_NAME);
				System.out.println("Printing "+EDGE_TABLE_NAME+" from "+DATABASE_NAME);
				while(rs.next())														// read the result set
				{
					System.out.println();
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());										// if the error message is "out of memory", 
																					// it probably means no database file is found
		}
	}
	
	public static void testInsert()
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);			//Create a database connection
			
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.

			System.out.println ("Inserting into table");
			statement.executeUpdate("insert into points values(1, 23, 45, 'SL105')");
			statement.executeUpdate("insert into points values(2, 46, 98, 'AK206')");
			statement.executeUpdate("insert into points values(3, 90, 12, 'FL120')");
			statement.executeUpdate("insert into points values(4, 16, 25, 'KV121')");
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												//If the error message is "out of memory", 
		}
	}
}	
