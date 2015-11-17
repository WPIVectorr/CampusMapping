package main_package;

import java.util.ArrayList;

public class Point {
	private int id;
	private String name;
	private int x;
	private int y;
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public Point(int id, String name, int x, int y) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
	}
	
	public int getId() {
		return id;
	}
	public String getName()
	{
		return this.name;
	}
	public void setName (String newName)
	{
		this.name = newName;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	public int getNumberEdges() {
		return edges.size();
	}
	public void addEdge(Edge addMe){
		edges.add(addMe);;
	}
	public int getNumEdges()
	{
		return edges.size();
	}
	public String toString(){
		return "(" + x + "," + y + ")";
	}
	public boolean equals(Point compPoint){
		return this.id == compPoint.getId();
	}
}
