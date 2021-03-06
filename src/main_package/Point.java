package main_package;

import java.util.ArrayList;

public class Point implements Comparable{
	private String id;
	private int mapId;
	private String name;
	private int index;
	private double locX;
	private double locY;
	private double globX;
	private double globY;
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private int numberEdges;
	private boolean isStairs;
	private boolean isOutside;
	
	
	public boolean isStairs() {
		return isStairs;
	}
	public void setStairs(boolean isStairs) {
		this.isStairs = isStairs;
	}
	public boolean isOutside() {
		return isOutside;
	}
	public void setOutside(boolean isOutside) {
		this.isOutside = isOutside;
	}

	
	
	public Point()
	{
		
	}
	//TODO UPdate Constructors
	public Point(String id, String name, double x, double y) {
		this.id = id;
		this.name = name;
		this.locX = x;
		this.locY = y;
		this.numberEdges = 0;
		this.index = 0;
		this.globX = x;
		this.globY = y;
	}
	
	public Point(String id, int mapId, String name, int index, int x, int y) {
		this.id = id;
		this.mapId = mapId;
		this.name = name;
		this.locX = x;
		this.locY = y;
		this.numberEdges = 0;
		this.index = index;
		this.globX = x;
		this.globY = y;
	}
	

	public Point(String id, String name, double locX, double locY, double globX, double globY,
			int numberEdges) {
		this.id = id;
		this.name = name;
		this.locX = locX;
		this.locY = locY;
		this.numberEdges = numberEdges;
		this.index = 0;
		this.globX = globX;
		this.globY = globY;
	}
	
	//TODO phase out this constructor
	public Point(String id, int mapId, String name, int index, double locX, double locY, double globX, 
			double globY, int numberEdges) {
		this.id = id;
		this.mapId = mapId;
		this.name = name;
		this.locX = locX;
		this.locY = locY;
		this.numberEdges = numberEdges;
		this.index = index;
		this.globX = globX;
		this.globY = globY;
		this.isStairs = false;
		this.isOutside = false;
	}
	
	public Point(String id, int mapId, String name, int index, double locX, double locY, double globX, 
			double globY, int numberEdges, boolean isStairs, boolean isOutside) {
		this.id = id;
		this.mapId = mapId;
		this.name = name;
		this.locX = locX;
		this.locY = locY;
		this.numberEdges = numberEdges;
		this.index = index;
		this.globX = globX;
		this.globY = globY;
		this.isStairs = isStairs;
		this.isOutside = isOutside;
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
	public double getLocX() {
		return locX;
	}
	public void setLocX(double x) {
		this.locX = x;
	}
	public double getLocY() {
		return locY;
	}
	public void setLocY(double y) {
		this.locX = y;
	}
	public double getGlobX() {
		return globX;
	}
	public void setGlobX(double x) {
		this.globX = x;
	}
	public double getGlobY() {
		return globY;
	}
	public void setGlobY(double y) {
		this.globY = y;
	}
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
		this.numberEdges = edges.size();
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
		return this.id.equals(compPoint.getId());
	}
	
	public void setNumEdges(int numEdges){
		this.numberEdges = numEdges;
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
	public boolean addEdge(Edge edge)
	{
		boolean found = false;
		int j = 0;
		for (j = 0; j<edges.size(); j++)
		{
			if (edges.get(j).getId().contentEquals(edge.getID()))
			{
				found = true;
			}
		}
		if (found)
		{
			return false;
		}
		else
		{
			edges.add(edge);
			this.numberEdges++;
			return true;
		}
	}
	
	@Override
	public int compareTo(Object o) {
		int temp = this.getName().compareTo(((Point) o).getName());
		return temp;
	}
}