package main_package;

import java.util.HashMap;
import java.util.LinkedList;

public class AStar {
	public void PathFind(Point start, Point end) {
		LinkedList<Point> Open = new LinkedList<Point>(); // List of all Open nodes
		Point[] Closed = null; // Array of all closed Nodes
		HashMap<Point, Point> CameFrom = new HashMap<Point, Point>(); // Map to track path taken to each node
		HashMap<Point, Integer> gscore = new HashMap<Point, Integer>(); // Time taken to each node
		HashMap<Point, Integer> fscore = new HashMap<Point, Integer>(); // Estimated time from each node to end
		int Nc = 0; // Index for Closed
		
		gscore.put(start, 0); // Initialize scores
		fscore.put(start, CostEstimate(start, end));
		
		Open.add(start); // Initialize Open
		
		while(!Open.isEmpty()){
			
		}
	}
	private int CostEstimate(Point a, Point b){
		return (int)Math.sqrt((double)((a.x+b.x)^2)+((a.y+b.y)^2));
	}
}
