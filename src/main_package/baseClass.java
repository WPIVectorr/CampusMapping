package main_package;

import database.MappingDatabase;

public class baseClass {
	public static void main(String[] args)
	{
		System.out.println("Test print");
		System.out.println("Test print 2");
		System.out.println("Test print 3");
		System.out.println("Test print 4");

		testDatabase();
		testGenDir();
	}
	public static void testDatabase ()
	{
		MappingDatabase testDB = new MappingDatabase();
		testDB.initDatabase();
		testDB.testInsert();
		//testDB.printDatabase(true, true, true);
	}
	
	
	public static void testGenDir(){
		System.out.println("");
		Point a = new Point(0, "Stair Bottom", 0, 0, 3);
		Point b = new Point(1, "Stair Top", 1, 0, 3);
		Point c = new Point(2, "Hallway Start", 2, -1, 3);
		Point[] testPoints = new Point[3];
		testPoints[0] = c;
		testPoints[1] = b;
		testPoints[2] = a;
		GenTextDir testGenTestDir = new GenTextDir(); 
		String[] testOneString = testGenTestDir.genTextDir(testPoints);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		
		System.out.println("");
		
		a = new Point(0, "Stair Bottom", 0, 0, 3);
		b = new Point(1, "Stair Top", 1, 0, 3);
		c = new Point(2, "Hallway Start", 2, 1, 3);
		
		testPoints[0] = c;
		testPoints[1] = b;
		testPoints[2] = a;
		
		testOneString = testGenTestDir.genTextDir(testPoints);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		
		System.out.println("");
		
		a = new Point(0, "Stair Bottom", 0, 0, 3);
		b = new Point(1, "Stair Top", 1, 0, 3);
		c = new Point(2, "Hallway Start", 2, 0, 3);
		
		testPoints[0] = c;
		testPoints[1] = b;
		testPoints[2] = a;
		
		testOneString = testGenTestDir.genTextDir(testPoints);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		
		Point p1 = new Point(0, "p1", 0, 0,1);
		Point p2 = new Point(1, "p2", 0, 1,1);
		Point p3 = new Point(2, "p3", 1, 0,1);
		Point p4 = new Point(3, "p4", 2, 0,1);
		Point p5 = new Point(4, "p5", 2, 1,1);
		Point p6 = new Point(5, "p6", 1, 2,1);
		Point p7 = new Point(6, "p7", 0, 2,1);
		Point p8 = new Point(7, "p8", 0, 3,1);
		Point p9 = new Point(8, "p9", 2, 3,1);
		Point p10 = new Point(9, "p9", 3, 1,1);
		Point p11 = new Point(10, "p9", 4, 1,1);
		Point p12 = new Point(11, "p9", 5, 1,1);
		Point p13 = new Point(12, "p9", 6, 1,1);
		Point p14 = new Point(13, "p9", 7, 1,1);
		Point p15 = new Point(14, "p9", 8, 1,1);
		Point[] testPoints2 = new Point[5];
		testPoints2[0] = p8;
		testPoints2[1] = p6;
		testPoints2[2] = p5;
		testPoints2[3] = p2;
		testPoints2[4] = p1;
		
		testOneString = testGenTestDir.genTextDir(testPoints2);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		System.out.println(testOneString[4]);
		System.out.println(testOneString[5]);
		
		System.out.println("");
		
		testPoints2[0] = p8;
		testPoints2[1] = p9;
		testPoints2[2] = p5;
		testPoints2[3] = p2;
		testPoints2[4] = p1;
		
		testOneString = testGenTestDir.genTextDir(testPoints2);
		System.out.println(testOneString[0]);
		System.out.println(testOneString[1]);
		System.out.println(testOneString[2]);
		System.out.println(testOneString[3]);
		System.out.println(testOneString[4]);
		System.out.println(testOneString[5]);
		//int id, String name, int x, int y, int numberEdges
	}
}