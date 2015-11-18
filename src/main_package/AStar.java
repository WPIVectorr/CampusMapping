package main_package;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class AStar {
	static LinkedList<Point> Open = new LinkedList<Point>(); // List of all Open nodes
	static ArrayList<String> Closed = new ArrayList<String>(); // Array of all closed Nodes
	static HashMap<String, Point> CameFrom = new HashMap<String, Point>(); // Map to track path taken to each node
	static HashMap<String, Integer> gscore = new HashMap<String, Integer>(); // Time taken to each node
	static HashMap<String, Integer> fscore = new HashMap<String, Integer>(); // Estimated time from each node to end
	
	public static ArrayList<Point> PathFind(Point start, Point end) {		
		gscore.put(Integer.toString(start.getId()), 0); // Initialize scores
		fscore.put(Integer.toString(start.getId()), CostEstimate(start, end));
		
		Open.add(start); // Initialize Open
		System.out.println("----------------------------open true in astar: " + !Open.isEmpty());
		while(!Open.isEmpty()){
			Point Current = Open.removeFirst(); // Working with lowest element in Open
			if(Current.equals(end)){
				return ReconstructPath(end); // Found the Exit
			}
			Closed.add(Integer.toString(Current.getId())); // Add it to Closed
			
			for(int i = 0; i < Current.getNumEdges(); i++) { // Check the points on each edge
				if(Closed.contains(Integer.toString(Current.getEdges().get(i).getPoint1().getId())) && !Current.getEdges().get(i).getPoint1().equals(Current)){
					//If point1 on this edge isnt this one and is already closed, dont do things
				}
				else if(Closed.contains(Integer.toString(Current.getEdges().get(i).getPoint2().getId())) && !Current.getEdges().get(i).getPoint2().equals(Current)){
					//If point2 on this edge isnt this one and is already closed, dont do things
				}
				else{ //This edge has a new point on it
					int tentGScore = gscore.get(Integer.toString(Current.getId())) + Current.getEdges().get(i).getWeight(); // How far we have gone to get to this point
					if(Current.getEdges().get(i).getPoint1().equals(Current)){ // Check which point is the new one
						// Point2 is the new point
						if(!Open.contains(Current.getEdges().get(i).getPoint2()) || gscore.get(Integer.toString(Current.getEdges().get(i).getPoint2().getId())) > tentGScore){ // If the point is not on the open list OR this is a faster route to it
							CameFrom.put(Integer.toString(Current.getEdges().get(i).getPoint2().getId()), Current); // Update how we got here
							gscore.put(Integer.toString(Current.getEdges().get(i).getPoint2().getId()), tentGScore);// Update how far we have gone
							fscore.put(Integer.toString(Current.getEdges().get(i).getPoint2().getId()), tentGScore + CostEstimate(Current.getEdges().get(i).getPoint2(), end)); // Update how far we will go to get to the end
							if(!Open.contains(Current.getEdges().get(i).getPoint2())) { // If it isnt on the open list
								OpenAdd(Current.getEdges().get(i).getPoint2());         // Add it to the open list
							}
						}
					}
					else if(Current.getEdges().get(i).getPoint2().equals(Current)){
						// Point1 is the new point
						if(!Open.contains(Current.getEdges().get(i).getPoint1()) || gscore.get(Integer.toString(Current.getEdges().get(i).getPoint1().getId())) > tentGScore){ // If the point is not on the open list OR this is a faster route to it
							CameFrom.put(Integer.toString(Current.getEdges().get(i).getPoint1().getId()), Current); // Update how we got here
							gscore.put(Integer.toString(Current.getEdges().get(i).getPoint1().getId()), tentGScore);// Update how far we have gone
							fscore.put(Integer.toString(Current.getEdges().get(i).getPoint1().getId()), tentGScore + CostEstimate(Current.getEdges().get(i).getPoint1(), end)); // Update how far we will go to get to the end
							if(!Open.contains(Current.getEdges().get(i).getPoint1())) { // If it isnt on the open list
								OpenAdd(Current.getEdges().get(i).getPoint1());         // Add it to the open list
							}
						}
					}
				}
			}
		}
		return null; // No Path Found
	}
	
	private static int CostEstimate(Point a, Point b){ // Find distance from point a to point b
		return (int)Math.sqrt((double)((a.getX()-b.getX())^2)+((a.getY()-b.getY())^2)); // Pythagorean theorum
	}
	private static void OpenAdd(Point addPoint){ // Add a point to the open list
		for(int i = 0; i < Open.size(); i++){ // Check for each point in open
			if(fscore.get(Integer.toString(Open.get(i).getId())) > fscore.get(Integer.toString(addPoint.getId()))){ // If point at i in open has a larger fscore than the point being added
				Open.add(i, addPoint); // Insert the point at i
				return;
			}
		}
		Open.add(addPoint); // If it is the largest yet
	}
	private static ArrayList<Point> ReconstructPath(Point PathEnd){ // Recreate a path to the given point
		Point Current = PathEnd; // Initialize current
		ArrayList<Point> ReturnPath = new ArrayList<Point>();

		while(CameFrom.containsKey(Integer.toString(Current.getId()))){ // While the current point has one it came from
			ReturnPath.add(Current); // Add the current one
			Current = CameFrom.get(Integer.toString(Current.getId())); // Find the next one
		}
		ReturnPath.add(Current); // Add the last one
		return ReturnPath;
	}
	public static void reset(){ // Reset to find a new path
		Open.clear();
		Closed.clear();
		CameFrom.clear();
		fscore.clear();
		gscore.clear();
	}
}