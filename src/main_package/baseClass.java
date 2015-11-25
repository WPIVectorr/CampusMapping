package main_package;

import database.ServerDB;

import java.util.ArrayList;

public class baseClass {
	public static void main(String[] args)
	{
		testDatabase();
		Point p1 = new Point("0", "p1", 0, 0);
		Point p2 = new Point("1", "p2", 0, 1);
		Point p3 = new Point("2", "p3", 1, 0);
		Point p4 = new Point("3", "p4", 2, 0);
		Point p5 = new Point("4", "p5", 2, 1);
		Point p6 = new Point("5", "p6", 1, 2);
		Point p7 = new Point("6", "p7", 0, 2);
		Point p8 = new Point("7", "p8", 0, 3);
		Point p9 = new Point("8", "p9", 2, 3);
		Point p10 = new Point("9", "p9", 3, 1);
		Point p11 = new Point("10", "p9", 4, 1);
		Point p12 = new Point("11", "p9", 5, 1);
		Point p13 = new Point("12", "p9", 6, 1);
		Point p14 = new Point("13", "p9", 7, 1);
		Point p15 = new Point("14", "p9", 8, 1);

		Edge e1 = new Edge(p1, p2, 1);
		Edge e2 = new Edge(p2, p5, 1);
		Edge e3 = new Edge(p2, p3, 1);
		Edge e4 = new Edge(p3, p4, 1);
		Edge e5 = new Edge(p3, p5, 1);
		Edge e6 = new Edge(p4, p5, 1);
		Edge e7 = new Edge(p3, p6, 1);
		Edge e8 = new Edge(p5, p6, 1);
		Edge e9 = new Edge(p6, p7, 1);
		Edge e10 = new Edge(p6, p8, 1);
		Edge e11 = new Edge(p5, p9, 1);
		Edge e12 = new Edge(p8, p9, 1);
		Edge e13 = new Edge(p5, p10, 1);
		Edge e14 = new Edge(p10, p11, 1);
		Edge e15 = new Edge(p11, p12, 1);
		Edge e16 = new Edge(p12, p13, 1);
		Edge e17 = new Edge(p13, p14, 1);
		Edge e18 = new Edge(p14, p15, 1);
		
		testGenDir();
		testAStar(p1, p8);
	}
	public static void testDatabase ()
	{
		System.out.println("------------------------------Testing Database-----------------------------");
		ServerDB testDB = ServerDB.getInstance();
		//testDB.testMaps();
		//testDB.testInsert();
		//testDB.testUpdate();
		System.out.println("------------------------------Done Testing Database-----------------------------");
		//testDB.printDatabase(true, true, true);
	}
	
	
	public static void testGenDir(){
		System.out.println("");
		Point a = new Point("0", "Stair Bottom", 0, 0);
		Point b = new Point("1", "Stair Top", 1, 0);
		Point c = new Point("2", "Hallway Start", 2, -1);
		ArrayList<Point> testPoints = new ArrayList<Point>();
		testPoints.add(c);
		testPoints.add(b);
		testPoints.add(a);
		GenTextDir testGenTestDir = new GenTextDir(); 
		/*String[] testOneString = testGenTestDir.genTextDir(testPoints);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		
		System.out.println("");
		
		a = new Point("0", "Stair Bottom", 0, 0);
		b = new Point("1", "Stair Top", 1, 0);
		c = new Point("2", "Hallway Start", 2, 1);
		
		testPoints.clear();
		testPoints.add(c);
		testPoints.add(b);
		testPoints.add(a);
		
		testOneString = testGenTestDir.genTextDir(testPoints);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		
		System.out.println("");
		
		a = new Point("0", "Stair Bottom", 0, 0);
		b = new Point("1", "Stair Top", 1, 0);
		c = new Point("2", "Hallway Start", 2, 0);
		testPoints.clear();
		testPoints.add(c);
		testPoints.add(b);
		testPoints.add(a);
		
		testOneString = testGenTestDir.genTextDir(testPoints);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		
		Point p1 = new Point("0", "p1", 0, 0);
		Point p2 = new Point("1", "p2", 0, 1);
		Point p3 = new Point("2", "p3", 1, 0);
		Point p4 = new Point("3", "p4", 2, 0);
		Point p5 = new Point("4", "p5", 2, 1);
		Point p6 = new Point("5", "p6", 1, 2);
		Point p7 = new Point("6", "p7", 0, 2);
		Point p8 = new Point("7", "p8", 0, 3);
		Point p9 = new Point("8", "p9", 2, 3);
		Point p10 = new Point("9", "p9", 3, 1);
		Point p11 = new Point("10", "p9", 4, 1);
		Point p12 = new Point("11", "p9", 5, 1);
		Point p13 = new Point("12", "p9", 6, 1);
		Point p14 = new Point("13", "p9", 7, 1);
		Point p15 = new Point("14", "p9", 8, 1);
		
		ArrayList<Point> testPoints2 = new ArrayList<Point>();
		testPoints2.add(p8);
		testPoints2.add(p6);
		testPoints2.add(p5);
		testPoints2.add(p2);
		testPoints2.add(p1);
		testOneString = testGenTestDir.genTextDir(testPoints2);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		System.out.println(testOneString[4]);
		System.out.println(testOneString[5]);
		
		System.out.println("");
		testPoints2.clear();
		testPoints2.add(p8);
		testPoints2.add(p9);
		testPoints2.add(p5);
		testPoints2.add(p2);
		testPoints2.add(p1);
		
		testOneString = testGenTestDir.genTextDir(testPoints2);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		System.out.println(testOneString[4]);
		System.out.println(testOneString[5]);*/
		//int id, String name, int x, int y, int numberEdges
	}
	public static void testAStar(Point start, Point end){
		System.out.print("Start: ");
		System.out.println(start);
		System.out.print("End: ");
		System.out.println(end);
		System.out.println("Path Found:");
		AStar path = new AStar();
		path.reset();
		ArrayList<Point> fin = path.PathFind(start, end);
		for(int i = 0; i < fin.size(); i++){
			System.out.println(fin.get(i));
		}
	}
}