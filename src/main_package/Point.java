package main_package;

import java.util.ArrayList;

public class Point {
	private String id;
	private int mapId;
	private String name;
	private int index;
	private int locX;
	private int locY;
	private int globX;
	private int globY;
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private int numberEdges;
	
	public Point()
	{
		
	}
	//TODO UPdate Constructors
	public Point(String id, String name, int x, int y) {
		this.id = id;
		this.name = name;
		this.locX = x;
		this.locY = y;
		this.numberEdges = 0;
		this.index = 0;
		this.globX = x;
		this.globY = y;
	}
	
	public Point(String id, String name, int index, int x, int y) {
		this.id = id;
		this.name = name;
		this.locX = x;
		this.locY = y;
		this.numberEdges = 0;
		this.index = index;
		this.globX = x;
		this.globY = y;
	}
	

	public Point(String id, String name, int locX, int locY, int globX, int globY, int numberEdges) {
		this.id = id;
		this.name = name;
		this.locX = locX;
		this.locY = locY;
		this.numberEdges = numberEdges;
		this.index = 0;
		this.globX = globX;
		this.globY = globY;
	}
	
	public Point(String id, String name, int index, int x, int y, int numberEdges) {
		this.id = id;
		this.name = name;
		this.index = index;
		this.locX = x;
		this.locY = y;
		this.numberEdges = numberEdges;
		this.globX = x;
		this.globY = y;
	}
	
	public String getId() {
		return id;
	}
	
	public void setID(String x){
		int j = 0;
		int oldIdLength = this.id.length();
		for (j = 0; j < edges.size(); j++)
		{
			Edge currEdge = edges.get(j);
			if(currEdge.getPoint1() == this){
				currEdge.setID(x + "-" + (currEdge.getPoint2().getId()));
			} else {
				currEdge.setID((currEdge.getPoint1().getId()) + "-" + x);
			}
		}
		this.id = x;
	}
	
	public String getName()
	{
		return this.name;
	}
	public void setName (String newName)
	{
		this.name = newName;
	}
	public int getLocX() {
		return locX;
	}
	public void setLocX(int x) {
		this.locX = x;
	}
	public int getLocY() {
		return locY;
	}
	public void setLocY(int y) {
		this.locX = y;
	}
	public int getGlobX() {
		return globX;
	}
	public void setGlobX(int x) {
		this.globX = x;
	}
	public int getGlobY() {
		return globY;
	}
	public void setGlobY(int y) {
		this.globY = y;
	}
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
		this.numberEdges = edges.size();
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
	public boolean equals(Point compPoint){
		return this.id == compPoint.getId();
	}
	public void print()
	{
		System.out.println ("----------Printing Point:"+this.name+"----------");
		System.out.println ("ID:"+this.id);
		System.out.println ("Index:"+this.index);
		System.out.println ("Local X:"+this.locX);
		System.out.println ("Local Y:"+this.locY);
		System.out.println ("Global X:"+this.globX);
		System.out.println ("Global Y:"+this.globY);
		System.out.println ("NumEdges:"+this.numberEdges);
		int i = 0;
		for (i = 0; i<this.numberEdges; i++)
		{
			//System.out.println("Edge"+(i+1)+"id:"+this.edges.get(i).getId());
			this.edges.get(i).print();
			//System.out.println("Edge"+(i+1)+"id:"+this.edges.get(i).getId());
		}
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getMapId()
	{
		return this.mapId;
	}
	public void setMapId(int mapId)
	{
		this.mapId = mapId;
	}
}