package main_package;

public class Edge {
	private String id;
	private Point point1;
	private Point point2;
	private int weight;
	private boolean isOutside = false;
	private boolean isStairs = false;
	
	public Edge(Point point1, Point point2){
		this.id = (point1.getId() + "-" + point2.getId());
		this.point1 = point1;
		this.point2 = point2;
		this.weight = (int)Math.sqrt((double)(Math.pow((point1.getGlobX()-point2.getGlobX()),2)+Math.pow((point1.getGlobY()-point2.getGlobY()),2)));
		if(point1.isStairs() && point2.isStairs()){
			this.isStairs = true;
		}
		if(point1.isOutside() || point2.isOutside()){
			this.isOutside = true;
		}
		point1.addEdge(this);								//TODO this may be duplicating edges
		point2.addEdge(this);
	}
	public Edge(Point point1, Point point2, int weight){
		this.id = (point1.getId() + "-" + point2.getId());
		this.point1 = point1;
		this.point2 = point2;
		this.weight = weight;
		if(point1.isStairs() && point2.isStairs()){
			this.isStairs = true;
		}
		if(point1.isOutside() || point2.isOutside()){
			this.isOutside = true;
		}
		point1.addEdge(this);								//TODO this may be duplicating edges
		point2.addEdge(this);								//TODO this may be duplicating edges
	}
	public Edge(Point point1, Point point2, int weight, boolean isOutside, boolean isStairs){
		this.id = (point1.getId() + "-" + point2.getId());
		this.point1 = point1;
		this.point2 = point2;
		this.weight = weight;
		this.isOutside = isOutside;
		this.isStairs = isStairs;
		if(point1.isStairs() && point2.isStairs()){
			this.isStairs = true;
		}
		if(point1.isOutside() || point2.isOutside()){
			this.isOutside = true;
		}
		point1.addEdge(this);								//TODO this may be duplicating edges
		point2.addEdge(this);								//TODO this may be duplicating edges
	}
	public String getID()
	{
		return id;
	}
	public void setID(String newID)
	{
		this.id = newID;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Point getPoint1() {
		return point1;
	}
	public void setPoint1(Point point1) {
		this.point1 = point1;
	}
	public Point getPoint2() {
		return point2;
	}
	public void setPoint2(Point point2) {
		this.point2 = point2;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public boolean isOutside() {
		return isOutside;
	}
	public void setOutside(boolean isOutside) {
		this.isOutside = isOutside;
	}
	public boolean isStairs() {
		return isStairs;
	}
	public void setStairs(boolean isStairs) {
		this.isStairs = isStairs;
	}
	public void print()
	{
		System.out.println("EdgeID:"+this.id);
		System.out.println("Point1ID:"+this.point1.getId());
		System.out.println("Point2ID:"+this.point2.getId());
		System.out.println("Weight:"+this.weight);
		System.out.println("Outside:"+this.isOutside);
		System.out.println("Stairs"+this.isStairs);
	}
}

