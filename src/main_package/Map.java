package main_package;

import java.util.ArrayList;

public class Map {

	ArrayList<Point> points;
	int id;

	public Map (ArrayList<Point> points, int id)
	{
		this.points = points;
		this.id = id;
	}
	public ArrayList<Point> getPointList()
	{
		return points;
	}
	public void setPointList(ArrayList<Point> newPoints)
	{
		this.points = newPoints;
	}
	public int getId()
	{
		return id;
	}
}
