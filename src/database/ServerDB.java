package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.*;

import main_package.Edge;
import main_package.Map;
import main_package.Point;


public class ServerDB {
	//------------------------------------------------------------Constants--------------------------------------------------------------------
	private static String DATABASE_URL = "jdbc:mysql://campusmapping.c5bohpl1biax.us-west-2.rds.amazonaws.com:3306/";	
	private static String DATABASE_NAME = "campusMapping_db";
	private static String userName = "Vectorr";
	private static String password = "mag";

	private static String MAP_TABLE_NAME = "AddedMaps";
	private static String POINT_TABLE_NAME = "Points";
	private static String EDGE_TABLE_NAME = "WeightedEdges";

	private static String MAP_SCHEMA = "id INTEGER, name VARCHAR(30), xTopLeft DOUBLE, yTopLeft DOUBLE, "
			+ " xBotRight DOUBLE, yBotRight DOUBLE, rotation DOUBLE, pointIDIndex INTEGER";
	private static String POINT_SCHEMA = "id VARCHAR(30), mapId INTEGER, name VARCHAR(30), localIndex INTEGER, "
			+ "locX INTEGER, locY INTEGER, globX INTEGER, globY INTEGER, numEdges INTEGER, idEdge1 VARCHAR(30),"
			+ " idEdge2 VARCHAR(30), idEdge3 VARCHAR(30), idEdge4 VARCHAR(30), idEdge5 VARCHAR(30), idEdge6 VARCHAR(30), idEdge7 VARCHAR(30), idEdge8 VARCHAR(30),"
			+ "idEdge9 VARCHAR(30), idEdge10 VARCHAR(30)";
	private static String EDGE_SCHEMA = "id VARCHAR(30), idPoint1 VARCHAR(30), idPoint2 VARCHAR(30), weight INTEGER, isOutside BOOLEAN, isStairs INTEGER";
	//-------------------------------------------------------------Variables-----------------------------------------------------------------------
	private static Connection conn = null;
	public final static boolean DEBUG = true;

	private static ArrayList<Map> allMaps = new ArrayList<Map>();
	private static ArrayList<Point> allPoints = new ArrayList<Point>();
	private static ArrayList<Edge> allEdges = new ArrayList<Edge>();
	
	//--------------------------------------------------------Singleton Handling--------------------------------------------------------------
	private static ServerDB instance;	
	private ServerDB()
	{
			
	}
	
	public static ServerDB getInstance()
	{
		if (instance == null)
			instance = new ServerDB();
		tryCreateDB();
		return instance;
	}

	//-------------------------------------------------------------Functions--------------------------------------------------------------------

	public static void main (String args[])
	{
		clearDatabase();
		//tryCreateDB();
		conn = connect();
		//testDB();
		System.out.println("Done testing");
	}

