package main_package;

import java.util.ArrayList;

public class Point {
	private int id;
	private String name;
	private int x;
	private int y;
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private int numberEdges;
	
	public Point()
	{
		
	}
	public Point(int id, String name, int x, int y) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.numberEdges = 0;
	}
	
	public Point(int id, String name, int x, int y, int numberEdges) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.numberEdges = numberEdges;
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
	//adds one edge
	public void addEdge(Edge addMe){
		edges.add(addMe);
		this.numberEdges++;
	}
	//deletes all edges
	public void deleteEdges(){
		edges.clear();
		this.numberEdges = 0;
	}
	public int getNumEdges()
	{
		return edges.size();
	}
	public String toString(){
		return name;
	}
	public void print()
	{
		System.out.println ("Name:"+this.name);
		System.out.println ("ID:"+this.id);
		System.out.println ("X:"+this.x);
		System.out.println ("Y:"+this.y);
		System.out.println ("NumEdges:"+this.numberEdges);
		int i = 0;
		for (i = 0; i<this.numberEdges; i++)
		{
			this.edges.get(i).print();
			//System.out.println("Edge"+(i+1)+"id:"+this.edges.get(i).getId());
		}
	}
}