The MappingDatabase uses a Singleton design pattern, meaning that in order to use it, all you need to do is call the following function

MappingDatabase.getInstance()

This returns a single instance of MappingDatabase which can be accessed from any class.
It may be necessary to refresh your copy of the database at times if another class is using the instance and modifies it. This will
require some testing, but as of right now shouldn't be an issue.

---------------------------------------------------------------------IMPORTANT-------------------------------------------------------------------------
In terms of storage philosophy
-The database assumes that maps are constructed as simple objects with only an id and name. This is because I assumed that maps would initially be created 
	without any points. Maps must be inserted before anything else
-The database assumes that points are created before edges. This follows from edges requiring that points exist. Points must be inserted before edges.

SO: The order of insertion is : Maps -> Points -> Edges
---------------------------------------------------------------------------------------------------------------------------------------------------------

There are a number of functions that can be called on an instance of the database.

insertMap (Map map)
	This one does pretty much what the name says. It checks for existing maps and if none exist, adds the one specified.
	Maps are stored with their names and ids. Map points and edges are stored in a different table.

insertPoint(Map map, Point pt)
	This one does what the name implies. It checks if there is already an existing point with the same id on the map and if so throws 
	an AlreadyExistsException. This is so that it can handled by the GUI

insertEdge(Edge edge)
	Does what name implies. All you need to do is give it an edge. The function handles the rest.
	
getPoints(Map map)
	This returns (in theory) an ArrayList of all the points on a given map. This will require some more extensive testing, I'm not entirely sure
	that it saves points between sessions right now.
	
-----------------------------------------------------------------SAMPLE CODE-------------------------------------------------------------------------------
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
		Point testPoint1 = new Point (1, "One", 50, 100);
		Point testPoint2 = new Point (2, "Two", 600, 500);
		Point testPoint3 = new Point (3, "Three", 500, 700);
		Point testPoint4 = new Point (4, "Four", 200, 200);
		Point testPoint7 = new Point (7, "seven", 100, 500);
		Point testPoint5 = new Point (5, "Five", 500, 600);
		Point testPoint6 = new Point (6, "Six", 700, 500);
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
		System.out.println("First map name:" + maps.get(0).getName());
		for (z = 0; z<maps.get(0).getPointList().size(); z++)
		{
			maps.get(0).getPointList().get(z).print();
		}
----------------------------------------------------------------------------------------------------------------------------------------------------------