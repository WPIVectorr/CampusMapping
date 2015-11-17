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