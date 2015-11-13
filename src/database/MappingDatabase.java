package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main_package.*;

public class MappingDatabase 
{
	//-------------------------------------------------Constants---------------------------------------------------------------
	private static String DATABASE_NAME = "campusMapping.db";
	
	private static String MAP_TABLE_NAME = "Maps";
	private static String POINT_TABLE_NAME = "Points";
	private static String EDGE_TABLE_NAME = "Edges";
	
	private static String MAP_SCHEMA = "id integer, String name, int numPoints";
	private static String POINT_SCHEMA = "id integer, name String, x integer, y integer, numEdges integer, idEdge1 integer,"
			+ " idEdge2 integer, idEdge3 integer, idEdge4 integer, idEdge5 integer, idEdge6 integer, idEdge7 integer, idEdge8 integer,"
			+ "idEdge9 integer, idEdge10 integer";
	private static String EDGE_SCHEMA = "id integer, idPoint1 integer, idPoint2 integer, weight integer, isOutside integer, isStairs integer";
	
	//----------------------------------------------Global Variables-----------------------------------------------------------
	private static Connection connection;
	public final static boolean DEBUG = true;
	
	ArrayList<Map> allMaps;
	ArrayList<Point> allPoints;
	ArrayList<Edge> allEdges;
	
	//--------------------------------------------Function Definitions---------------------------------------------------------
	public static void main(String args[]) throws ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");													//Look into exactly what this is intended to do
		connection = null;																	//Initialize connection
	}
	public static void initDatabase()
	{
		try
		{							
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);			//Create a database connection
			
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.

			System.out.println("Dropping old tables");
			statement.executeUpdate("drop table if exists "+MAP_TABLE_NAME);
			statement.executeUpdate("drop table if exists "+POINT_TABLE_NAME);
			statement.executeUpdate("drop table if exists "+EDGE_TABLE_NAME);
			System.out.println("Creating new tables");
			statement.executeUpdate("create table "+ MAP_TABLE_NAME +" ("+ MAP_SCHEMA + ")");
			if (DEBUG)
				System.out.println("Constructed Table: "+MAP_TABLE_NAME);
			statement.executeUpdate("create table "+ POINT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");
			if (DEBUG)
				System.out.println("Constructed Table: "+POINT_TABLE_NAME);
			statement.executeUpdate("create table "+ EDGE_TABLE_NAME +" ("+ EDGE_SCHEMA + ")");
			if (DEBUG)
				System.out.println("Constructed Table: "+EDGE_TABLE_NAME);
			System.out.println("Finished creating tables");
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
	public static void insertPoint(Point pt)
	{
		int counter = 0;
		int pointID = pt.getId();
		String ptName = pt.getName();
		int ptX = pt.getX();
		int ptY = pt.getY();
		ArrayList<Edge> edgeArray = pt.getEdges();
		int numberEdges = pt.getNumberEdges();
		
		//TODO: Check exists
		if (DEBUG)
			System.out.println("Beginning to construct insert statement");
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.
			
			String insertStatement = "insert into points values(";
			insertStatement += pointID;
			insertStatement += ", ";
			insertStatement += ("'"+ptName+"'");
			insertStatement += ", ";
			insertStatement += ptX;
			insertStatement += ", ";
			insertStatement += ptY;
			insertStatement += ", ";
			insertStatement += numberEdges;
			insertStatement += ", ";
			if (DEBUG)
			{
				System.out.println("About to add edge id's to insert statement");
			}
			for (counter =0; counter < numberEdges; counter++)
			{
				insertStatement += edgeArray.get(counter).getID();
				insertStatement += ", ";
			}
			for (counter = numberEdges; counter < 9; counter++)
			{
				insertStatement += "null";
				insertStatement += ", ";
			}
			insertStatement += "null";										//Formatting, last var doesn't have comma after
			insertStatement += ")";
			if (DEBUG)
				System.out.println ("Insert statement: "+insertStatement);
			statement.executeUpdate(insertStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}			
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

	public boolean checkExists (Map map, Point pt)
	{
		//map.
		int ptID = pt.getId();
		return false; 
	}
	public static void testInsert()
	{
		Edge[] emptyArray = null;
		Point testPoint = new Point(1432, "testPoint", 23, 56, 0);
		insertPoint(testPoint);
		
	}
}	
