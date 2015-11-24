package main_package;

import java.util.ArrayList;

public class GenTextDir {
	boolean DEBUG = false;//Debug variable for printouts
	public ArrayList<Directions> genTextDir(ArrayList<Point> listPoints){
		if(listPoints.size() <= 1){//checks to make certain there are enough points (Origin and Destination are not the same)
			return null;
		} else { //If the origin and destination are not the same, then continue
			Point[] arrPoints = new Point[listPoints.size()];//First, convert the array list to an array.
			
			ArrayList<Directions> retDir = new ArrayList<Directions>();//EVENTUAL RETURN VALUE
			
			for (int i = 0; i < listPoints.size(); i++){
				arrPoints[i] = listPoints.get(i);
			}
			double dist = 0;//Now find out the last direction
			dist = PythagTheorem(arrPoints[1].getGlobX() - arrPoints[0].getGlobX(), arrPoints[1].getGlobY() - arrPoints[0].getGlobY());//CONVERT TO FEET
			dist = dist * 10;
			dist = Math.floor(dist);
			dist = dist / 10;
			Directions retDirFirst = new Directions("straight", dist, true, dist, arrPoints[0], arrPoints[1]);
			retDir.add(retDirFirst);
			
			
			Point prevPoint = null;//Initialize previous, current, and next points
			Point currPoint = null;
			Point nextPoint = null;

			double prevPointX;//Store their coordinates as doubles. I use doubles for my trig later
			double prevPointY;
			double nextPointX;
			double nextPointY;

			double angle = 0;//Store the angle to turn as a double
			Directions currDir;//VARIABLE WILL BE USED AS THE RETURN LATER, IN ARRAYLIST FORM
			for(int i = 1; i < arrPoints.length - 1; i++){//Iterate over the array of points given
				prevPoint = arrPoints[arrPoints.length-i];//Grab the previous point the user was at
				currPoint = arrPoints[arrPoints.length-(i+1)];//Grab the current point the user is at
				nextPoint = arrPoints[arrPoints.length-(i+2)];//Grab the next point the user is going to.
				System.out.println("Previous point name: " + prevPoint.getName());
				System.out.println("Current point name: " + currPoint.getName());
				System.out.println("Next point name: " + nextPoint.getName());
				//The way this code works is first it moves all the points such that the current point
				//sits at the origin.
				prevPointX = prevPoint.getGlobX() - currPoint.getGlobX();
				prevPointY = currPoint.getGlobY() - prevPoint.getGlobY();

				nextPointX = nextPoint.getGlobX() - currPoint.getGlobX();
				nextPointY = currPoint.getGlobY() - nextPoint.getGlobY();
				System.out.println("Previous next point Y was : " + nextPointY);
				System.out.println("Previous Y is: " + prevPointY);
				
				//Next, we want to rotate the points around the axis, so the vector from the previous point
				//to the current point is the Y axis
				double angleRotate = 0;//Initialize the angle of rotation.
				//System.out.println("PrevPointX = " + prevPointX);
				//System.out.println("PrevPointY = " + prevPointY);
				if(prevPointY == 0){//if the previous points Y is at 0, we rotate either PI/2, or -PI/2
					if(prevPointX < 0){//If it's X is less than 0, we are rotating PI/2
						angleRotate = -Math.PI/2;
					} else {
						angleRotate = Math.PI/2;
					}
				} else if (prevPointY < 0){
					if(prevPointX > 0){
						angleRotate = 2 * Math.PI - Math.atan(Math.abs(prevPointX) / Math.abs(prevPointY));//if it is in the bottom right quadrant, subtract angle found from 2PI
					} else {
						angleRotate = Math.atan(Math.abs(prevPointX) / Math.abs(prevPointY));//if it is in the bottom left quadrant, just rotate the angle found
					}
				} else {
					if(prevPointX > 0){
						angleRotate = Math.PI + Math.atan(Math.abs(prevPointX) / Math.abs(prevPointY));
					} else if (prevPointX < 0){
						angleRotate = Math.PI - Math.atan(Math.abs(prevPointX) / Math.abs(prevPointY));
					} else {
						angleRotate = 0;
					}
				}
				if(DEBUG){//testing
					System.out.println("Tan Value is: " + Math.atan(-1 / -1) * 4);
					System.out.println("Angle of rotation is: " + angleRotate * 180 / Math.PI);
					System.out.println("Prev Point X = " + prevPointX);
					System.out.println("Prev Point Y = " + prevPointY);
					System.out.println("Next Point X = " + nextPointX);
					System.out.println("Next Point Y = " + nextPointY);
				}
				System.out.println("Angle of rotation is: " + (180 * angleRotate / Math.PI));
				//Now, rotate the previous point by the angle of rotation.
				double tempPrevPointX = (prevPointX * Math.cos(angleRotate)) - (prevPointY * Math.sin(angleRotate));
				//System.out.println("tempPrevPointX " + tempPrevPointX);
				double tempPrevPointY = (prevPointX * Math.sin(angleRotate)) + (prevPointY * Math.cos(angleRotate));
				//Next, rotate the next point by the angle of rotation
				double tempNextPointX = (nextPointX * Math.cos(angleRotate)) - (nextPointY * Math.sin(angleRotate));
				double tempNextPointY = (nextPointX * Math.sin(angleRotate)) + (nextPointY * Math.cos(angleRotate));
				//Grab those values and store them.
				prevPointX = Math.floor(tempPrevPointX);
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
						angle = Math.atan( nextPointX / nextPointY);//If it is, grab the arcTan of X/Y
					} else {
						angle = Math.atan( nextPointX / nextPointY);//If it isn't, grab the arcTan of -X/Y
					}
				}
				angle = angle * 180 / Math.PI;//Convert the angle found into degrees
				angle = Math.floor(angle);
				if(DEBUG){
					System.out.println("Turn " + angle + " degrees");
				}
				angle = Math.abs(angle);//Set the angle equal to its absolute value (no turning - degrees)
				String turnAmount;
				//System.out.println("At " + currPoint.getName() + " the past angle rotate value is: " + (180 * angleRotate / Math.PI));
				System.out.println("At " + currPoint.getName() + " the angle value is: " + angle);
				System.out.println("At " + currPoint.getName() + " the y value is: " + prevPointY);
				System.out.println("At " + currPoint.getName() + " the x value is: " + nextPointX);
				if((angle >= -0.1) && (angle <= 0.1)){//if the angle is within some degree of error of 0, we are going straight
					dist = 0;//Now find out the last direction
					dist = PythagTheorem(nextPoint.getGlobX() - currPoint.getGlobX(), nextPoint.getGlobY() - currPoint.getGlobY());//CONVERT TO FEET
					dist = dist * 10;
					dist = Math.floor(dist);
					dist = dist / 10;
					currDir = new Directions("straight", dist, false, dist, currPoint, nextPoint);
					
				
				} else if (nextPointX <= 0){//otherwise, if its X is negative, we are turning left.
					System.out.println("At " + currPoint.getName() + " the new y value is: " + nextPointY);
					
					if(nextPointY < 0){//if the Y is less than 0, angle is 180-angle
						angle = 180-angle;
						//System.out.println("angle at: " + currPoint.getName());
					}
					System.out.println("Angle found is: " + angle);
					if(angle > 0 && angle < 60){
						turnAmount = "slight left";
					} else if(angle > 60 && angle < 120){
						turnAmount = "left";
					} else {
						turnAmount = "sharp left";
					}
					dist = 0;//Now find out the last direction
					dist = PythagTheorem(nextPoint.getGlobX() - currPoint.getGlobX(), nextPoint.getGlobY() - currPoint.getGlobY());//CONVERT TO FEET
					dist = dist * 10;
					dist = Math.floor(dist);
					dist = dist / 10;
					currDir = new Directions(turnAmount, dist, false, dist, currPoint, nextPoint);
				} else {//otherwise, do the same, but we are turning right
					if(nextPointY < 0){
						angle = 180-angle;
					}
					if(angle > 0 && angle < 60){
						turnAmount = "slight right";
					} else if(angle > 60 && angle < 120){
						turnAmount = "right";
					} else {
						turnAmount = "sharp right";
					}
					
					dist = 0;//Now find out the last direction
					dist = PythagTheorem(nextPoint.getGlobX() - currPoint.getGlobX(), nextPoint.getGlobY() - currPoint.getGlobY());//CONVERT TO FEET
					dist = dist * 10;
					dist = Math.floor(dist);
					dist = dist / 10;
					currDir = new Directions(turnAmount, dist, false, dist, currPoint, nextPoint);
					
				}
				if(DEBUG){
					System.out.println("");
					System.out.println("");
				}
				retDir.add(currDir);
			}
			dist = 0;//Now find out the last direction
			if(nextPoint != null){//Figure out the distance to the last point
				dist = PythagTheorem(nextPoint.getGlobX() - currPoint.getGlobX(), nextPoint.getGlobY() - currPoint.getGlobY());//CONVERT TO FEET
			} else {
				dist = PythagTheorem(arrPoints[1].getGlobX() - arrPoints[0].getGlobX(), arrPoints[1].getGlobY() - arrPoints[0].getGlobY());
			}
			dist = dist * 10;
			dist = Math.floor(dist);
			dist = dist / 10;
			currDir = new Directions("straight", dist, false, dist, currPoint, nextPoint);
			retDir.add(currDir);
			
