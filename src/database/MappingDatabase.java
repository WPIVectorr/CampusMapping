package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import main_package.*;

/*
 * TODO
 * Cleanup insertEdge, make it add edgeId to the relevant points in the SQLite database
 * Add functions
 * -getAllMaps
 * Cleanup print database and print objects
 * Adjust populate to work with edges
 * TODO
 */
public class MappingDatabase 
{
	//-----------------------------------------------------------Constants--------------------------------------------------------------------
	private static String DATABASE_NAME = "campusMapping.db";
	
	private static String MAP_TABLE_NAME = "Maps";
	private static String POINT_TABLE_NAME = "Points";
	private static String EDGE_TABLE_NAME = "WeigtedEdges";
	
	private static String MAP_SCHEMA = "id integer, name String";
	private static String POINT_SCHEMA = "id integer, name String, x integer, y integer, numEdges integer, idEdge1 integer,"
			+ " idEdge2 integer, idEdge3 integer, idEdge4 integer, idEdge5 integer, idEdge6 integer, idEdge7 integer, idEdge8 integer,"
			+ "idEdge9 integer, idEdge10 integer";
	private static String EDGE_SCHEMA = "id String, idPoint1 integer, idPoint2 integer, weight integer, isOutside integer, isStairs integer";
	
	//--------------------------------------------------------Global Variables---------------------------------------------------------------
	private static Connection connection;
	public final static boolean DEBUG = false;
	
	private static ArrayList<Map> allMaps = new ArrayList<Map>();
	private static ArrayList<Point> allPoints = new ArrayList<Point>();
	private static ArrayList<Edge> allEdges = new ArrayList<Edge>();
	
	//--------------------------------------------------------Singleton Handling--------------------------------------------------------------
	private static MappingDatabase instance;
	
	private MappingDatabase()
	{
		
	}
	
