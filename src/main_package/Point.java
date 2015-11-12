package main_package;

public class Point {
	public int x;
	public int y;
	public Edge[] edges;
	int N = 0;
	
	public Point(int px, int py) {
		x = px;
		y = py;
	}
	
	public void addEdge(Edge addMe){
		edges[N] = addMe;
		N++;
	}
}
