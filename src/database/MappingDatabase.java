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
	//-----------------------------------------------------------Constants--------------------------------------------------------------------
	private static String DATABASE_NAME = "campusMapping.db";
	
	private static String MAP_TABLE_NAME = "Maps";
	private static String POINT_TABLE_NAME = "Points";
	private static String EDGE_TABLE_NAME = "Edges";
	
	private static String MAP_SCHEMA = "id integer, name String";
	private static String POINT_SCHEMA = "id integer, name String, x integer, y integer, numEdges integer, idEdge1 integer,"
			+ " idEdge2 integer, idEdge3 integer, idEdge4 integer, idEdge5 integer, idEdge6 integer, idEdge7 integer, idEdge8 integer,"
			+ "idEdge9 integer, idEdge10 integer";
	private static String EDGE_SCHEMA = "id integer, idPoint1 integer, idPoint2 integer, weight integer, isOutside integer, isStairs integer";
	
	//--------------------------------------------------------Global Variables---------------------------------------------------------------
	private static Connection connection;
	public final static boolean DEBUG = true;
	
	private static ArrayList<Map> allMaps = new ArrayList<Map>();
	private static ArrayList<Point> allPoints = new ArrayList<Point>();
	private static ArrayList<Edge> allEdges = new ArrayList<Edge>();
	
	//------------------------------------------------------Singleton Handling---------------------------------------------------------------
	private static MappingDatabase instance;
	
	private MappingDatabase()
	{
		
	}
	
	public static MappingDatabase getInstance()
	{
		if (instance == null)
			instance = new MappingDatabase();
		return instance;
	}
	
	//------------------------------------------------------Function Definitions-------------------------------------------------------------
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

			/*System.out.println("Dropping old tables");
			statement.executeUpdate("drop table if exists "+MAP_TABLE_NAME);
			statement.executeUpdate("drop table if exists "+POINT_TABLE_NAME);
			statement.executeUpdate("drop table if exists "+EDGE_TABLE_NAME); */
			System.out.println("Creating new tables");
			statement.executeUpdate("create table if not exists "+ MAP_TABLE_NAME +" ("+ MAP_SCHEMA + ")");
			//if (DEBUG)
			//	System.out.println("Constructed Table: "+MAP_TABLE_NAME);
			//statement.executeUpdate("create table "+ POINT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");
			//if (DEBUG)
			//	System.out.println("Constructed Table: "+POINT_TABLE_NAME);
			statement.executeUpdate("create table if not exists "+ EDGE_TABLE_NAME +" ("+ EDGE_SCHEMA + ")");
			if (DEBUG)
				System.out.println("Constructed Table: "+EDGE_TABLE_NAME);
			System.out.println("Finished ensuring existence of table(s):"+EDGE_TABLE_NAME + ", "+MAP_TABLE_NAME);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												//If the error message is "out of memory", 
		}
	}
	public static void insertMap(Map map) throws AlreadyExistsException
	{
		int mapId = map.getId();
		//--------------------------------------------------Add to Database-----------------------------------------------------------------

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); 
			
			ResultSet rs = statement.executeQuery("SELECT * from "+MAP_TABLE_NAME);
			while (rs.next())
			{
				int comparisonId = rs.getInt("id");
				if (comparisonId == mapId)
				{
					throw new AlreadyExistsException("Map with same id exists already in SQLite DB");
				}
			}
			rs.close();
			//----------------------------------------------Build insert statement---------------------------------------------------------
			String insertStatement = "insert into " +MAP_TABLE_NAME+" values(";
			insertStatement += mapId;
			insertStatement += ", ";
			insertStatement += map.getName();
			insertStatement += ")";
			statement.executeUpdate(insertStatement);
			//---------------------------------------------Finish inserting-------------------------------------------------------------------
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//-------------------------------------------------Add to objects--------------------------------------------------------------------
		int counter = 0;
		boolean alreadyExists = false;
		if (allMaps == null || allMaps.isEmpty())
		{
			alreadyExists = false;
		}
		else
		{
			for (counter = 0; counter<allMaps.size(); counter++)
			{
				if (allMaps.get(counter).getId() == mapId)
				{
					alreadyExists = true;
				}
			}
		}
		if (alreadyExists)
			throw new AlreadyExistsException("Map already exists in object database");
		else
			allMaps.add(map);
	}
	
	public static void insertPoint(Map map, Point pt) throws AlreadyExistsException, NoMapException, InsertFailureException, SQLException
	{
		String RELEVANT_TABLE_NAME = "";
		
		int counter = 0;
		int pointID = pt.getId();
		String ptName = pt.getName();
		int ptX = pt.getX();
		int ptY = pt.getY();
		ArrayList<Edge> edgeArray = pt.getEdges();
		int numberEdges = pt.getNumberEdges();
		
		RELEVANT_TABLE_NAME += ("Map"+map.getId()+"Points");
		
		{
			//--------------------------------------------------Add to Database--------------------------------------------------------
			try {
				boolean alreadyExists = false;
				connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);  											// set timeout to 30 sec.
				
				ResultSet rs = statement.executeQuery("SELECT name FROM sqlite"
						+ "_master WHERE type='table' "
						+ "AND name='"+RELEVANT_TABLE_NAME+"'");							//Check if table already exists or not
				if (resultSetEmpty(rs))
				{
					if (DEBUG)
						System.out.println("Creating table: " + RELEVANT_TABLE_NAME);
					statement.executeUpdate("create table "+ 								//If does not exist, then create table
											RELEVANT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");
				}
				else
				{
					rs = statement.executeQuery("SELECT * from "+RELEVANT_TABLE_NAME);
					while (rs.next())
					{
						int comparisonId = rs.getInt("id");
						if (comparisonId == pt.getId())
						{
							throw new AlreadyExistsException("Point with same id exists already in SQLite DB");
						}
					}
				}
				rs.close();
				//----------------------------------------------Build insert statement---------------------------------------------------
				String insertStatement = "insert into " +RELEVANT_TABLE_NAME+" values(";
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
				insertStatement += "null";													//Formatting, last var doesn't have comma after
				insertStatement += ")";
				statement.executeUpdate(insertStatement);
				if (DEBUG)
					System.out.println("Sucessfully inserted into database");
				//--------------------------------------------------Finish inserting------------------------------------------------------
			} catch (SQLException e) {
				e.printStackTrace();
				throw new InsertFailureException("Failed to add to SQLite database");
			}
			finally
			{
				connection.close();
			}
			//-------------------------------------------------Add to local object storage----------------------------------------
			int mapId = map.getId();
			int mapIndex = -1;
			if (allMaps == null)
			{
				throw new NoMapException("Object database doesn't contain any maps");
			}
			if (allMaps.isEmpty())
			{
				throw new NoMapException("Object map database is empty");
			}
			else
			{
				counter = 0;
				for (counter = 0; counter<allMaps.size(); counter++)
				{
					if (allMaps.get(counter).getId() == mapId)
					{
						mapIndex = counter;
					}
				}
				if (mapIndex == -1)
				{
					throw new NoMapException("Map does not exist in object database");
				}
				else
				{
					if (checkObjectExists(map, pt))												//Check if point already exists in map object
					{
						throw new AlreadyExistsException("Point already exists in map");	//TODO revisit if this is actually how I want this to be handled
					}
					Map mapToAddTo = allMaps.get(mapIndex);
					ArrayList<Point> tempArrayList = new ArrayList<Point>();
					tempArrayList = mapToAddTo.getPointList();
					boolean addRes = false;
					addRes = tempArrayList.add(pt);
					if (addRes == false)
						throw new InsertFailureException("Failed to add to object database");
					else
					{
						mapToAddTo.setPointList(tempArrayList);
					}
				}																			//End map search check
			}																				//End empty check
			if (allPoints == null || allPoints.isEmpty())									//Begin adding to allPoints table
			{
				allPoints.add(pt);
			}
			else
			{
				counter = 0;
				boolean idExists = false;
				for (counter = 0; counter<allPoints.size(); counter++)
				{
					if (allPoints.get(counter).getId() == pt.getId())
						idExists = true;
				}
				if (idExists)
				{
					throw new AlreadyExistsException("Point id is already in object database (allPoints");
				}
				else
				{
					boolean insertRes;
					insertRes = allPoints.add(pt);
					if (!insertRes)
					{
						throw new InsertFailureException("Failed to add to allPoints array list");
					}
				}
			}
			if (DEBUG)
				System.out.println("Succesfully inserted into object database");
			
		}					//End checkObjectExists check
	}																						//End insertPoint
	
	/*
	public void insertEdge(Edge edge)
	{
		int counter = 0;
		String edgeId= edge.getId();
		Point point1 = edge.getPoint1();
		Point point2 = edge.getPoint2();
		int weight = edge.getWeight();
		boolean out= edge.isOutside();
		boolean stairs = edge.isStairs();
		
		//--------------------------------------------------Add to Database--------------------------------------------------------
		try {
			boolean alreadyExists = false;
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  											// set timeout to 30 sec.
		
			
			ResultSet rs = statement.executeQuery("SELECT * from "+EDGE_TABLE_NAME);
			while (rs.next())
			{
				String comparisonId = rs.getString("id");
				if (comparisonId.contentEquals(edge.getId()))
				{
					throw new AlreadyExistsException("Edge with same id exists already in SQLite DB");
				}
			}
		
			rs.close();
			//----------------------------------------------Build insert statement---------------------------------------------------
			String insertStatement = "insert into " +EDGE_TABLE_NAME+" values(";
			insertStatement += "'"+edgeId;
			insertStatement += ", ";
			insertStatement += ("'"+ptName+"'");
			insertStatement += ", ";
			insertStatement += ptX;
			insertStatement += ", ";
			insertStatement += ptY;
			insertStatement += ", ";
			insertStatement += numberEdges;
			insertStatement += ", ";
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
			insertStatement += "null";													//Formatting, last var doesn't have comma after
			insertStatement += ")";
			statement.executeUpdate(insertStatement);
			if (DEBUG)
				System.out.println("Sucessfully inserted into database");
			//--------------------------------------------------Finish inserting------------------------------------------------------
		} catch (SQLException e) {
			e.printStackTrace();
			throw new InsertFailureException("Failed to add to SQLite database");
		}
		finally
		{
			connection.close();
		}
		//-------------------------------------------------Add to local object storage----------------------------------------
		int mapId = map.getId();
		int mapIndex = -1;
		if (allMaps == null)
		{
			throw new NoMapException("Object database doesn't contain any maps");
		}
		if (allMaps.isEmpty())
		{
			throw new NoMapException("Object map database is empty");
		}
		else
		{
			counter = 0;
			for (counter = 0; counter<allMaps.size(); counter++)
			{
				if (allMaps.get(counter).getId() == mapId)
				{
					mapIndex = counter;
				}
			}
			if (mapIndex == -1)
			{
				throw new NoMapException("Map does not exist in object database");
			}
			else
			{
				if (checkObjectExists(map, pt))												//Check if point already exists in map object
				{
					throw new AlreadyExistsException("Point already exists in map");	//TODO revisit if this is actually how I want this to be handled
				}
				Map mapToAddTo = allMaps.get(mapIndex);
				ArrayList<Point> tempArrayList = new ArrayList<Point>();
				tempArrayList = mapToAddTo.getPointList();
				boolean addRes = false;
				addRes = tempArrayList.add(pt);
				if (addRes == false)
					throw new InsertFailureException("Failed to add to object database");
				else
				{
					mapToAddTo.setPointList(tempArrayList);
				}
			}																			//End map search check
		}																				//End empty check
		if (allPoints == null || allPoints.isEmpty())									//Begin adding to allPoints table
		{
			allPoints.add(pt);
		}
		else
		{
			counter = 0;
			boolean idExists = false;
			for (counter = 0; counter<allPoints.size(); counter++)
			{
				if (allPoints.get(counter).getId() == pt.getId())
					idExists = true;
			}
			if (idExists)
			{
				throw new AlreadyExistsException("Point id is already in object database (allPoints");
			}
			else
			{
				boolean insertRes;
				insertRes = allPoints.add(pt);
				if (!insertRes)
				{
					throw new InsertFailureException("Failed to add to allPoints array list");
				}
			}
		}
		if (DEBUG)
			System.out.println("Succesfully inserted into object database");
		
	}					//End checkObjectExists check
	}*/
	
	public static ArrayList<Point> getPoints(Map map) throws PopulateErrorException
	{
		ArrayList<Point> retArray = new ArrayList<Point>();
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  											// set timeout to 30 sec.
			String TABLE_NAME = "";
			TABLE_NAME += ("Map"+map.getId()+"Points");
			
			int newPtId;
			String newPtName;
			int newPtX;
			int newPtY;
			int newPtNumberEdges;
			ArrayList<Edge> newPtEdges = new ArrayList<Edge>();
			
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_NAME);
			while (rs.next())
			{
				newPtId = rs.getInt("id");
				newPtName = rs.getString("name");
				newPtX = rs.getInt("x");
				newPtY = rs.getInt("y");
				newPtNumberEdges = rs.getInt("numEdges");
				
				String edgeSelect = "idEdge";
				String edgeId= "";
				int counter = 0;
				int edgeCounter = 0;
				for (counter = 0; counter<newPtNumberEdges; counter++)
				{
					edgeSelect = ("idEdge"+String.valueOf(counter));
					edgeId = rs.getString(edgeSelect);
					boolean foundEdge = false;
					for (edgeCounter = 0; edgeCounter<allEdges.size(); edgeCounter++)
					{
						if (allEdges.get(edgeCounter).getID().contentEquals(edgeId))
						{
							foundEdge = true;
							newPtEdges.add(allEdges.get(edgeCounter));
						}
						if (foundEdge == false)
						{
							throw new PopulateErrorException("Couldn't find edgeId in allEdges");
						}
					}
				}
				
				Point newPt = new Point(newPtId, newPtName, newPtX, newPtY, newPtNumberEdges);
				retArray.add(newPt);
			}
			rs.close();
			return retArray;																			//TODO add error handling
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return retArray;
		}			
	}

	public ArrayList<Map> getMaps()
	{
		return allMaps;
	}
	
	public static void printDatabase(boolean printMaps, boolean printPoints, boolean printEdges)
	{
		try
		{
			Statement statement = connection.createStatement();
			//SELECT name FROM my_db.sqlite_master WHERE type='table';
			if (printMaps)
			{
				ResultSet rs = statement.executeQuery("select * from "+MAP_TABLE_NAME);
				System.out.println("Printing "+MAP_TABLE_NAME+" from "+DATABASE_NAME);
				while(rs.next())															// read the result set
				{
					System.out.println("");
				}
				rs.close();
			}
			if (printPoints)
			{
				ResultSet rs = statement.executeQuery("select * from "+POINT_TABLE_NAME);
				System.out.println("Printing "+POINT_TABLE_NAME+" from "+DATABASE_NAME);
				while(rs.next())															// read the result set
				{
					System.out.println("name = " + rs.getString("name"));
					System.out.println("id = " + rs.getInt("id"));
					System.out.print("xCoord = " + rs.getInt("xCoord"));
					System.out.println(", yCoord = " + rs.getInt("yCoord"));
					System.out.println();
				}
				rs.close();
			}
			if (printEdges)
			{
				ResultSet rs = statement.executeQuery("select * from "+EDGE_TABLE_NAME);
				System.out.println("Printing "+EDGE_TABLE_NAME+" from "+DATABASE_NAME);
				while(rs.next())															// read the result set
				{
					System.out.println();
				}
				rs.close();
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												// if the error message is "out of memory", 
																							// it probably means no database file is found
		}
	}

	public static void printObjects(boolean printMaps, boolean printPoints, boolean printEdges)
	{
		System.out.println("Printing stored objects");
		int counter = 0;
		if (printMaps)
		{
			for (counter = 0; counter<allMaps.size();counter++)
			{
				System.out.println("Map"+allMaps.get(counter).getId());
			}
		}
		if (printPoints)
		{
			for (counter = 0; counter<allPoints.size(); counter++)
			{
				System.out.println("Point"+allPoints.get(counter).getId());
			}
		}
		if (printEdges)
		{
			for (counter = 0; counter<allEdges.size(); counter++)
			{
				System.out.println("Edge"+allEdges.get(counter).getId());
			}
		}
	}
	
	public static boolean checkObjectExists (Map map, Point pt)
	{
		ArrayList<Point> pointList = map.getPointList();
		if (pointList == null)
			return false;
		else if (pointList.isEmpty())
			return false;
		else
		{
			int arrayLength = pointList.size();
			int counter = 0;
			for (counter = 0; counter < arrayLength; counter++)
			{
				if (pointList.get(counter).getId() == pt.getId())
					return true;
			}
			return false;
		}
	}
	
	public static boolean resultSetEmpty(ResultSet rs)
	{
		try {
			if (!rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;												//TODO look into why this needs to be in a try catch
		}
	}
	
	public static void testInsert()
	{
		ArrayList<Point> insertablePoints = new ArrayList<Point>();
		Point p1 = new Point(0, "p1", 0, 0);
		Point p2 = new Point(1, "p2", 0, 1);
		Point p3 = new Point(2, "p3", 1, 0);
		Point p4 = new Point(3, "p4", 2, 0);
		Point p5 = new Point(4, "p5", 23, 90);
		Point p6 = new Point(5, "p6", 27, 90);
		Point testPoint = new Point(1432, "testPoint", 23, 56, 0);
		Edge[] emptyArray = null;
		ArrayList<Point> emptyPointArrayList = new ArrayList<Point>();
		Map testMap = new Map(emptyPointArrayList, 22);
		//--------------------------------------------------Add points to insert arraylist--------------------------------
		insertablePoints.add(p1);
		insertablePoints.add(p2);
		insertablePoints.add(p3);
		insertablePoints.add(p4);
		insertablePoints.add(p5);
		insertablePoints.add(testPoint);
		
		int counter = 0;
		
		//------------------------------------------------Populate Object storage from database-----------------------------
		try {
			populateFromDatabase();
		} catch (PopulateErrorException e2) {
			e2.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//-------------------------------------------------------Test map insert--------------------------------------------
		try {																																						
			insertMap(testMap);
		} catch (AlreadyExistsException e1) {
			System.out.println("Map already exists in Database");
		}
		
		//------------------------------------------------------Test point insert--------------------------------------------
		for (counter = 0; counter<insertablePoints.size(); counter++)
		{
			try {																				//Test insert point
				insertPoint(testMap, insertablePoints.get(counter));
			} catch (AlreadyExistsException e) {
				System.out.println("Point:"+insertablePoints.get(counter).getId()+ " already exists");
			} catch (NoMapException e) {
				e.printStackTrace();
			} catch (InsertFailureException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//----------------------------------------------See if inserts were successful---------------------------------------
		ArrayList<Point> testArray = new ArrayList<Point>();
		try {
			testArray = getPoints(testMap);
		} catch (PopulateErrorException e) {
			e.printStackTrace();
		}
		
		int count = 0;
		System.out.println("Printing points found in map: "+testMap.getId());
		for (count = 0; count<testArray.size(); count++)
		{
			System.out.println("PointId:"+testArray.get(count).getId());
		}
		//printObjects(true, true, true); 
		
	}
	
	private static void clearDatabase()
	{
		if (DEBUG)
			System.out.println("Clearing database!");
		String command = "";
		try {
			Statement statement = connection.createStatement();

			ResultSet rs = statement.executeQuery("SELECT name FROM sqlite"
					+ "_master WHERE type='table' ");
			while (rs.next())
			{
				command += ("drop table "+rs.getString(1));
				statement.executeUpdate(command);
				if (DEBUG)
					System.out.println(command);
				command = "";
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void populateFromDatabase() throws PopulateErrorException, SQLException
	{
		allMaps.clear();
		allPoints.clear();
		allEdges.clear();
		
		try {
			int newPtId;
			String newPtName;
			int newPtX;
			int newPtY;
			int newPtNumberEdges;
			ArrayList<Edge> newPtEdges = new ArrayList<Edge>();
			
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			//statement1.setQueryTimeout(30);  											// set timeout to 30 sec.
			//statement2.setQueryTimeout(30);  											// set timeout to 30 sec.
			

			ResultSet rs1 = connection.createStatement().executeQuery("SELECT name FROM sqlite"
					+ "_master WHERE type='table' ");
			ResultSet rs2;
			ResultSet rs3;
			while (rs1.next())
			{
				//------------------------------------------Populate Edges-------------------------------------------
				if (rs1.getString("name").toLowerCase().contains("Edges".toLowerCase()))
				{
					
				}
				//------------------------------------------Populate Points-------------------------------------------
				else if (rs1.getString("name").toLowerCase().contains("Points".toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+rs1.getString("name")+" which is at row: " + rs1.getRow());
					
					rs2 = connection.createStatement().executeQuery("SELECT * FROM "+rs1.getString("name"));
					while(rs2.next())
					//TODO CHECK TABLE NAME FOR MAP OR EDGE
					{
						newPtId = rs2.getInt("id");
						newPtName = rs2.getString("name");
						newPtX = rs2.getInt("x");
						newPtY = rs2.getInt("y");
						newPtNumberEdges = rs2.getInt("numEdges");
	
						String edgeSelect = "idEdge";
						String edgeId= "";
						int counter = 0;
						int edgeCounter = 0;
						for (counter = 0; counter<newPtNumberEdges; counter++)
						{
							edgeSelect = ("idEdge"+String.valueOf(counter));
							edgeId = rs2.getString(edgeSelect);
							boolean foundEdge = false;
							for (edgeCounter = 0; edgeCounter<allEdges.size(); edgeCounter++)
							{
								if (allEdges.get(edgeCounter).getID().contentEquals(edgeId))
								{
									foundEdge = true;
									newPtEdges.add(allEdges.get(edgeCounter));
								}
								if (foundEdge == false)
								{
									throw new PopulateErrorException("Couldn't find edgeId in allEdges");
								}
							}
						}
						 
						Point newPt = new Point(newPtId, newPtName, newPtX, newPtY, newPtNumberEdges);
					}
					rs2.close();
				}
				//----------------------------------------Populate Map--------------------------------------------
				else if (rs1.getString("name").toLowerCase().contentEquals(MAP_TABLE_NAME.toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+rs1.getString("name")+" which is at row: " + rs1.getRow());
					
					rs2 = connection.createStatement().executeQuery("SELECT * FROM "+rs1.getString("name"));
					while (rs2.next())
					{
						int newMapId = rs2.getInt("id");
						String newMapName = rs2.getString("name");
						Map newMap = new Map(newMapId, newMapName);
						allMaps.add(newMap);
					}
				}
				else
				{
					throw new PopulateErrorException("Invalid table type. Can't resolve name:"+rs1.getString("name"));
				}
			}
			rs1.close();
			if(DEBUG)
				System.out.println("Done populating.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			connection.close();
		}
	}
}	

