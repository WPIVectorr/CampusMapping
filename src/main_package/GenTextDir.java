package main_package;

import java.util.ArrayList;

public class GenTextDir {
	boolean DEBUG = false;//Debug variable for printouts
	public String[] genTextDir(ArrayList<Point> listPoints){
		if(listPoints.size() <= 1){//checks to make certain there are enough points (Origin and Destination are not the same)
			String[] retString = new String[1];
			retString[0] = "Invalid Points entered."; //If they are, return an error array.
			return retString;
		} else { //If the origin and destination are not the same, then continue
			Point[] arrPoints = new Point[listPoints.size()];//First, convert the array list to an array.
			for (int i = 0; i < listPoints.size(); i++){
				arrPoints[i] = listPoints.get(i);
			}
			String[] retString = new String[arrPoints.length + 1];//Our return array is going to be one larger than the given point array
			retString[0] = "Start your path at " + arrPoints[arrPoints.length - 1].getName();//Give it a start message as the beginning direction
			retString[1] = "Travel towards: " + arrPoints[arrPoints.length - 2].getName();//Give it a point to travel towards as the second direction
			Point prevPoint = null;//Initialize previous, current, and next points
			Point currPoint = null;
			Point nextPoint = null;

			double prevPointX;//Store their coordinates as doubles. I use doubles for my trig later
			double prevPointY;
			double nextPointX;
			double nextPointY;

			double angle = 0;//Store the angle to turn as a double

			String currString;//Initialize the string to input at the current spot.
			for(int i = 1; i < arrPoints.length - 1; i++){//Iterate over the array of points given
				prevPoint = arrPoints[arrPoints.length-i];//Grab the previous point the user was at
				currPoint = arrPoints[arrPoints.length-(i+1)];//Grab the current point the user is at
				nextPoint = arrPoints[arrPoints.length-(i+2)];//Grab the next point the user is going to.

				//The way this code works is first it moves all the points such that the current point
				//sits at the origin.
				prevPointX = prevPoint.getX() - currPoint.getX();
				prevPointY = prevPoint.getY() - currPoint.getY();

				nextPointX = nextPoint.getX() - currPoint.getX();
				nextPointY = nextPoint.getY() - currPoint.getY();
				//Next, we want to rotate the points around the axis, so the vector from the previous point
				//to the current point is the Y axis
				double angleRotate = 0;//Initialize the angle of rotation.
				if(prevPointY == 0){//if the previous points Y is at 0, we rotate either PI/2, or -PI/2
					if(prevPointX < 0){//If it's X is less than 0, we are rotating PI/2
						angleRotate = Math.PI/2;
					} else {
						angleRotate = -Math.PI/2;
					}
				} else if (prevPointY < 0){
					angleRotate = Math.atan(prevPointX / prevPointY);//If it's y is less than 0, rotate the arcTangent of X / Y
				} else {
					angleRotate = 180 - Math.atan(prevPointX / prevPointY);//If it's y is greater than 0, rotate 180 - arcTangent of X / Y
				}
				if(DEBUG){//testing
					System.out.println("Tan Value is: " + Math.atan(-1 / -1) * 4);
					System.out.println("Angle of rotation is: " + angleRotate * 180 / Math.PI);
					System.out.println("Prev Point X = " + prevPointX);
					System.out.println("Prev Point Y = " + prevPointY);
					System.out.println("Next Point X = " + nextPointX);
					System.out.println("Next Point Y = " + nextPointY);
				}
				//Now, rotate the previous point by the angle of rotation.
				double tempPrevPointX = prevPointX * Math.cos(angleRotate) - prevPointY * Math.sin(angleRotate);
				double tempPrevPointY = prevPointX * Math.sin(angleRotate) + prevPointY * Math.cos(angleRotate);
				//Next, rotate the next point by the angle of rotation
				double tempNextPointX = nextPointX * Math.cos(angleRotate) - nextPointY * Math.sin(angleRotate);
				double tempNextPointY = nextPointX * Math.sin(angleRotate) + nextPointY * Math.cos(angleRotate);
				//Grab those values and store them.
				prevPointX = tempPrevPointX;
				prevPointY = tempPrevPointY;

				nextPointX = tempNextPointX;
				nextPointY = tempNextPointY;
				if(DEBUG){//testing
					System.out.println("Prev Point X = " + prevPointX);
					System.out.println("Prev Point Y = " + prevPointY);
					System.out.println("Next Point X = " + nextPointX);
					System.out.println("Next Point Y = " + nextPointY);
				}
				//Now, we check if there is no change in Y for the next point.
				if(nextPointY == 0){//If there isn't a change in Y, then we are either turning PI/2 to the left, or the right
					if(DEBUG){
						System.out.println("The point: " + nextPoint.getName() + " has 0 no change in Y");
					}
					
					if(nextPointX < 0){//If the next point is to the left of current point.
						angle = Math.PI/2;//Turn left 90 degrees
					} else {// otherwise
						angle = -Math.PI/2;//Turn right 90 degrees
					}
				} else {//if the next point has a change in Y
					if(nextPointX >= 0){//Figure out if its X value is greater than 0
						angle = Math.atan(nextPointX / nextPointY);//If it is, grab the arcTan of X/Y
					} else {
						angle = Math.atan( - nextPointX / nextPointY);//If it isn't, grab the arcTan of -X/Y
					}

				}
				angle = angle * 180 / Math.PI;//Convert the angle found into degrees
				if(DEBUG){
					System.out.println("Turn " + angle + " degrees");
				}
				angle = Math.abs(angle);//Set the angle equal to its absolute value (no turning - degrees)
				if((angle >= -0.1) && (angle <= 0.1)){//if the angle is within some degree of error of 0, we are going straight
					currString = "Once you reach " + currPoint.getName() + " continue going straight until you reach " + nextPoint.getName();
				} else if (nextPointX <= 0){//otherwise, if its X is negative, we are turning left.
					if(nextPointY < 0){//if the Y is less than 0, angle is 180-angle
						angle = 180-angle;
					}
					currString = "Once you reach " + currPoint.getName() + " then turn " + Math.floor(angle) + " degrees to your left, and head towards " + nextPoint.getName();
				} else {//otherwise, do the same, but we are turning right
					if(nextPointY < 0){
						angle = angle + 90;
					}
					currString = "Once you reach " + currPoint.getName() + " then turn " + Math.floor(angle) + " degrees to your right, and head towards " + nextPoint.getName();
				}
				retString[i+1] = currString;//put the current string in the return array.
				if(DEBUG){
					System.out.println("");
					System.out.println("");
				}
			}
			double dist = 0;//Now find out the last direction
			if(nextPoint != null){//Figure out the distance to the last point
				dist = PythagTheorem(nextPoint.getX() - currPoint.getX(), nextPoint.getY() - currPoint.getY());//CONVERT TO FEET
			} else {
				dist = PythagTheorem(arrPoints[1].getX() - arrPoints[0].getX(), arrPoints[1].getY() - arrPoints[0].getY());
			}
			dist = dist * 10;
			dist = Math.floor(dist);
			dist = dist / 10;
			if(dist == 1){//Print out the dist value (also, check if its feet or foot)
				retString[retString.length - 1] = "Go forward approximately " + Double.toString(dist) + " foot, then you have reached your destination";
			} else {
				retString[retString.length - 1] = "Go forward approximately " + Double.toString(dist) + " feet, then you have reached your destination";
			}
			return retString;//return retString
		}
	}
	private double PythagTheorem(double x, double y){
		return Math.sqrt(x*x + y*y);
	}
}