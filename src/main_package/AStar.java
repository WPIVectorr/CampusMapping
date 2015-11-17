package main_package;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class AStar {
	static LinkedList<Point> Open = new LinkedList<Point>(); // List of all Open nodes
	static ArrayList<Point> Closed = new ArrayList<Point>(); // Array of all closed Nodes
	static HashMap<Point, Point> CameFrom = new HashMap<Point, Point>(); // Map to track path taken to each node
	static HashMap<Point, Integer> gscore = new HashMap<Point, Integer>(); // Time taken to each node
	static HashMap<Point, Integer> fscore = new HashMap<Point, Integer>(); // Estimated time from each node to end
	
	public static ArrayList<Point> PathFind(Point start, Point end) {		
		gscore.put(start, 0); // Initialize scores
		fscore.put(start, CostEstimate(start, end));
		
		Open.add(start); // Initialize Open
		
		while(!Open.isEmpty()){
			Point Current = Open.removeFirst(); // Working with lowest element in Open
			if(Current == end){
				return ReconstructPath(end); // Found the Exit
			}
			Closed.add(Current); // Add it to Closed
			
			for(int i = 0; i < Current.getNumEdges(); i++) {
				if(Closed.contains(Current.getEdges().get(i).getPoint1()) && Current.getEdges().get(i).getPoint1() != Current){
					
				}
				else if(Closed.contains(Current.getEdges().get(i).getPoint2()) && Current.getEdges().get(i).getPoint2() != Current){
					
				}
				else{
					int tentGScore = gscore.get(Current) + Current.getEdges().get(i).getWeight();
					if(Current.getEdges().get(i).getPoint1() == Current){
						if(!Open.contains(Current.getEdges().get(i).getPoint2()) || gscore.get(Current.getEdges().get(i).getPoint2()) > tentGScore){
							CameFrom.put(Current.getEdges().get(i).getPoint2(), Current);
							gscore.put(Current.getEdges().get(i).getPoint2(), tentGScore);
							fscore.put(Current.getEdges().get(i).getPoint2(), tentGScore + CostEstimate(Current.getEdges().get(i).getPoint2(), end));
							if(!Open.contains(Current.getEdges().get(i).getPoint2())) {
								OpenAdd(Current.getEdges().get(i).getPoint2());
							}
						}
					}
					else if(Current.getEdges().get(i).getPoint2() == Current){
						if(!Open.contains(Current.getEdges().get(i).getPoint1()) || gscore.get(Current.getEdges().get(i).getPoint1()) > tentGScore){
							CameFrom.put(Current.getEdges().get(i).getPoint1(), Current);
							gscore.put(Current.getEdges().get(i).getPoint1(), tentGScore);
							fscore.put(Current.getEdges().get(i).getPoint1(), tentGScore + CostEstimate(Current.getEdges().get(i).getPoint1(), end));
							if(!Open.contains(Current.getEdges().get(i).getPoint1())) {
								OpenAdd(Current.getEdges().get(i).getPoint1());
							}
						}
					}
				}
			}
		}
		return null; // No Path Found
	}
	
	private static int CostEstimate(Point a, Point b){
		return (int)Math.sqrt((double)((a.getX()-b.getX())^2)+((a.getY()-b.getY())^2));
	}
	private static void OpenAdd(Point addPoint){
		for(int i = 0; i < Open.size(); i++){
			if(fscore.get(Open.get(i)) > fscore.get(addPoint)){
				Open.add(i, addPoint);
				return;
			}
		}
		Open.add(addPoint);
	}
	private static ArrayList<Point> ReconstructPath(Point PathEnd){
		Point Current = PathEnd;
		ArrayList<Point> ReturnPath = new ArrayList<Point>();
		while(CameFrom.containsKey(Current)){
			ReturnPath.add(Current);
			Current = CameFrom.get(Current);
		}
		ReturnPath.add(Current);
		return ReturnPath;
	}
	public static void reset(){
		Open.clear();
		Closed.clear();
		CameFrom.clear();
		fscore.clear();
		gscore.clear();
	}
}