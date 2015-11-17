package main_package;

import java.util.ArrayList;

public class GenTextDir {
	boolean DEBUG = false;
	public String[] genTextDir(ArrayList<Point> listPoints){
		Point[] arrPoints = new Point[listPoints.size()];
		for (int i = 0; i < listPoints.size(); i++){
			arrPoints[i] = listPoints.get(i);
		}
		String[] retString = new String[arrPoints.length + 1];
		retString[0] = "Start your path at " + arrPoints[arrPoints.length - 1].getName();
		retString[1] = "Travel towards: " + arrPoints[arrPoints.length - 2].getName();
		Point prevPoint = null;
		Point currPoint = null;
		Point nextPoint = null;
		
		double prevPointX;
		double prevPointY;
		double nextPointX;
		double nextPointY;
		
		double angle = 0;
		
		String currString;
		for(int i = 1; i < arrPoints.length - 1; i++){
			prevPoint = arrPoints[arrPoints.length-i];
			currPoint = arrPoints[arrPoints.length-(i+1)];
			nextPoint = arrPoints[arrPoints.length-(i+2)];
			
			prevPointX = prevPoint.getX() - currPoint.getX();
			prevPointY = prevPoint.getY() - currPoint.getY();
			
			nextPointX = nextPoint.getX() - currPoint.getX();
			nextPointY = nextPoint.getY() - currPoint.getY();
			
			double angleRotate = 0;
			if(prevPointY == 0){
				if(prevPointX < 0){
					angleRotate = Math.PI/2;
				} else {
					angleRotate = -Math.PI/2;
				}
			} else {
				angleRotate = Math.atan(prevPointX / prevPointY);
			}
			if(DEBUG){
				System.out.println("Tan Value is: " + Math.atan(-1 / -1) * 4);
				System.out.println("Angle of rotation is: " + angleRotate * 180 / Math.PI);
				System.out.println("Prev Point X = " + prevPointX);
				System.out.println("Prev Point Y = " + prevPointY);
				System.out.println("Next Point X = " + nextPointX);
				System.out.println("Next Point Y = " + nextPointY);
			}
			double tempPrevPointX = prevPointX * Math.cos(angleRotate) - prevPointY * Math.sin(angleRotate);
			double tempPrevPointY = prevPointX * Math.sin(angleRotate) + prevPointY * Math.cos(angleRotate);
			
			double tempNextPointX = nextPointX * Math.cos(angleRotate) - nextPointY * Math.sin(angleRotate);
			double tempNextPointY = nextPointX * Math.sin(angleRotate) + nextPointY * Math.cos(angleRotate);
			
			prevPointX = tempPrevPointX;
			prevPointY = tempPrevPointY;
			
			nextPointX = tempNextPointX;
			nextPointY = tempNextPointY;
			if(DEBUG){
				System.out.println("Prev Point X = " + prevPointX);
				System.out.println("Prev Point Y = " + prevPointY);
				System.out.println("Next Point X = " + nextPointX);
				System.out.println("Next Point Y = " + nextPointY);
			}
			
			if(nextPointY == 0){
				if(DEBUG){
					System.out.println("The point: " + nextPoint.getName() + " has 0 no change in Y");
				}
				if(nextPointX < 0){
					angle = Math.PI/2;
				} else {
					angle = -Math.PI/2;
				}
			} else {
				if(nextPointX >= 0){
					angle = Math.atan(nextPointX / nextPointY);
				} else {
					angle = Math.atan( - nextPointX / nextPointY);
				}
				
			}
			angle = angle * 180 / Math.PI;
			if(DEBUG){
				System.out.println("Turn " + angle + " degrees");
			}
			angle = Math.abs(angle);
			if((angle >= -0.1) && (angle <= 0.1)){
				currString = "Once you reach " + currPoint.getName() + " continue going straight until you reach " + nextPoint.getName();
			} else if (nextPointX <= 0){
				if(nextPointY < 0){
					angle = angle + 90;
				}
				currString = "Once you reach " + currPoint.getName() + " then turn " + Math.floor(angle) + " degrees to your left, and head towards " + nextPoint.getName();
			} else {
				if(nextPointY < 0){
					angle = angle + 90;
				}
				currString = "Once you reach " + currPoint.getName() + " then turn " + Math.floor(angle) + " degrees to your right, and head towards " + nextPoint.getName();
			}
			retString[i+1] = currString;
			if(DEBUG){
				System.out.println("");
				System.out.println("");
			}
		}
		double dist = 0;
		if(nextPoint != null){
			dist = PythagTheorem(nextPoint.getX() - currPoint.getX(), nextPoint.getY() - currPoint.getY());//CONVERT TO FEET
		} else {
			dist = PythagTheorem(arrPoints[1].getX() - arrPoints[0].getX(), arrPoints[1].getY() - arrPoints[0].getY());
		}
		dist = dist * 10;
		dist = Math.floor(dist);
		dist = dist / 10;
		if(dist == 1){
			retString[retString.length - 1] = "Go forward approximately " + Double.toString(dist) + " foot, then you have reached your destination";
		} else {
			retString[retString.length - 1] = "Go forward approximately " + Double.toString(dist) + " feet, then you have reached your destination";
		}
		return retString;
	}
	private double PythagTheorem(double x, double y){
		return Math.sqrt(x*x + y*y);
	}
}