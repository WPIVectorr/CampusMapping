
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
		
		//int id, String name, int x, int y, int numberEdges
	}
}
