package main_package;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JTextField;

import main_package.MapUpdaterGUI.DrawPanel;

public class MapUpdaterControl {
	protected static ArrayList<Point> pointArray = new ArrayList<Point>();
	protected static boolean editingPoint = false;
	protected static boolean addingMap = false;
	protected static ArrayList<Point> oldPoints = new ArrayList<Point>();
	protected static ArrayList<Point> newPoints = new ArrayList<Point>();
	protected static ArrayList<Point> updatedPoints = new ArrayList<Point>();
	protected static ArrayList<Point> markForDelete = new ArrayList<Point>();
	protected static Point currentPoint;
	protected static Point editPoint;
	protected static int editPointIndex;
	protected static ArrayList<Edge> edgeArray = new ArrayList<Edge>();
	protected static ArrayList<Edge> newEdges = new ArrayList<Edge>();
	protected static ArrayList<Edge> drawnEdges = new ArrayList<Edge>();
	protected static Edge currentEdge;
	protected static String maptitle = "";
	protected static String srcInput = "";
	protected static File srcFile = null;
	protected static InterMapEdgeGUI connectMapGUI;

	protected final static boolean DEBUG = false;
	protected static Map currentMap = null;
	protected static File destinationFile;
	protected static double newImageHeight = 0;
	protected static double newImageWidth = 0;
	protected static double scaleSize = 1;
	protected static double drawnposx = 0;
	protected static double drawnposy = 0;

	protected static Boolean pathMode = false;



	public static boolean addPoint(int mouseX,int mouseY, Map currentMap, BufferedImage img, String roomNumber)
	{




		Integer nameNumber = currentMap.getPointIDIndex()+1;
		double ourRotation = currentMap.getRotationAngle();
		ourRotation = 2 * Math.PI - ourRotation;

		destinationFile = new File("src/VectorMaps/" + "Campus" + ".png");
		destinationFile = new File(destinationFile.getAbsolutePath());
		BufferedImage campusImage = null;
		try {
			if(DEBUG)
				System.out.println("The absolute path is: " + destinationFile.getAbsolutePath());
			//System.out.println("Map name " + currentMap.getMapName());

			campusImage = ImageIO.read(destinationFile);
		} catch (IOException e) {
			System.out.println("Invalid Map Selection");
			e.printStackTrace();
		}
		//double centerCurrentMapX = (Math.floor(((currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2) * img.getWidth())) / (campusImage.getWidth());
		//double centerCurrentMapY = (Math.floor(((currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2) * img.getHeight())) / (campusImage.getHeight());
		double tempPreRotateX = mouseX;
		double tempPreRotateY = mouseY;
		double LocalX = (mouseX-drawnposx)/newImageWidth;
		double LocalY = (mouseY-drawnposy)/newImageHeight;
		tempPreRotateX = tempPreRotateX - drawnposx;
		tempPreRotateY = tempPreRotateY - drawnposy;
		tempPreRotateX = tempPreRotateX/(img.getWidth()*scaleSize);
		tempPreRotateY = tempPreRotateY/(img.getHeight()*scaleSize);
		tempPreRotateX = tempPreRotateX - 0.5;
		tempPreRotateY = tempPreRotateY - 0.5;
		tempPreRotateX = tempPreRotateX * currentMap.getWidth();
		tempPreRotateY = tempPreRotateY * currentMap.getHeight();
		double rotateX = Math.cos(ourRotation) * tempPreRotateX - Math.sin(ourRotation) * tempPreRotateY;
		double rotateY = Math.sin(ourRotation) * tempPreRotateX + Math.cos(ourRotation) * tempPreRotateY;
		rotateX = rotateX * campusImage.getWidth();
		rotateY = rotateY * campusImage.getHeight();
		int finalGlobX = (int) Math.round(rotateX + (campusImage.getWidth() * (currentMap.getxTopLeft() + currentMap.getxBotRight()) / 2));
		int finalGlobY = (int) Math.round(rotateY + (campusImage.getHeight() * (currentMap.getyTopLeft() + currentMap.getyBotRight()) / 2));

		//if(DEBUG)
		System.out.println("Global X is: " + finalGlobX + " and Y is: " + finalGlobY);

		if(DEBUG)
			System.out.println("newest map id: "+currentMap.getNewPointID());
		Point point = new Point(currentMap.getNewPointID(), currentMap.getMapId(),
				roomNumber, currentMap.getPointIDIndex(),
				LocalX, LocalY, finalGlobX, finalGlobY, 0);

		boolean shouldAdd = true;
		for(int k = 0; k < pointArray.size(); k++){
			//System.out.println(pointArray.get(k).getId());
			if(point.getId().equals(pointArray.get(k).getId())){
				//System.out.println(pointArray.get(k).getId());
				//System.out.println("should add = false");
				shouldAdd = false;
			}
		}
		if(LocalX < 0 || LocalX > 1 || LocalY < 0 || LocalY > 1)
			shouldAdd = false;
		if(shouldAdd){
			MapUpdaterGUI.pointArray.add(point);
			newPoints.add(point);
		}

		return true;
	}



