package main_package;

public class Edge {
	public Point a;
	public Point b;
	public int weight;
	
	public Edge(Point ea, Point eb, int eweight){
		a = ea;
		b = eb;
		weight = eweight;
		a.addEdge(this);
		b.addEdge(this);
	}
}
