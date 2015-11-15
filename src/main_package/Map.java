package main_package;

import java.util.ArrayList;

public class Map {
	private ArrayList<Point> Points; 
	private ArrayList<Edge> Edges;
	private int mapId;
	private int numPoints;
	private String mapName;
	
	public Point addPoint(Point a){
		Points.add(a);
		//MappingDatabase.getInstance().addToDatabase
		return a;
		
	}
	
	public Point getPoint(int xcoord, int ycoord){
		//goes through arraylist Points and returns the Point with
		//the x and y coordinates inputted
		 for (int count = 0; count < Points.size(); count++){
			 Point temp = Points.get(count);
			 if(temp.getX() == xcoord && temp.getY() == ycoord){
				 return temp;
			 }
		 }
		 //if point is not in Points, returns null
		 return null;
	}
	
	public Map (ArrayList<Point> points, int id)
	{
		this.Points = points;
		this.mapId = id;
	}
	public ArrayList<Point> getPointList()
	{
		return Points;
	}
	public void setPointList(ArrayList<Point> newPoints)
	{
		this.Points = newPoints;
	}
	public int getId()
	{
		return mapId;
	}

}
