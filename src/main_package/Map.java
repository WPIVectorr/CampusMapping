package main_package;

import java.util.ArrayList;


public class Map {
	private ArrayList<Point> Points; 
	private int mapId;
	private int numPoints;
	private String mapName;
	private double xTopLeft;
	private double yTopLeft;
	private double xBotRight;
	private double yBotRight;
	private double rotationAngle;
	private double width;
	private double height;
	private int pointIDIndex = 0;
	
	/*
	public Map (ArrayList<Point> pointList, int mapId, String mapName)
	{
		this.Points = pointList;
		this.mapId = mapId;
		this.mapName = mapName;
	}
	*/
	//TODO UPdate Constructors
	public Map (int mapId, String mapName, int xTopLeft, int yTopLeft, double rotationAngle)
	{
		this.Points = new ArrayList<Point>();
		this.mapId = mapId;
		this.numPoints = 0;
		this.mapName = mapName;
		this.xTopLeft = xTopLeft;
		this.yTopLeft = yTopLeft;
		
		this.rotationAngle = rotationAngle;
		
		
	}
	
	public Map (ArrayList<Point> points, int mapId, String mapName, int xTopLeft, int yTopLeft, double rotationAngle)
	{
		this.Points = points;
		this.mapId = mapId;
		this.numPoints = points.size();
		this.mapName = mapName;
		this.xTopLeft = xTopLeft;
		this.yTopLeft = yTopLeft;
		this.rotationAngle = rotationAngle;
	}
	
	public ArrayList<Point> getPoints() {
		return Points;
	}

	public void setPoints(ArrayList<Point> points) {
		Points = points;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public double getxTopLeft() {
		return xTopLeft;
	}

	public void setxTopLeft(double xTopLeft) {
		this.xTopLeft = xTopLeft;
	}

	public double getyTopLeft() {
		return yTopLeft;
	}

	public void setyTopLeft(double yTopLeft) {
		this.yTopLeft = yTopLeft;
	}
	
	public double getxBotRight() {
		return xBotRight;
	}

	public void setxBotRight(double xBotRight) {
		this.xBotRight = xBotRight;
	}

	public double getyBotRight() {
		return yBotRight;
	}

	public void setyBotRight(double yBotRight) {
		this.yBotRight = yBotRight;
	}

	public double getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public Map (int mapId, String mapName)
	{
		this.mapId = mapId;
		this.mapName = mapName;
		this.numPoints = 0;
	}
	
	public boolean addPoint(Point a){
		Points.add(a);
		numPoints++;
		pointIDIndex++;
		int j = 0;
		boolean added = false;
		for (j = 0; j <Points.size(); j++)
		{
			if (Points.get(j).getId().contentEquals(a.getId()))
			{
				added = true;
			}
		}
		if (added)
			return true;
		else
			return false;
	}
	
	public Point getPoint(int xcoord, int ycoord){
		//TODO change this to account for Offsets
		//goes through arraylist Points and returns the Point with
		//the x and y coordinates inputted
		 for (int count = 0; count < Points.size(); count++){
			 Point temp = Points.get(count);
			 if(temp.getLocX() == xcoord && temp.getLocY() == ycoord){
				 return temp;
			 }
		 }
		 //if point is not in Points, returns null
		 return null;
	}
	
	public ArrayList<Point> getPointList()
	{
		return Points;
	}
	public void setPointList(ArrayList<Point> newPoints)
	{
		this.Points = newPoints;
		numPoints = newPoints.size();
	}
	public int getPointIDIndex(){
		return pointIDIndex;
	}
}