package main_package;

public class Directions {
	private String turn;
	private double distance;
	private boolean isStraight;
	private double time;
	private Point origin;
	private Point destination;
	
	public Directions(String turn, double distance, boolean isStraight, double time, Point origin, Point destination){
		this.turn = turn;
		this.distance = distance;
		this.isStraight = isStraight;
		this.time = time;
		this.origin = origin;
		this.destination = destination;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public boolean isStraight() {
		return isStraight;
	}

	public void setStraight(boolean isStraight) {
		this.isStraight = isStraight;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public Point getOrigin() {
		return origin;
	}

	public void setOrigin(Point origin) {
		this.origin = origin;
	}

	public Point getDestination() {
		return destination;
	}

	public void setDestination(Point destination) {
		this.destination = destination;
	}
	
	
	
	
	
	
}