	public static String addEdgeToMap(String pointName, int i)
	{
		currentEdge = new Edge(editPoint, currentPoint);
		
		pointArray.set(editPointIndex, editPoint);
		pointArray.get(i).print();
		if(!(updatedPoints.contains(editPoint))){
			updatedPoints.add(editPoint);
		} else {
			for(int r = 0; r < updatedPoints.size(); r++){
				if(editPoint.getId().contentEquals(updatedPoints.get(r).getId())){
					//update the value of editpoint in updated points
					updatedPoints.set(r, editPoint);
					r = updatedPoints.size();
				}
			}
		}
		if(!(updatedPoints.contains(currentPoint))){
			updatedPoints.add(currentPoint);
		} else {
			for(int r = 0; r < updatedPoints.size(); r++){
				if(currentPoint.getId().contentEquals(updatedPoints.get(r).getId())){
					//update the value of "currentPoint" in updated points
					updatedPoints.set(r, currentPoint);
					r = updatedPoints.size();
				}
			}
		}
		if(DEBUG){
			System.out.println("Edge sizes- editPoint:"+pointArray.get(editPointIndex).getEdges().size()+
					" currentPoint:"+pointArray.get(i).getEdges().size());
			System.out.println("Current Edge is: " + currentEdge.getId());
		}
		edgeArray.add(currentEdge);
		//if the new edge is in the newedges array, update it, else add it.
		if(newEdges.contains(currentEdge)){
			for(int r = 0; r < newEdges.size(); r++){
				if(currentEdge.getId().contentEquals(newEdges.get(r).getId())){
					newEdges.set(r, currentEdge);
					r = newEdges.size();
				}
			}
		} else {
			newEdges.add(currentEdge);
		}

		//this is just a print statement. wtf.
		if (currentPoint.getNumEdges() > 0)//this has to be caught in an exception later
		{
			for (int j = 0; j < currentPoint.getNumEdges(); j++) {
				if(DEBUG){
					System.out.println("Adding clicked edge between: "
							+ currentPoint.getEdges().get(j).getPoint1().getName() + ", "
							+ currentPoint.getEdges().get(j).getPoint2().getName());
				}
			}
		}
		if(pathMode){
			Point tempEditPoint = pointArray.get(editPointIndex);
			tempEditPoint.setName(pointName);
			pointArray.set(editPointIndex, tempEditPoint);
			if(updatedPoints.contains(tempEditPoint)){
				for(int r = 0; r < updatedPoints.size(); r++){
					if(tempEditPoint.getId().contentEquals(updatedPoints.get(r).getId())){
						updatedPoints.set(r, currentPoint);
						r = updatedPoints.size();
					}
				}
			} else {
				updatedPoints.add(tempEditPoint);
			}
			editPoint = currentPoint;
			editPointIndex = i;

		}

		return editPoint.getName();

	}

	public static void setImageConstraints(DrawPanel drawPanel, BufferedImage img) {
		// TODO Auto-generated method stub

	}
}