	public static void tryCreateDB()
	{
		Statement statement;
		try {
			conn = DriverManager.getConnection(DATABASE_URL, userName, password);
			conn.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS "+DATABASE_NAME);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		conn = connect();
		try {
			statement = conn.createStatement();
			statement.setQueryTimeout(5); 
			System.out.println("Creating new tables");
			statement.executeUpdate("create table if not exists "+ MAP_TABLE_NAME +" ("+ MAP_SCHEMA + ")");
			statement.executeUpdate("create table if not exists "+ EDGE_TABLE_NAME +" ("+ EDGE_SCHEMA + ")");
			System.out.println("Finished ensuring existence of table(s):"+EDGE_TABLE_NAME + ", "+MAP_TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection connect()
	{
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DATABASE_URL + DATABASE_NAME, userName, password);
		} catch (SQLException e) {
			System.out.println("Failed to get connection to amazon server");
			e.printStackTrace();
		}
		return connection;
	}

	//---------------------------------------------------------Insert functions------------------------------------------------------------

	public static void insertMap(Map map) throws AlreadyExistsException, SQLException
	{
		int mapId = map.getMapId();
		String RELEVANT_TABLE_NAME = "";
		RELEVANT_TABLE_NAME += ("Map"+map.getMapId()+"Points");
		//--------------------------------------------------Add to Database-----------------------------------------------------------------

		try {
			conn = connect();
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30); 


			statement.executeUpdate("create table if not exists "+ 								//If does not exist, then create table
					RELEVANT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");

			ResultSet rs = conn.createStatement().executeQuery("SELECT * from "+MAP_TABLE_NAME);
			while (rs.next())
			{
				int comparisonId = rs.getInt("id");
				if (comparisonId == mapId)
				{
					throw new AlreadyExistsException("Map with same id exists already in MySQL DB");
				}
			}
			rs.close();
			//----------------------------------------------Build insert statement---------------------------------------------------------
			String insertStatement = "insert into " +MAP_TABLE_NAME+" values(";
			insertStatement += mapId;
			insertStatement += ", ";
			insertStatement += ("'"+map.getMapName()+"'");
			insertStatement += ", ";
			insertStatement += map.getxTopLeft();
			insertStatement += ", ";
			insertStatement += map.getyTopLeft();
			insertStatement += ", ";
			insertStatement += map.getxBotRight();
			insertStatement += ", ";
			insertStatement += map.getyBotRight();
			insertStatement += ", ";
			insertStatement += map.getRotationAngle();
			insertStatement += ", ";
			insertStatement += map.getPointIDIndex();
			insertStatement += ")";
			statement.executeUpdate(insertStatement);
			//---------------------------------------------Finish inserting-------------------------------------------------------------------
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.close();
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
				if (allMaps.get(counter).getMapId() == mapId)
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
			}																			//End point add loop
		}																				//End null pointlist check
	}																					//End insertMap

	public static void insertPoint(Map map, Point pt) throws AlreadyExistsException, NoMapException, InsertFailureException, SQLException
	{
		String RELEVANT_TABLE_NAME = "";

		int counter = 0;
		String pointID = pt.getId();
		String ptName = pt.getName();
		int ptX = pt.getLocX();
		int ptY = pt.getLocY();
		ArrayList<Edge> edgeArray = pt.getEdges();
		int numberEdges = pt.getNumEdges();
		pt.setMapId(map.getMapId());

		RELEVANT_TABLE_NAME += ("Map"+map.getMapId()+"Points");

		/*
		int index = getNewPointIndex(map);
		pt.setIndex(index);*/
		{
			//--------------------------------------------------Add to Database--------------------------------------------------------
			try {
				boolean alreadyExists = false;
				conn = connect();
				ResultSet rs = null;
				Statement statement = conn.createStatement();
				statement.setQueryTimeout(30);  											// set timeout to 30 sec.

				statement.executeUpdate("create table if not exists "+ 						//If does not exist, then create table
						RELEVANT_TABLE_NAME +" ("+ POINT_SCHEMA + ")");

				rs = statement.executeQuery("SELECT * from "+RELEVANT_TABLE_NAME);			//Get all points from table (Currently only assumes map-level point id uniqueness)
				while (rs.next())
				{
					String comparisonId = rs.getString("id");								
					if (comparisonId.contentEquals(pt.getId()))								//Iterate through making sure that this id does not already exist
					{
						throw new AlreadyExistsException("Point with same id exists already in MySQL DB");
					}
				}
				
				rs.close();																	//Close resultSet to prevent errors
				//TODO Take care of point having a mapId attribute
				//----------------------------------------------Build insert statement---------------------------------------------------
				String insertStatement = "insert into " +RELEVANT_TABLE_NAME+" values(";	//Start building MySQL insert statement
				insertStatement += ("'"+pointID+"'");													
				insertStatement += ", ";													//Add commas for formatting reasons
				insertStatement += pt.getMapId();
				insertStatement += ", ";
				insertStatement += ("'"+ptName+"'");										
				insertStatement += ", ";
				insertStatement += pt.getIndex();
				insertStatement += ", ";
				insertStatement += pt.getLocX();
				insertStatement += ", ";
				insertStatement += pt.getLocY();
				insertStatement += ", ";
				insertStatement += pt.getGlobX();
				insertStatement += ", ";
				insertStatement += pt.getGlobY();
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
				throw new InsertFailureException("Failed to add to MySQL database");
			}
			finally
			{
				conn.close();
			}
			//-------------------------------------------------Add to local object storage-------------------------------------------------
			//-------------------------------------------------------Add to Map object-----------------------------------------------------
			int mapId = map.getMapId();
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
					if (allMaps.get(counter).getMapId() == mapId)
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
					boolean addRes = mapToAddTo.addPoint(pt);										//Add point to map object
					if (addRes == false)
						throw new InsertFailureException("Failed to add to object database");
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
					if (allPoints.get(counter).getId().contentEquals(pt.getId()))
						idExists = true;
				}
				if (idExists)
				{
					throw new AlreadyExistsException("Point id is already in object database (allPoints)");
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
			conn = connect();
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30);  												// set timeout to 30 sec.


			ResultSet rs = statement.executeQuery("SELECT * from "+EDGE_TABLE_NAME+			//Query to see if there are any edges with matching id's
					" WHERE id = '"+edge.getID()+"'");
			if (rs.next())
				throw new AlreadyExistsException("Edge with same id exists already in MySQL DB");
			rs.close();

			//----------------------------------------------Build insert statement---------------------------------------------------
			String insertStatement = "insert into " +EDGE_TABLE_NAME+" values(";
			insertStatement += ("'"+edgeId+"'");
			insertStatement += ", ";
			insertStatement += ("'"+point1.getId()+"'");
			insertStatement += ", ";
			insertStatement += ("'"+point2.getId()+"'");
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
			throw new InsertFailureException("Failed to add to MySQL database");
		}
		finally
		{
			conn.close();
		}
		//-------------------------------------------------Add to local object storage----------------------------------------
		boolean alreadyExists = false;
		if (allEdges == null || allMaps.isEmpty())
		{
			alreadyExists = false;
		}
		else																				//If there are edges we need to look through them
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

	//---------------------------------------------------------Modifying Functions-------------------------------------------------------=---

	private static void updatePoint (Point point) throws SQLException, DoesNotExistException
	{
		String ptId = point.getId();
		//---------------------------------------------------Update point in database-------------------------------------------------
		conn = connect();
		Statement statement = conn.createStatement();
		statement.setQueryTimeout(5);  											// set timeout to 30 sec.
		String tableName = "Map"+point.getMapId()+"Points";
		System.out.println("point mapId: "+point.getMapId());
		boolean found = false;
		ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM "+tableName);
		while (rs2.next())
		{
			if (rs2.getString("id").contentEquals(ptId))
			{
				found = true;
				String updateStatement = ("UPDATE "+tableName+" SET ");
				updateStatement += ("name ="+"'"+point.getName()+"'");										
				updateStatement += ", ";
				updateStatement += ("localIndex = "+ point.getIndex());
				updateStatement += ", ";
				updateStatement += ("locX = "+ point.getLocX());
				updateStatement += ", ";
				updateStatement += ("locY = "+point.getLocY());
				updateStatement += ", ";
				updateStatement += ("globX = "+ point.getGlobX());
				updateStatement += ", ";
				updateStatement += ("globY = "+ point.getGlobY());
				updateStatement += ", ";
				updateStatement += ("numEdges = "+point.getNumEdges());
				updateStatement += ", ";
				int i = 0;
				for (i =0; i < point.getNumEdges(); i++)								//Add number of edges edges to the point
				{
					updateStatement += ("idEdge"+(i+1)+" = "
							+"'"+point.getEdges().get(i).getID()+"'");
					updateStatement += ", ";
				}
				for (i = point.getNumEdges(); i < 9; i++)							//Add null padding so there are enough arguments in the insert statement
				{
					updateStatement += ("idEdge"+(i+1)+" = "+"null");
					updateStatement += ", ";
				}
				updateStatement += ("idEdge"+(i+1)+" = "+"null");						//Formatting, last value must not have comma after it
				updateStatement +=(" WHERE ID = '"+ptId+"'");
				statement.executeUpdate(updateStatement);								//Insert data
				if (DEBUG)
					System.out.println("Sucessfully updated point:"+ptId+" in database");
			}
		}
		rs2.close();
		conn.close();
		if (found == false)
		{
			throw new DoesNotExistException("Could not find point in database"); 
		}
		
		//Do not need to update edges in DB because we are assuming that point ID's never change
		
		//---------------------------------------------------Update local object----------------------------------------------------
		int j = 0;
		boolean foundObject = false;
		for (j = 0; j < allPoints.size(); j++)
		{
			if (allPoints.get(j).getId().contentEquals(ptId))
			{
				foundObject = true;
				allPoints.set(j, point);
			}
		}
		if (foundObject == false)
		{
			throw new DoesNotExistException("Could not find point object in local object storage");
		}
	}

	public static void removePoint (Point pt) throws DoesNotExistException
	{
		//------------------------------------------------------Remove Edges from DB---------------------------------------------------------
		
		ArrayList<Edge> edges = pt.getEdges();
		int j =0;
		for (j = edges.size(); j > 0; j--)
		{
			try {
				System.out.println("REMOVING EDGE:"+edges.get(j-1).getID());
				removeEdge(edges.get(j-1));
			} catch (DoesNotExistException e) {
				e.printStackTrace();
			}
		}
		
		edges = pt.getEdges();
		System.out.println("Number of edges after retrieval:"+edges.size());
		
		//------------------------------------------------------Remove point from DB----------------------------------------------------------
		String ptId = pt.getId();
		conn = connect();
		boolean found = false;
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.setQueryTimeout(5);  											// set timeout to 30 sec.
			String tableName = "Map"+pt.getMapId()+"Points";
			
			ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM "+tableName);
			while (rs2.next())
			{
				if (rs2.getString("id").contentEquals(ptId))
				{
					found = true;
					String deleteStatement = ("DELETE FROM "+tableName+" WHERE id = '"+ptId+"'");
					int result = statement.executeUpdate(deleteStatement);
					break;
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (found == false)
		{
			throw new DoesNotExistException("Could not find point in database");
		}
		//------------------------------------------------------Refresh local objects---------------------------------------------------------
		try {
			populateFromDatabase();
		} catch (PopulateErrorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeEdge(Edge edge) throws DoesNotExistException
	{
		Point p1 = edge.getPoint1();
		Point p2 = edge.getPoint2();
		
		String ptId = p1.getId();
		
		//-------------------------------------------------Remove edge id from Points in DB----------------------------------------------------
		boolean found1 = false;
		boolean found2 = false;
		int j = 0;
		ArrayList<Edge> edges = p1.getEdges();														//This will be used as a local list of edges that we can modify and add back to the point
		for (j = 0; j< edges.size(); j++)
		{
			if (edges.get(j).getId().contentEquals(edge.getId()))
			{
				found1 = true;
				edges.remove(j);
				p1.setEdges(edges);
				break;
			}
		}
		if (found1 == false)																			//Make sure that edge was successfully found
		{
			throw new DoesNotExistException("Could not find edge in first point");
		} else																						//If so, update the point information in the database
		{
			try {
				updatePoint(p1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		edges = p2.getEdges();
		for (j = 0; j< edges.size(); j++)
		{
			if (edges.get(j).getId().contentEquals(edge.getId()))
			{
				found1 = true;
				edges.remove(j);
				p2.setEdges(edges);
				break;
			}
		}
		if (found2 == false)																			//Make sure that the edge was successfully found
		{
			throw new DoesNotExistException("Could not find edge in second point");
		} else																						//If so, update the point information in the database
		{
			try {
				updatePoint(p2);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//--------------------------------------------------------Remove edge from DB--------------------------------------------------------
		boolean found3 = false;
		conn = connect();
		Statement statement;
		try {
			statement = conn.createStatement();
			statement.setQueryTimeout(5);  											// set timeout to 30 sec.

			ResultSet rs1 = conn.createStatement().executeQuery("SELECT table_name FROM information_schema.tables WHERE table_name = '"+EDGE_TABLE_NAME+"'");
			while (rs1.next())
			{
				if (rs1.getString("table_name").contentEquals(EDGE_TABLE_NAME))
				{
					String tableName = rs1.getString("table_name");
					ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM "+tableName);
					while (rs2.next())
					{
						if (rs2.getString("id").contentEquals(edge.getID()))
						{
							found3 = true;
							String deleteStatement = ("DELETE FROM "+tableName+" WHERE id = '"+edge.getID()+"'");
							int x = conn.createStatement().executeUpdate(deleteStatement);
							break;
						}
					}
					if (found3 == false)
					{
						throw new DoesNotExistException("Could not find edge in table:"+tableName);
					}
				}
			}
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		//--------------------------------------------------------Refresh local objects-------------------------------------------------------
		try {
			populateFromDatabase();
		} catch (PopulateErrorException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void clearDatabase()
	{
		try {
			conn = connect();
			Statement statement = conn.createStatement();
			statement.executeUpdate("DROP DATABASE "+DATABASE_NAME);
			//conn.createStatement().executeUpdate("FLUSH HOSTS");
			tryCreateDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//----------------------------------------------------------Retrieval Functions------------------------------------------------------------

	public static ArrayList<Map> getMapsFromLocal()
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
				tempMap.setPointList(getPointsFromServer(tempMap));
			} catch (PopulateErrorException e) {
				System.out.println("Failed to get points for map:"+allMaps.get(counter).getMapId());
				e.printStackTrace();
			}
			allMaps.set(counter, tempMap);
		}
		return allMaps;
	}

	private static Point getPointFromLocal(String pointId) throws DoesNotExistException
	{
		int counter = 0;
		Point retPt = null;
		boolean found = false;
		for (counter = 0; counter<allPoints.size(); counter++)
		{
			if (allPoints.get(counter).getId().contentEquals(pointId))
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
			printObjects(true, true, true);
			throw new DoesNotExistException("Point does not exist in object database");
		}
	}

	public static ArrayList<Point> getPointsFromServer(Map map) throws PopulateErrorException
	{
		if (DEBUG)
			System.out.println("getPointsFromServer called");
		ArrayList<Point> retArray = new ArrayList<Point>();
		try {
			conn = connect();
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30);  											// set timeout to 30 sec.
			String TABLE_NAME = "";
			TABLE_NAME += ("Map"+map.getMapId()+"Points");

			String newPtId;
			int newPtMapId;
			String newPtName;
			int newPtIndex;
			int newPtLocX;
			int newPtLocY;
			int newPtGlobX;
			int newPtGlobY;
			int newPtNumberEdges;
			ArrayList<Edge> newPtEdges = new ArrayList<Edge>();

			if (DEBUG)
				System.out.println("Populating from database");
			populateFromDatabase();
			ResultSet rs = statement.executeQuery("SELECT * FROM "+TABLE_NAME);
			while (rs.next())
			{
				newPtId = rs.getString("id");
				newPtMapId = rs.getInt("mapId");
				newPtName = rs.getString("name");
				newPtIndex = rs.getInt("localIndex");
				newPtLocX = rs.getInt("locX");
				newPtLocY = rs.getInt("locY");
				newPtGlobX = rs.getInt("globX");
				newPtGlobY = rs.getInt("globY");
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
					if (DEBUG)
						System.out.println("Searching for edge:"+edgeId);
					for (edgeCounter = 0; edgeCounter<allEdges.size(); edgeCounter++)
					{
						if (allEdges.get(edgeCounter).getID().contentEquals(edgeId))
						{
							foundEdge = true;
							newPtEdges.add(allEdges.get(edgeCounter));
							if (DEBUG)
								System.out.println("found:"+allEdges.get(edgeCounter).getId());
						}
					}

					if (foundEdge == false)
					{			
						throw new PopulateErrorException("Couldn't find edgeId in allEdges");
					}
				}

				Point newPt = new Point(newPtId, newPtName, newPtLocX, newPtLocY, newPtGlobX, newPtGlobY, newPtNumberEdges);
				newPt.setIndex(newPtIndex);
				newPt.setEdges(newPtEdges);
				newPt.setMapId(newPtMapId);
				retArray.add(newPt);
			}
			rs.close();
			if (DEBUG)
				System.out.println("Finished getting points");
			return retArray;																			//TODO add error handling
		} catch (SQLException e) {
			e.printStackTrace();
			return retArray;
		}
	}

	private static void populateFromDatabase() throws PopulateErrorException, SQLException
	{
		allMaps.clear();
		allPoints.clear();
		allEdges.clear();

		try {
			String newPtId;
			int newPtMapId;
			String newPtName;
			int newPtIndex;
			int newPtLocX;
			int newPtLocY;
			int newPtGlobX;
			int newPtGlobY;
			int newPtNumberEdges;

			String newEdgeId;
			String newEdgePt1;
			String newEdgePt2;
			int newEdgeWeight;
			int newEdgeOutside;
			int newEdgeStairs;

			ArrayList<Edge> newPtEdges = new ArrayList<Edge>();

			conn = connect();
			ResultSet rs1 = conn.createStatement().executeQuery("SELECT table_name FROM information_schema.tables");
			ResultSet rs2;
			ResultSet rs3;
			ResultSet rs4;

			ArrayList<String> tableNames = new ArrayList<String>();

			while (rs1.next())
			{
				if (rs1.getString("table_name").toLowerCase().contains("Points".toLowerCase()) ||
						rs1.getString("table_name").toLowerCase().contains("Edges".toLowerCase())||
						rs1.getString("table_name").toLowerCase().contentEquals(MAP_TABLE_NAME.toLowerCase()))
					tableNames.add(rs1.getString("table_name"));
			}
			Collections.sort(tableNames);
			int nameCounter = 0;
			rs4 = conn.createStatement().executeQuery("SELECT table_name FROM information_schema.tables");
			for (nameCounter = 0; nameCounter < tableNames.size(); nameCounter++)
			{
				//----------------------------------------Populate Map--------------------------------------------
				if (tableNames.get(nameCounter).toLowerCase().contentEquals(MAP_TABLE_NAME.toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+tableNames.get(nameCounter)+" which is at row: " + nameCounter);

					rs4 = conn.createStatement().executeQuery("SELECT * FROM "+tableNames.get(nameCounter));
					while (rs4.next())
					{
						int newMapId = rs4.getInt("id");
						String newMapName = rs4.getString("name");
						double newXTopLeft = rs4.getDouble("xTopLeft");
						double newYTopLeft = rs4.getDouble("yTopLeft");
						double newXBotRight = rs4.getDouble("xBotRight");
						double newYBotRight = rs4.getDouble("yBotRight");
						double newRotationAngle = rs4.getDouble("rotation");
						int newPointIDIndex = rs4.getInt("pointIDIndex");
						Map newMap = new Map(newMapId, newMapName, newXTopLeft, newYTopLeft, newXBotRight, newYBotRight,
								newRotationAngle, newPointIDIndex);
						allMaps.add(newMap);
					}
					rs4.close();
				}
				//------------------------------------------Populate Points-------------------------------------------
				else if (tableNames.get(nameCounter).toLowerCase().contains("Points".toLowerCase()))
				{
					String tableName = tableNames.get(nameCounter);
					if (DEBUG)
						System.out.println("Populating from table: "+tableNames.get(nameCounter)+" which is at row: " + nameCounter);
					int mapID = Integer.parseInt(tableName.substring(3, 4));															//Gets the mapId
					int j = 0;
					Map currentMap = null;
					for (j = 0; j < allMaps.size(); j++)
					{
						if (allMaps.get(j).getMapId() == mapID)
						{
							currentMap = allMaps.get(j);
						}
					}
					if (currentMap == null)
					{
						throw new PopulateErrorException("Couldn't find map object to add point to");
					}
					
					rs2 = conn.createStatement().executeQuery("SELECT * FROM "+tableNames.get(nameCounter));
					while(rs2.next())
					{
						newPtId = rs2.getString("id");
						newPtMapId = rs2.getInt("mapId");
						newPtName = rs2.getString("name");
						newPtIndex = rs2.getInt("localIndex");
						newPtLocX = rs2.getInt("locX");
						newPtLocY = rs2.getInt("locY");
						newPtGlobX = rs2.getInt("globX");
						newPtGlobY = rs2.getInt("globY");
						newPtNumberEdges = 0;															//This should be automatically rectified when adding in edges
						Point newPt = new Point(newPtId, newPtName, newPtLocX, newPtLocY, newPtGlobX, newPtGlobY, newPtNumberEdges);
						newPt.setIndex(newPtIndex);
						newPt.setMapId(newPtMapId);
						currentMap.addPoint(newPt);
						allPoints.add(newPt);
					}
					rs2.close();
				}
				//------------------------------------------Populate Edges-------------------------------------------
				else if (tableNames.get(nameCounter).toLowerCase().contains("Edges".toLowerCase()))
				{
					if (DEBUG)
						System.out.println("Populating from table: "+tableNames.get(nameCounter)+" which is at row: " + nameCounter);
					rs3 = conn.createStatement().executeQuery("SELECT * FROM "+tableNames.get(nameCounter));
					while(rs3.next())
					{
						newEdgeId = rs3.getString("id");
						newEdgePt1 = rs3.getString("idPoint1");
						Point pt1 = new Point();
						Point pt2 = new Point();
						try {
							pt1 = getPointFromLocal(newEdgePt1);
						} catch (DoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						newEdgePt2 = rs3.getString("idPoint2");
						try {
							pt2 = getPointFromLocal(newEdgePt2);
						} catch (DoesNotExistException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						newEdgeWeight = rs3.getInt("weight");
						newEdgeOutside = rs3.getInt("isOutside");
						newEdgeStairs = rs3.getInt("isStairs");
						boolean out = (newEdgeOutside==1);
						boolean stairs = (newEdgeStairs == 1);

						Edge newEdge = new Edge(pt1, pt2, newEdgeWeight, out, stairs);						//Automatically adds to points
						allEdges.add(newEdge);
					}
					rs3.close();
				}
				else
				{
					System.out.println("Couldn't resolve table name");
					throw new PopulateErrorException("Invalid table type. Can't resolve name:"+rs1.getString("table_name"));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			conn.close();
		}
	}

	//-----------------------------------------------------------Helper Functions--------------------------------------------------------------

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
				if (pointList.get(counter).getId().contentEquals(pt.getId()))
					return true;
			}
			return false;
		}
	}

	public static void printTableNames() throws SQLException
	{
		ResultSet rs = null;
		String selectTableNames = "SELECT table_name FROM information_schema.tables WHERE table_schema='"+DATABASE_NAME+"'";
		conn = connect();
		try {
			rs = conn.createStatement().executeQuery(selectTableNames);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		while (rs.next())
		{
			System.out.println(rs.getString(1));
		}
	}

	public static void printDatabase(boolean printMaps, boolean printPoints, boolean printEdges) throws SQLException
	{
		try
		{
			conn = connect();
			Statement statement = conn.createStatement();
			ResultSet rs1 = statement.executeQuery("SELECT table_name FROM information_schema.tables");
			if (DEBUG)
				System.out.println("Retrieved Tables");
			while (rs1.next())
			{
				String tableName = rs1.getString("table_name");
				//System.out.println("Looking at table "+tableName);
				if (tableName.contentEquals(MAP_TABLE_NAME))
				{
					if (printMaps)
					{
						System.out.println("--------------------Printing Maps--------------------");
						ResultSet rs = conn.createStatement().executeQuery("select * from "+MAP_TABLE_NAME);
						System.out.println("Printing "+tableName+" from "+DATABASE_NAME);
						while(rs.next())															// read the result set
						{
							System.out.println("MapID:"+rs.getInt("id"));
							System.out.println("Map name:"+rs.getString("name"));
						}
						System.out.println("Finished printing "+tableName);
						rs.close();
					}
				}
				else if (tableName.contains("Points"))
				{
					if (printPoints)
					{
						ResultSet rs = conn.createStatement().executeQuery("select * from "+tableName);
						System.out.println("--------------------Printing "+POINT_TABLE_NAME+" from "+tableName+"--------------------");
						while(rs.next())															// read the result set
						{
							System.out.println("name = " + rs.getString("name"));
							System.out.println("id = " + rs.getString("id"));
							System.out.println("index = " + rs.getInt("localIndex"));
							System.out.print("LocalX = " + rs.getInt("locX"));
							System.out.println("LocalY = " + rs.getInt("locY"));
							System.out.println("numEdges = "+rs.getInt("numEdges"));
						}
						rs.close();
					}
				}
				else if (tableName.contains("Edges"))
				{
					if (printEdges)
					{
						System.out.println("--------------------Printing Edges--------------------");
						ResultSet rs = conn.createStatement().executeQuery("select * from "+tableName);
						System.out.println("Printing "+EDGE_TABLE_NAME+" from "+DATABASE_NAME);
						while(rs.next())															// read the result set
						{
							System.out.println("id = "+rs.getString("id"));
						}
						rs.close();
					}
				}
			}
			System.out.println("--------------------Done printing tables--------------------");
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());												// if the error message is "out of memory", 
			// it probably means no database file is found
		}
		finally
		{
			conn.close();
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
				System.out.println("Map"+allMaps.get(counter).getMapId());
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

	public static class pointComparator implements Comparator<Point>
	{
		@Override
		public int compare (Point p1, Point p2)
		{
			if (p1.getIndex() > p2.getIndex() )
				return 1;
			else if (p2.getIndex() < p2.getIndex())
				return -1;
			return 0;
		}
	}
	
	//---------------------------------------------------------------Test Functions----------------------------------------------------------------
	
	public static void testDB()
	{
		clearDatabase();
		System.out.println("Database cleared");
		ArrayList<Point> insertablePoints = new ArrayList<Point>();
		ArrayList<Edge> insertableEdges = new ArrayList<Edge>();
		Map testMap = new Map(1, "testMap", 12, 20, 45, 60, 12.6, 0);
		Point p1 = new Point(testMap.getNewPointID(), "p1", testMap.getPointIDIndex(), 0, 1, 0);
		Point p2 = new Point(testMap.getNewPointID(), "p2", testMap.getPointIDIndex(), 0, 1, 0);
		Point p3 = new Point(testMap.getNewPointID(), "p3", testMap.getPointIDIndex(), 1, 0, 0);
		Point p4 = new Point(testMap.getNewPointID(), "p4", testMap.getPointIDIndex(), 2, 0, 0);
		Point p5 = new Point(testMap.getNewPointID(), "p5", testMap.getPointIDIndex(), 23, 90, 0);
		Point p6 = new Point(testMap.getNewPointID(), "p6", testMap.getPointIDIndex(), 27, 90, 0);
		Edge e1 = new Edge(p1, p2, 1);
		Edge e2 = new Edge(p2, p5, 1);
		Edge e3 = new Edge(p2, p3, 1);
		Edge e4 = new Edge(p3, p4, 1);
		Edge e5 = new Edge(p3, p5, 1);
		Edge e6 = new Edge(p4, p5, 1);
		Edge[] emptyArray = null;
		//--------------------------------------------------Add points to insert arraylist--------------------------------
		insertablePoints.add(p1);
		insertablePoints.add(p2);
		insertablePoints.add(p3);
		insertablePoints.add(p4);
		insertablePoints.add(p5);
		insertableEdges.add(e1);
		insertableEdges.add(e2);
		insertableEdges.add(e3);
		insertableEdges.add(e4);
		insertableEdges.add(e5);
		insertableEdges.add(e6);
		int counter = 0;

		//------------------------------------------------Populate Object storage from database-----------------------------
		System.out.println("Printing database");
		try {
			printDatabase(true, true, true);
		} catch (SQLException e7) {
			e7.printStackTrace();
		}
		System.out.println("Finished printing database");
		//-------------------------------------------------------Test map insert--------------------------------------------
		System.out.println("Testing map insert");
		try {																																						
			insertMap(testMap);
			printDatabase(true, true, true);
		} catch (AlreadyExistsException e) {
			System.out.println("Map already exists in Database");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Finished testing map insert");
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
		//----------------------------------------------------Test edge insert----------------------------------------------
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
		//----------------------------------------------See results of insertions-----------------------------------------
		System.out.println("Testing results of inserts");
		ArrayList<Point> testArray = new ArrayList<Point>();
		try {
			testArray = getPointsFromServer(testMap);
		} catch (PopulateErrorException e) {
			e.printStackTrace();
		}

		int count = 0;
		System.out.println("Printing points found in map: "+testMap.getMapId());
		for (count = 0; count<testArray.size(); count++)
		{
			Point tempPt = testArray.get(count);
			tempPt.print();
		}

		//---------------------------------------------------Test removal------------------------------------------------
		System.out.println("--------------------Testing edge removal--------------------");
		System.out.println("Database before removing edge e1");
		try {
			printDatabase(false, true, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("----------Removing edge e1----------");
		try {
			removeEdge(e1);
		} catch (DoesNotExistException e) {
			e.printStackTrace();
		}
		System.out.println("Database after removing e1");
		try {
			printDatabase(true, true, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("--------------------Finished edge removal--------------------");
		System.out.println("--------------------Testing Point removal--------------------");
		System.out.println("Database before removing point p2");
		try {
			printDatabase(false, true, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("----------Removing point p2----------");
		try {
			removePoint(p2);
		} catch (DoesNotExistException e7) {
			e7.printStackTrace();
		}
		System.out.println("Database after removing p2");
		try {
			printDatabase(true, true, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("--------------------Finished Point removal--------------------");
	}

	public static void testRetrieval()
	{
		System.out.println("--------------------Testing Retrieval--------------------");
		ArrayList<Map> retrievedMaps = getMapsFromLocal();
		int j = 0;
		for (j = 0; j<retrievedMaps.size(); j++)
		{
			retrievedMaps.get(j).printMap();
		}
		System.out.println("--------------------Finished Testing Retrieval--------------------");
	}

	public static void testRemoval()
	{
		
	}
}
