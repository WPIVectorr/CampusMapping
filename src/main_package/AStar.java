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
	
	public static ArrayList<Point> PathFind(Point start, Point end, int outside, int stairs, ArrayList<Point> allPoints) {	
		
		System.out.println("outside: " + outside + " stairs: " + stairs);
		gscore.put(start.getId(), 0); // Initialize scores
		fscore.put(start.getId(), CostEstimate(start, end));
		
		Open.add(start); // Initialize Open
		while(!Open.isEmpty()){
			Point Current = Open.removeFirst(); // Working with lowest element in Open
			if(Current.equals(end)){
				return ReconstructPath(end); // Found the Exit
			}
			Closed.add(Current.getId()); // Add it to Closed
			
			for(int i = 0; i < Current.getEdges().size(); i++) { // Check the points on each edge
				
				boolean isOutside;
				boolean point1Outside = false;
				boolean point2Outside = false;
				boolean isStairs;
				boolean point1Stairs = false;
				boolean point2Stairs = false;
				/*for(int s = 0; s < allPoints.size(); s++){
					if(Current.getEdges().get(i).getPoint1().getId() == allPoints.get(s).getId()){
						point1Outside = allPoints.get(s).isOutside();
						point1Stairs = allPoints.get(s).isStairs();
					}
					if(Current.getEdges().get(i).getPoint2().getId() == allPoints.get(s).getId()){
						point2Outside = allPoints.get(s).isOutside();
						point2Stairs = allPoints.get(s).isStairs();
					}
				}*/
				isOutside = Current.getEdges().get(i).isOutside();
				isStairs = Current.getEdges().get(i).isStairs();
				if(isStairs){
					System.out.println("Point A is: " + Current.getEdges().get(i).getPoint1().getName() + " Point B is: " + Current.getEdges().get(i).getPoint2().getName());
				}
				if(Closed.contains(Current.getEdges().get(i).getPoint1().getId()) && !Current.getEdges().get(i).getPoint1().equals(Current)){
					//If point1 on this edge isnt this one and is already closed, dont do things
				}
				else if(Closed.contains(Current.getEdges().get(i).getPoint2().getId()) && !Current.getEdges().get(i).getPoint2().equals(Current)){
					//If point2 on this edge isnt this one and is already closed, dont do things
				} /*else if(isStairs && stairs == -1){
					
					if(Current.getEdges().get(i).getPoint1().getId() == Current.getId()){
						Closed.add(Current.getEdges().get(i).getPoint2().getId());
						System.out.println("Adding Something to Closed." + Current.getEdges().get(i).getPoint2().getId());
					} else {
						Closed.add(Current.getEdges().get(i).getPoint1().getId());
						System.out.println("Adding Something to Closed." + Current.getEdges().get(i).getPoint1().getId());
					}
				}*/
				else{ //This edge has a new point on it
					double prefOutsideScale = 1;
					double prefStairsScale = 1;
					if(isOutside){
						if(outside == -1){
							prefOutsideScale = 10.0;
						} else if (outside == 1){
							prefOutsideScale = .1;
						}
					}
					//checks if name contains stairs
					
					if(isStairs){
						if(stairs == -1){
							prefStairsScale = 1000.0;
						} else if(stairs == 1){
							prefStairsScale = -10;
						}
					}
					int ElevatorWeight = 1;
					String lowercaseElevator1 = Current.getEdges().get(i).getPoint1().getName().toLowerCase();
					String lowercaseElevator2 = Current.getEdges().get(i).getPoint2().getName().toLowerCase();
					if(lowercaseElevator1.contains("elevator") &&
							lowercaseElevator2.contains("elevator") &&
							stairs == 1){
						ElevatorWeight = 1000;
					}
					
					
					//System.out.println("Edge weight sample : " + Current.getEdges().get(i).getWeight());
					//System.out.println("prefOutsideScale: " + prefOutsideScale + " prefStairsScale: "+ prefStairsScale);
					int tentGScore = gscore.get(Current.getId()) + Math.abs((int)(Current.getEdges().get(i).getWeight() * prefStairsScale * prefOutsideScale * ElevatorWeight)); // How far we have gone to get to this point
					
					//System.out.println("testGSCore: "+ tentGScore + " Weight: " + Current.getEdges().get(i).getWeight() * prefStairsScale * prefOutsideScale);
					
					if(Current.getEdges().get(i).getPoint1().equals(Current)){ // Check which point is the new one
						// Point2 is the new point
						if(!Open.contains(Current.getEdges().get(i).getPoint2()) || gscore.get(Current.getEdges().get(i).getPoint2().getId()) > tentGScore){ // If the point is not on the open list OR this is a faster route to it
							CameFrom.put(Current.getEdges().get(i).getPoint2().getId(), Current); // Update how we got here
							gscore.put(Current.getEdges().get(i).getPoint2().getId(), tentGScore);// Update how far we have gone
							fscore.put(Current.getEdges().get(i).getPoint2().getId(), tentGScore + CostEstimate(Current.getEdges().get(i).getPoint2(), end)); // Update how far we will go to get to the end
							if(!Open.contains(Current.getEdges().get(i).getPoint2())) { // If it isnt on the open list
								OpenAdd(Current.getEdges().get(i).getPoint2());         // Add it to the open list
							}
						}
					}
					else if(Current.getEdges().get(i).getPoint2().equals(Current)){
						// Point1 is the new point
						if(!Open.contains(Current.getEdges().get(i).getPoint1()) || gscore.get(Current.getEdges().get(i).getPoint1().getId()) > tentGScore){ // If the point is not on the open list OR this is a faster route to it
							CameFrom.put(Current.getEdges().get(i).getPoint1().getId(), Current); // Update how we got here
							gscore.put(Current.getEdges().get(i).getPoint1().getId(), tentGScore);// Update how far we have gone
							fscore.put(Current.getEdges().get(i).getPoint1().getId(), tentGScore + CostEstimate(Current.getEdges().get(i).getPoint1(), end)); // Update how far we will go to get to the end
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
		return (int)Math.sqrt((double)(Math.pow((a.getGlobX()-b.getGlobX()),2)+Math.pow((a.getGlobY()-b.getGlobY()),2))); // Pythagorean theorum
	}
	private static void OpenAdd(Point addPoint){ // Add a point to the open list
		for(int i = 0; i < Open.size(); i++){ // Check for each point in open
			if(fscore.get(Open.get(i).getId()) > fscore.get(addPoint.getId())){ // If point at i in open has a larger fscore than the point being added
				Open.add(i, addPoint); // Insert the point at i
				return;
			}
		}
		Open.add(addPoint); // If it is the largest yet
	}
	private static ArrayList<Point> ReconstructPath(Point PathEnd){ // Recreate a path to the given point
		Point Current = PathEnd; // Initialize current
		ArrayList<Point> ReturnPath = new ArrayList<Point>();

		while(CameFrom.containsKey(Current.getId())){ // While the current point has one it came from
			ReturnPath.add(Current); // Add the current one
			Current = CameFrom.get(Current.getId()); // Find the next one
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