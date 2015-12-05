package main_package;

import java.util.ArrayList;


public class Map implements Comparable{
	private ArrayList<Point> Points; 
	private int mapId;
	private int numPoints;
	private String mapName;
	private double xTopLeft;
	private double yTopLeft;
	private double xBotRight;
	private double yBotRight;
	private double rotationAngle;
	private int pointIDIndex = 0;
	private double width;
	private double height;
	
	/*
	public Map (ArrayList<Point> pointList, int mapId, String mapName)
	{
		this.Points = pointList;
		this.mapId = mapId;
		this.mapName = mapName;
	}
	*/
	//TODO UPdate Constructors
	public Map (int mapId, String mapName)
	{
		this.mapId = mapId;
		this.mapName = mapName;
		this.numPoints = 0;
		this.Points = new ArrayList<Point>();
	}
	
	public Map (int mapId, String mapName, double xTopLeft, double yTopLeft, double rotationAngle)
	{
		this.Points = new ArrayList<Point>();
		this.mapId = mapId;
		this.numPoints = 0;
		this.mapName = mapName;
		this.xTopLeft = xTopLeft;
		this.yTopLeft = yTopLeft;
		this.rotationAngle = rotationAngle;
	}
	
	public Map (ArrayList<Point> points, int mapId, String mapName, double xTopLeft, double yTopLeft, double rotationAngle)
	{
		this.Points = points;
		this.mapId = mapId;
		this.numPoints = points.size();
		this.mapName = mapName;
		this.xTopLeft = xTopLeft;
		this.yTopLeft = yTopLeft;
		this.rotationAngle = rotationAngle;
	}
	
	public Map (int mapId, String mapName, double xTopLeft, double yTopLeft, double xBotRight, double yBotRight, double rotationAngle, int pointIDIndex)
	{
		this.Points = new ArrayList<Point>();
		this.mapId = mapId;
		this.numPoints = 0;
		this.mapName = mapName;
		this.xTopLeft = xTopLeft;
		this.yTopLeft = yTopLeft;
		this.xBotRight = xBotRight;
		this.yBotRight = yBotRight;
		this.rotationAngle = rotationAngle;
		this.pointIDIndex = pointIDIndex;
		double ourRotate = (Math.PI * 2) - rotationAngle;
		double xTopLeftDeRotate = xTopLeft * Math.cos(ourRotate) - yTopLeft * Math.sin(ourRotate);
		double xBotRightDeRotate = xBotRight * Math.cos(ourRotate) - yBotRight * Math.sin(ourRotate);
		this.width = Math.abs(xTopLeftDeRotate - xBotRightDeRotate);
		
		double yTopLeftDeRotate = yTopLeft * Math.cos(ourRotate) + xTopLeft * Math.sin(ourRotate);
		double yBotRightDeRotate = yBotRight * Math.cos(ourRotate) + xBotRight * Math.sin(ourRotate);
		this.height = Math.abs(yTopLeftDeRotate - yBotRightDeRotate);
	}
	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
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
	
	public boolean addPoint(Point a){
		if (a == null)
		{
			System.out.println("Point to add is null");
			return false;
		}
		if (this.Points == null)
		{
			System.out.println("Current point list is null");
			return false;
		}
		this.Points.add(a);
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
		if (this.Points == null)
		{
			System.out.println("STILL NULL AHSJDHBLASHDLASD");
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
	
	public void printMap()
	{
		System.out.println("--------------------Printing Map:"+this.mapName+"--------------------");
		System.out.println("Map id:" + this.mapId);
		System.out.println("xTopLeft:"+this.xTopLeft);
		System.out.println("yTopLeft:"+this.yTopLeft);
		System.out.println("xBotRight:"+this.xBotRight);
		System.out.println("yBotRight:"+this.yBotRight);
		System.out.println("rotation angle:"+this.rotationAngle);
		System.out.println("pointIDindex:"+this.pointIDIndex);
		System.out.println("numPoints:"+this.numPoints);
		int j = 0;
		for (j = 0; j < Points.size(); j++)
		{
			Points.get(j).print();
		}
	}
	
	public int getNewPointIndex()
	{
		this.pointIDIndex++;
		return this.pointIDIndex;
	}
	
	public String getNewPointID()
	{
		int index = getNewPointIndex();
		String retVal = "";
		retVal+=this.mapId;
		retVal+="_";
		retVal+=index;
		return retVal;
	}
	
	public void setPointIDIndex(int newIndex)
	{
		this.pointIDIndex = newIndex;
	}

	@Override
	public int compareTo(Object o) {
		int temp = this.getMapName().compareTo(((Map) o).getMapName());
		return temp;		
	}
	
	
}