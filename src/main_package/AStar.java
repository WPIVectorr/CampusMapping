package main_package;

import java.util.HashMap;
import java.util.LinkedList;

public class AStar {
	LinkedList<Point> Open = new LinkedList<Point>(); // List of all Open nodes
	Point[] Closed = null; // Array of all closed Nodes
	HashMap<Point, Point> CameFrom = new HashMap<Point, Point>(); // Map to track path taken to each node
	HashMap<Point, Integer> gscore = new HashMap<Point, Integer>(); // Time taken to each node
	HashMap<Point, Integer> fscore = new HashMap<Point, Integer>(); // Estimated time from each node to end
	int Nc = 0; // Index for Closed
	
	public void PathFind(Point start, Point end) {		
		gscore.put(start, 0); // Initialize scores
		fscore.put(start, CostEstimate(start, end));
		
		Open.add(start); // Initialize Open
		
		while(!Open.isEmpty()){
			Point Current = Open.removeFirst(); // Working with lowest element in Open
			if(Current == end){
				//Reconstruct Path Here
				return; // Found the Exit, Need
			}
			Closed[Nc] = Current; // Add it to Closed
			Nc++;
			
			for(int i = 0; i < Current.edges.length; i++) {
				if(ClosedContains(Current.edges[i].a)){
					
				}
				else if(ClosedContains(Current.edges[i].b)){
					
				}
				else{
					int tentGScore = gscore.get(Current) + Current.edges[i].weight;
					if(Current.edges[i].a == Current){
						if(!Open.contains(Current.edges[i].b) || gscore.get(Current.edges[i].b) > tentGScore){
							if(!Open.contains(Current.edges[i].b)) {
								OpenAdd(Current.edges[i].b);
							}
							CameFrom.put(Current.edges[i].b, Current);
							gscore.put(Current.edges[i].b, tentGScore);
							fscore.put(Current.edges[i].b, tentGScore + CostEstimate(Current.edges[i].b, end));
						}
					}
					else if(Current.edges[i].b == Current){
						if(!Open.contains(Current.edges[i].a) || gscore.get(Current.edges[i].a) > tentGScore){
							if(!Open.contains(Current.edges[i].a)) {
								OpenAdd(Current.edges[i].a);
							}
							CameFrom.put(Current.edges[i].a, Current);
							gscore.put(Current.edges[i].a, tentGScore);
							fscore.put(Current.edges[i].a, tentGScore + CostEstimate(Current.edges[i].a, end));
						}
					}
				}
			}
		}
	}
	
	private int CostEstimate(Point a, Point b){
		return (int)Math.sqrt((double)((a.x+b.x)^2)+((a.y+b.y)^2));
	}
	private boolean ClosedContains(Point testPoint){
		for(int i = 0; i < Closed.length; i++){
			if(Closed[i] == testPoint){
				return true;
			}
		}
		return false;
	}
	private void OpenAdd(Point addPoint){
		for(int i = 0; i < Open.size(); i++){
			if(fscore.get(Open.get(i)) > fscore.get(addPoint)){
				Open.add(i, addPoint);
				return;
			}
		}
		Open.add(addPoint);
	}
}
