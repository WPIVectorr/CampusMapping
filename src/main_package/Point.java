package main_package;

public class Point {
	private int id;
	private String name;
	private int x;
	private int y;
	private Edge[] edges;
	private int numberEdges = 0;
	
	public Point(int id, String name, int x, int y, Edge[] edges, int numberEdges) {
		this.id = id;
		this.name = name;
		this.x = x;
		this.y = y;
		this.edges = edges;
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
	public Edge[] getEdges() {
		return edges;
	}
	public void setEdges(Edge[] edges) {
		this.edges = edges;
	}
	public int getNumberEdges() {
		return numberEdges;
	}
	public void setNumberEdges(int numberEdges) {
		this.numberEdges = numberEdges;
	}
	public void addEdge(Edge addMe){
		edges[numberEdges] = addMe;
		numberEdges++;
	}
	public int getNumEdges()
	{
		return this.numberEdges;
	}
}