	public static MappingDatabase getInstance()
	{
		if (instance == null)
			instance = new MappingDatabase();
		initDatabase();
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
			//statement.executeUpdate("create table "+ POINT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");
			statement.executeUpdate("create table if not exists "+ EDGE_TABLE_NAME +" ("+ EDGE_SCHEMA + ")");
			System.out.println("Finished ensuring existence of table(s):"+EDGE_TABLE_NAME + ", "+MAP_TABLE_NAME);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												//If the error message is "out of memory", 
		}
	}
	
	public static void insertMap(Map map) throws AlreadyExistsException, SQLException
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
			insertStatement += ("'"+map.getName()+"'");
			insertStatement += ")";
			statement.executeUpdate(insertStatement);
			//---------------------------------------------Finish inserting-------------------------------------------------------------------
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			connection.close();
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
		//------------------------------------------------Add all points------------------------------------------------------------
		if (map.getPointList() != null)
		{
			int k = 0;
			for (k = 0; k<map.getPointList().size(); k++)
			{
				Point tempPt = map.getPointList().get(k);
				try {
					insertPoint(map, tempPt);
				} catch (NoMapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InsertFailureException e) {
					System.out.println(e.getMessage());
				}
			}
		}
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
					rs = statement.executeQuery("SELECT * from "+RELEVANT_TABLE_NAME);		//Get all points from table (Currently only assumes map-level point id uniqueness)
					while (rs.next())
					{
						int comparisonId = rs.getInt("id");								
						if (comparisonId == pt.getId())										//Iterate through making sure that this id does not already exist
						{
							throw new AlreadyExistsException("Point with same id exists already in SQLite DB");
						}
					}
				}	
				rs.close();																	//Close resultSet to prevent errors
				//TODO Take care of point having a mapId attribute
				//----------------------------------------------Build insert statement---------------------------------------------------
				String insertStatement = "insert into " +RELEVANT_TABLE_NAME+" values(";	//Start building SQLite insert statement
				insertStatement += pointID;													
				insertStatement += ", ";													//Add commas for formatting reasons
				insertStatement += ("'"+ptName+"'");										
				insertStatement += ", ";
				insertStatement += ptX;
				insertStatement += ", ";
				insertStatement += ptY;
				insertStatement += ", ";
				insertStatement += numberEdges;
				insertStatement += ", ";
				for (counter =0; counter < numberEdges; counter++)							//Add number of edges edges to the point
				{
					insertStatement += "'"+edgeArray.get(counter).getID()+"'";
					insertStatement += ", ";
				}
				for (counter = numberEdges; counter < 9; counter++)							//Add null padding so there are enough arguments in the insert statement
				{
					insertStatement += "null";
					insertStatement += ", ";
				}
				insertStatement += "null";													//Formatting, last value must not have comma after it
				insertStatement += ")";
				statement.executeUpdate(insertStatement);									//Insert data
				if (DEBUG)
					System.out.println("Sucessfully inserted point into database");
				//--------------------------------------------------Finish inserting------------------------------------------------------
			} catch (SQLException e) {
				e.printStackTrace();
				throw new InsertFailureException("Failed to add to SQLite database");
			}
			finally
			{
				connection.close();
			}
			//-------------------------------------------------Add to local object storage-------------------------------------------------
			//-------------------------------------------------------Add to Map object-----------------------------------------------------
			int mapId = map.getId();
			int mapIndex = -1;
			if (allMaps == null)															//Check that point can be properly added to the map object
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
				for (counter = 0; counter<allMaps.size(); counter++)						//Check through all map objects to find the one we want
				{
					if (allMaps.get(counter).getId() == mapId)
					{
						mapIndex = counter;
					}
				}
				if (mapIndex == -1)															//If map does not exist, throw error
				{	
					throw new NoMapException("Map does not exist in object database");
				}
				else
				{
					if (checkObjectExists(map, pt))											//Check if point already exists in map object
					{
						throw new AlreadyExistsException("Point already exists in map");	//TODO revisit if this is actually how I want this to be handled
					}
					Map mapToAddTo = allMaps.get(mapIndex);
					ArrayList<Point> tempArrayList = new ArrayList<Point>();				//Create temporary array for adding purposes (May want to move this to a helper function in point)
					if (mapToAddTo.getPointList()!=null)
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
			//---------------------------------------------------------Add to allPoints array------------------------------------------------------------
			if (allPoints == null || allPoints.isEmpty())									//If empty or null, safe to add								
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
				System.out.println("Succesfully inserted point into object database");
			
		}																					//End checkObjectExists check
	}																						//End insertPoint
		
	public static void insertEdge(Edge edge) throws InsertFailureException, AlreadyExistsException, SQLException, DoesNotExistException
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
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.
		
			
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
			insertStatement += ("'"+edgeId+"'");
			insertStatement += ", ";
			insertStatement += (point1.getId());
			insertStatement += ", ";
			insertStatement += (point2.getId());
			insertStatement += ", ";
			insertStatement += weight;
			insertStatement += ", ";
			int outInt = (out) ? 1 : 0;
			insertStatement += outInt;
			insertStatement += ", ";
			int stairsInt = (out) ? 1 : 0;
			insertStatement += stairsInt;
			insertStatement += ")";
			statement.executeUpdate(insertStatement);
			if (DEBUG)
				System.out.println("Sucessfully inserted edge:"+edgeId+" into database");
			//--------------------------------------------------Finish inserting------------------------------------------------------
			//---------------------------------------------Add to Points in Database--------------------------------------------------
			
			//point1.addEdge(edge);
			//point2.addEdge(edge);
			updatePoint(point1);
			updatePoint(point2);
			
			if (DEBUG)
			{
				System.out.println("Successfully updated points in database");
			}
			//----------------------------------------Finished adding to points in database-------------------------------------------
		} 
		catch (SQLException e) {
			e.printStackTrace();
			throw new InsertFailureException("Failed to add to SQLite database");
		}
		finally
		{
			connection.close();
		}
		//-------------------------------------------------Add to local object storage----------------------------------------
		boolean alreadyExists = false;
		if (allEdges == null)
		{
		}
		else if (allMaps.isEmpty())
		{
		}
		else
		{
			counter = 0;
			for (counter = 0; counter<allEdges.size(); counter++)
			{
				if (allEdges.get(counter).getId().contentEquals(edgeId))
				{
					alreadyExists = true;
				}
			}
			if (alreadyExists)
			{
				throw new AlreadyExistsException("Already exists in edge database table");
			}
			else
			{
				boolean addRes = allEdges.add(edge);
				if (addRes == false)
					throw new InsertFailureException("Failed to add edge to object database");
			}																			
		}
		//-----------------------------------------------Refresh all local objects------------------------------------
		try {
			populateFromDatabase();
		} catch (PopulateErrorException e) {
			e.printStackTrace();
		}
		if (DEBUG)
			System.out.println("Successfully inserted edge into object database");
	}
	
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
				newPtEdges = new ArrayList<Edge>();
				
				String edgeSelect = "idEdge";
				String edgeId= "";
				int counter = 0;
				int edgeCounter = 0;
				for (counter = 0; counter<newPtNumberEdges; counter++)
				{
					edgeSelect = ("idEdge"+String.valueOf(counter+1));
					edgeId = rs.getString(edgeSelect);
					boolean foundEdge = false;
					for (edgeCounter = 0; edgeCounter<allEdges.size(); edgeCounter++)
					{
						if (allEdges.get(edgeCounter).getID().contentEquals(edgeId))
						{
							foundEdge = true;
							newPtEdges.add(allEdges.get(edgeCounter));
							//System.out.println("found:"+allEdges.get(edgeCounter).getId());
						}
					}
					
					if (foundEdge == false)
					{			
						throw new PopulateErrorException("Couldn't find edgeId in allEdges");
					}
				}
				
				Point newPt = new Point(newPtId, newPtName, newPtX, newPtY, newPtNumberEdges);
				//System.out.println("---------------------------"+newPtEdges.size());
				newPt.setEdges(newPtEdges);
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
		try {
			populateFromDatabase();
		} catch (PopulateErrorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int counter = 0;
		for (counter = 0; counter<allMaps.size(); counter++)
		{
			Map tempMap = allMaps.get(counter);
			try {
				tempMap.setPointList(getPoints(tempMap));
			} catch (PopulateErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			allMaps.set(counter, tempMap);
		}
		return allMaps;
	}
	
	public static void printDatabase(boolean printMaps, boolean printPoints, boolean printEdges) throws SQLException
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			Statement statement = connection.createStatement();
			ResultSet rs1 = statement.executeQuery("SELECT name FROM sqlite"+ "_master WHERE type='table'");
			if (DEBUG)
				System.out.println("Retrieved Tables");
			while (rs1.next())
			{
				String tableName = rs1.getString("name");
				ResultSet rs2 = connection.createStatement().executeQuery("SELECT * FROM "+tableName);
				while (rs2.next())
				{
					if (tableName.contentEquals(MAP_TABLE_NAME))
					{
						if (printMaps)
						{
							ResultSet rs = connection.createStatement().executeQuery("select * from "+MAP_TABLE_NAME);
							System.out.println("Printing "+MAP_TABLE_NAME+" from "+DATABASE_NAME);
							while(rs.next())															// read the result set
							{
								System.out.println("MapID:"+rs.getInt("id"));
							}
						}
					}
					else if (tableName.contains("Points"))
					{
						if (printPoints)
						{
							ResultSet rs = connection.createStatement().executeQuery("select * from "+tableName);
							System.out.println("Printing "+POINT_TABLE_NAME+" from "+DATABASE_NAME);
							while(rs.next())															// read the result set
							{
								System.out.println("name = " + rs.getString("name"));
								System.out.println("id = " + rs.getInt("id"));
								System.out.print("xCoord = " + rs.getInt("xCoord"));
								System.out.println(", yCoord = " + rs.getInt("yCoord"));
								System.out.println("numEdges = "+rs.getInt("numEdges"));
							}
							rs.close();
						}
					}
					else if (tableName.contains("Edges"))
					{
						if (printEdges)
						{
							ResultSet rs = statement.executeQuery("select * from "+tableName);
							System.out.println("Printing "+EDGE_TABLE_NAME+" from "+DATABASE_NAME);
							while(rs.next())															// read the result set
							{
								System.out.println("id = "+rs.getString("id"));
							}
							rs.close();
						}
					}
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												// if the error message is "out of memory", 
																							// it probably means no database file is found
		}
		finally
		{
			connection.close();
		}
	}

	public static void printObjects(boolean printMaps, boolean printPoints, boolean printEdges)
	{
		int counter = 0;
		if (printMaps)
		{
			System.out.println("Printing stored map objects");
			for (counter = 0; counter<allMaps.size();counter++)
			{
				System.out.println("Map"+allMaps.get(counter).getId());
			}
		}
		if (printPoints)
		{
			System.out.println("Printing stored point objects");
			for (counter = 0; counter<allPoints.size(); counter++)
			{
				System.out.println("Point"+allPoints.get(counter).getId());
			}
		}
		if (printEdges)
		{
			System.out.println("Printing stored edge objects");
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
		
		class cleanUpEdgePointLink
		{
			public int pt1id;
			public int pt2id;
			public String edgeId;
			public cleanUpEdgePointLink (int pt1, int pt2, String edge)
			{
				this.pt1id = pt1;
				this.pt2id = pt2;
				this.edgeId = edge;
			}
		}
		
		ArrayList<cleanUpEdgePointLink> cleanUpArray = new ArrayList<cleanUpEdgePointLink>();
		try {
			int newPtId;
			String newPtName;
			int newPtX;
			int newPtY;
			int newPtNumberEdges;
			
			String newEdgeId;
			int newEdgePt1;
			int newEdgePt2;
			int newEdgeWeight;
			int newEdgeOutside;
			int newEdgeStairs;
			
			ArrayList<Edge> newPtEdges = new ArrayList<Edge>();
			
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
			ResultSet rs1 = connection.createStatement().executeQuery("SELECT name FROM sqlite"
					+ "_master WHERE type='table' ");
			ResultSet rs2;
			ResultSet rs3;
			ResultSet rs4;
			
			ArrayList<String> tableNames = new ArrayList<String>();
			
			while (rs1.next())
			{
				tableNames.add(rs1.getString("name"));
			}
			Collections.sort(tableNames);
			int nameCounter = 0;
			rs4 = connection.createStatement().executeQuery("SELECT name FROM sqlite"
					+ "_master WHERE type='table' ");
			for (nameCounter = 0; nameCounter < tableNames.size(); nameCounter++)
			{
				
				//------------------------------------------Populate Points-------------------------------------------
				if (tableNames.get(nameCounter).toLowerCase().contains("Points".toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+tableNames.get(nameCounter)+" which is at row: " + nameCounter);
					
					rs2 = connection.createStatement().executeQuery("SELECT * FROM "+tableNames.get(nameCounter));
					while(rs2.next())
					{
						newPtId = rs2.getInt("id");
						newPtName = rs2.getString("name");
						newPtX = rs2.getInt("x");
						newPtY = rs2.getInt("y");
						newPtNumberEdges = rs2.getInt("numEdges");
						Point newPt = new Point(newPtId, newPtName, newPtX, newPtY, newPtNumberEdges);
						allPoints.add(newPt);
					}
					rs2.close();
				}
				//------------------------------------------Populate Edges-------------------------------------------
				else if (tableNames.get(nameCounter).toLowerCase().contains("Edges".toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+tableNames.get(nameCounter)+" which is at row: " + nameCounter);
					rs2 = connection.createStatement().executeQuery("SELECT * FROM "+tableNames.get(nameCounter));
					while(rs2.next())
					{
						newEdgeId = rs2.getString("id");
						newEdgePt1 = rs2.getInt("idPoint1");
						Point pt1 = new Point();
						Point pt2 = new Point();
						try {
							pt1 = getPoint(newEdgePt1);
						} catch (DoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						newEdgePt2 = rs2.getInt("idPoint2");
						try {
							pt2 = getPoint(newEdgePt2);
						} catch (DoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						newEdgeWeight = rs2.getInt("weight");
						newEdgeOutside = rs2.getInt("isOutside");
						newEdgeStairs = rs2.getInt("isStairs");
						boolean out = (newEdgeOutside==1);
						boolean stairs = (newEdgeStairs == 1);
				
						Edge newEdge = new Edge(pt1, pt2, newEdgeWeight, out, stairs);			//Automatically adds to points
						allEdges.add(newEdge);
					}
					rs2.close();
				}
				
				//----------------------------------------Populate Map--------------------------------------------
				else if (tableNames.get(nameCounter).toLowerCase().contentEquals(MAP_TABLE_NAME.toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+tableNames.get(nameCounter)+" which is at row: " + nameCounter);
					
					rs2 = connection.createStatement().executeQuery("SELECT * FROM "+tableNames.get(nameCounter));
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			connection.close();
		}
	}

	private static Point getPoint(int pointId) throws DoesNotExistException
	{
		int counter = 0;
		Point retPt = null;
		boolean found = false;
		for (counter = 0; counter<allPoints.size(); counter++)
		{
			if (allPoints.get(counter).getId() == pointId)
			{
				retPt = allPoints.get(counter);
				found = true;
				break;
			}
		}
		if (found)
		{
			return retPt;
		}
		else
		{
			System.out.println("Failed to get point:"+pointId);
			throw new DoesNotExistException("Point does not exist in object database");
		}
	}

	private static void updatePoint (Point point) throws SQLException, DoesNotExistException
	{
		int ptId = point.getId();
		//---------------------------------------Update database-------------------------------------------------
		connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(5);  											// set timeout to 30 sec.
		
		ResultSet rs1 = statement.executeQuery("SELECT name FROM sqlite"
				+ "_master WHERE type='table'");
		
		boolean found = false;
		while (rs1.next())
		{
			String tableName = rs1.getString("name");
			ResultSet rs2 = connection.createStatement().executeQuery("SELECT * FROM "+tableName);
			while (rs2.next())
			{
				if (tableName.toLowerCase().contains("points"))
				{
					if (rs2.getInt("id")==ptId)
					{
						found = true;
						String updateStatement = ("UPDATE "+rs1.getString("name")+" SET ");
						updateStatement += ("name ="+"'"+point.getName()+"'");										
						updateStatement += ", ";
						updateStatement += ("x = "+ point.getX());
						updateStatement += ", ";
						updateStatement += ("y = "+point.getY());
						updateStatement += ", ";
						updateStatement += ("numEdges = "+point.getNumberEdges());
						updateStatement += ", ";
						int i = 0;
						for (i =0; i < point.getNumberEdges(); i++)							//Add number of edges edges to the point
						{
							updateStatement += ("idEdge"+(i+1)+" = "
									+"'"+point.getEdges().get(i).getID()+"'");
							updateStatement += ", ";
						}
						for (i = point.getNumberEdges(); i < 9; i++)							//Add null padding so there are enough arguments in the insert statement
						{
							updateStatement += ("idEdge"+(i+1)+" = "+"null");
							updateStatement += ", ";
						}
						updateStatement += ("idEdge"+(i+1)+" = "+"null");						//Formatting, last value must not have comma after it
						updateStatement +=(" WHERE ID = "+ptId);
						statement.executeUpdate(updateStatement);									//Insert data
						if (DEBUG)
							System.out.println("Sucessfully updated point:"+ptId+" in database");
						updateStatement +=("WHERE ID = "+ptId);
					}
				}
			}
		}
		connection.close();
		if (found == false)
		{
			throw new DoesNotExistException("Could not find point in database"); 
		}
		//---------------------------------------------------Update local object----------------------------------------------------
		int j = 0;
		boolean foundObject = false;
		for (j = 0; j < allPoints.size(); j++)
		{
			if (allPoints.get(j).getId() == ptId)
			{
				foundObject = true;
				allPoints.set(j, point);
			}
		}
		if (foundObject == false)
		{
			throw new DoesNotExistException("Could not find point object in object storage");
		}
	}
	
	/*
	private static void deletePoint(Point point)
	{
		
	}
	*/
	
	private void printPoint(int ptId) throws DoesNotExistException
	{
		int j = 0;
		boolean found = false;
		for (j = 0; j<allPoints.size(); j++)
		{
			if (allPoints.get(j).getId() == ptId)
			{
				found = true;
				Point tempPt = allPoints.get(j);
				System.out.println("Point id: "+tempPt.getId());
				System.out.println("Point name: "+tempPt.getName());
				System.out.println("Point Coords: "+tempPt.getX()+", "+tempPt.getY());
			}
		}
		if (!found)
		{
			throw new DoesNotExistException("Point does not exist in object database");
		}
	}
	
	public static void testInsert()
	{
		ArrayList<Point> insertablePoints = new ArrayList<Point>();
		ArrayList<Edge> insertableEdges = new ArrayList<Edge>();
		Point testPoint = new Point(1432, "testPoint", 23, 56, 0);
		Point p1 = new Point(0, "p1", 0, 0);
		Point p2 = new Point(1, "p2", 0, 1);
		Point p3 = new Point(2, "p3", 1, 0);
		Point p4 = new Point(3, "p4", 2, 0);
		Point p5 = new Point(4, "p5", 23, 90);
		Point p6 = new Point(5, "p6", 27, 90);
		Edge e1 = new Edge(p1, p2, 1);
		Edge e2 = new Edge(p2, p5, 1);
		Edge e3 = new Edge(p2, p3, 1);
		Edge e4 = new Edge(p3, p4, 1);
		Edge e5 = new Edge(p3, p5, 1);
		Edge e6 = new Edge(p4, p5, 1);
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
		insertableEdges.add(e1);
		insertableEdges.add(e2);
		insertableEdges.add(e3);
		insertableEdges.add(e4);
		insertableEdges.add(e5);
		insertableEdges.add(e6);
		int counter = 0;
		
		//------------------------------------------------Populate Object storage from database-----------------------------
		try {
			populateFromDatabase();
		} catch (PopulateErrorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		printObjects(true, true, true);
		//-------------------------------------------------------Test map insert--------------------------------------------
		try {																																						
			insertMap(testMap);
		} catch (AlreadyExistsException e) {
			System.out.println("Map already exists in Database");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//------------------------------------------------------Test point insert--------------------------------------------
		System.out.println("Testing point insert");
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
		System.out.println("Finished testing point insert");
		//-------------------------------------------------------Test edge insert-------------------------------------------
		System.out.println("Testing edge insert");
		for (counter = 0; counter<insertableEdges.size(); counter++)
		{
			try {																				//Test insert point
				insertEdge(insertableEdges.get(counter));
			} catch (AlreadyExistsException e) {
				System.out.println("Edge:"+insertablePoints.get(counter).getId()+ " already exists");
			} catch (InsertFailureException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (DoesNotExistException e) {
				System.out.println("Edge not added to database");
				e.printStackTrace();
			}
		}
		System.out.println("Finished testing edge insert");
		//----------------------------------------------See if inserts were successful---------------------------------------
		System.out.println("Testing results of inserts");
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
			Point tempPt = testArray.get(count);
			tempPt.print();
		}
		
	}
	
	public void testUpdate()
	{
		System.out.println("Getting point 1 from objects");
		Point testPoint = null;
		try {
			testPoint = getPoint (1);
		} catch (DoesNotExistException e) {
			e.printStackTrace();
			System.out.println("Does not exist failure");
			try {
				printDatabase(true, true, true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		testPoint.setName ("FlufferDoodle");
		
		System.out.println("Updating point 1");
		try {
			updatePoint(testPoint);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Sqlexception");
		} catch (DoesNotExistException e) {
			e.printStackTrace();
			System.out.println("Could not find point to update");
		}
		
		try {
			printPoint(1);
		} catch (DoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testMaps()
	{
		Map testMap = new Map(1, "Campus");
		Map testMap2 = new Map(2, "AK");
		try {
			insertMap(testMap);
		} catch (AlreadyExistsException e4) {
			System.out.println(e4.getMessage());
		} catch (SQLException e4) {
			System.out.println(e4.getMessage());
		}
		try {
			insertMap(testMap2);
		} catch (AlreadyExistsException e4) {
			System.out.println(e4.getMessage());
		} catch (SQLException e4) {
			System.out.println(e4.getMessage());
		}
		ArrayList<Point> points = new ArrayList<Point>();
		Point testPoint1 = new Point (15, "One", 50, 100);
		Point testPoint2 = new Point (30, "Two", 600, 500);
		Point testPoint3 = new Point (45, "Three", 500, 700);
		Point testPoint4 = new Point (78, "Four", 200, 200);
		Point testPoint7 = new Point (109, "seven", 100, 500);
		Point testPoint5 = new Point (88, "Five", 500, 600);
		Point testPoint6 = new Point (91, "Six", 700, 500);
		points.add(testPoint1);
		points.add(testPoint2);
		points.add(testPoint3);
		points.add(testPoint4);
		points.add(testPoint5);
		points.add(testPoint6);
		points.add(testPoint7);
		int z = 0;
		for (z = 0; z<4; z++)
		{
			try {
				insertPoint(testMap, points.get(z));
			} catch (AlreadyExistsException e) {
				System.out.println(e.getMessage());
			} catch (NoMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InsertFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (z = 4; z<7; z++)
		{
			try {
				insertPoint(testMap2, points.get(z));
			} catch (AlreadyExistsException e) {
				System.out.println(e.getMessage());
			} catch (NoMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InsertFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Edge e1 = new Edge(testPoint1, testPoint2, 1);
		Edge e2 = new Edge(testPoint2, testPoint3, 1);
		Edge e3 = new Edge(testPoint1, testPoint4, 1);
		Edge e7 = new Edge(testPoint7, testPoint6, 1);
		ArrayList<Edge> edges = new ArrayList<Edge>();
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
		edges.add(e7);
		for (z = 0; z < edges.size(); z++)
		{
			try {
				insertEdge(edges.get(z));
			} catch (InsertFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyExistsException e) {
				System.out.println(e.getMessage());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DoesNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		ArrayList<Map>maps = getMaps();
		System.out.println("Number maps: " + maps.size());
		
		ArrayList<Map> mapList = getMaps();
		int j = 0;
		int k = 0;
		for (j = 0; j<mapList.size(); j++)
		{
			Map tempMap = mapList.get(j);
			System.out.println("Map: "+tempMap);
			ArrayList<Point> pointList = tempMap.getPointList();
			for (k = 0; k<pointList.size(); k++)
			{
				System.out.println("--------------------Point "+pointList.get(k).getId()+"---------------------------");
				pointList.get(k).print();
			}
		}
		//System.out.println("maps.size()
	}
}	

