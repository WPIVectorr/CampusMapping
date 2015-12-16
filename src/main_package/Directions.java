package main_package;

public class Directions {
	private String turn;
	private int distance;
	private boolean isStraight;
	private double time;
	private Point origin;
	private Point destination;
	private Double angle;
	
	public Directions(String turn, int distance, boolean isStraight, double time, Point origin, Point destination, double angle){
		this.turn = turn;
		this.distance = distance;
		this.isStraight = isStraight;
		this.time = time;
		this.origin = origin;
		this.destination = destination;
		this.angle = angle;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
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
	public double getAngle(){
		return angle;
	}
	public void setAngle(double angle){
		this.angle = angle;
	}
	
	
	
	
}
