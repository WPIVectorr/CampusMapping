The MappingDatabase uses a Singleton design pattern, meaning that in order to use it, all you need to do is call the following function

MappingDatabase.getInstance()

This returns a single instance of MappingDatabase which can be accessed from any class.
It may be necessary to refresh your copy of the database at times if another class is using the instance and modifies it. This will
require some testing, but as of right now shouldn't be an issue.

There are a number of functions that can be called on an instance of the database.

insertMap (Map map)
	This one does pretty much what the name says. It checks for existing maps and if none exist, adds the one specified.

insertPoint(Map map, Point pt)
	This one does what the name implies. It checks if there is already an existing point with the same id on the map and if so throws 
	an AlreadyExistsException. This is so that it can handled by the GUI

getPoints(Map map)
	This returns (in theory) an ArrayList of all the points on a given map. This will require some more extensive testing, I'm not entirely sure
	that it saves points between sessions right now.