			return retDir;//return retDir
		}
	}
	private double PythagTheorem(double x, double y){
		return Math.sqrt(x*x + y*y);
	}
	public ArrayList<Directions> generateDirections(ArrayList<Directions> directions) throws MalformedDirectionException{
		ArrayList<Directions> retDirections = new ArrayList<Directions>();
		Directions currDir;
		int i = 0;
		while(i < directions.size()){
			if(directions.get(i).getTurn().equals("slight left")){
				currDir = directions.get(i);
				i++;
			} else if (directions.get(i).getTurn().equals("left")) {
				currDir = directions.get(i);
				i++;
			} else if (directions.get(i).getTurn().equals("sharp left")) {
				currDir = directions.get(i);
				i++;
			} else if (directions.get(i).getTurn().equals("slight right")) {
				currDir = directions.get(i);
				i++;
			} else if (directions.get(i).getTurn().equals("right")) {
				currDir = directions.get(i);
				i++;
			} else if (directions.get(i).getTurn().equals("sharp right")) {
				currDir = directions.get(i);
				i++;
			} else if (directions.get(i).getTurn().equals("straight")) {
				currDir = directions.get(i);
				i++;
				while(directions.get(i).isStraight()){
					currDir.setDistance(currDir.getDistance() + directions.get(i).getDistance());
					currDir.setDistance(currDir.getTime() + directions.get(i).getTime());
					currDir.setDestination(directions.get(i).getDestination());
					i++;
				}
			} else {
				throw new MalformedDirectionException("This direction has the wrong turn information" + directions.get(i).getTurn());
			}
			retDirections.add(currDir);
		}
		return retDirections;
	}
	
	public String[] genDirStrings(ArrayList<Directions> directions) throws MalformedDirectionException{
		String[] retString = new String[directions.size()];
		Directions currDir;
		for(int i = 0; i < directions.size(); i++){
			currDir = directions.get(i);
			if(currDir.getTurn().equals("slight left")){
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " turn a " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else if (currDir.getTurn().equals("left")) {
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " turn a " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else if (currDir.getTurn().equals("sharp left")) {
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " turn a " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else if (currDir.getTurn().equals("slight right")) {
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " turn a " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else if (currDir.getTurn().equals("right")) {
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " turn a " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else if (currDir.getTurn().equals("sharp right")) {
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " turn a " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else if (currDir.getTurn().equals("straight")) {
				retString[i] = "Once you reach " + currDir.getOrigin().getName() + " go " + currDir.getTurn() + " towards " + currDir.getDestination().getName() + " and walk for " + currDir.getDistance() + " feet.";
			} else {
				throw new MalformedDirectionException("This direction has the wrong turn information" + directions.get(i).getTurn());
			}
		}
		return retString;
	}
	
}