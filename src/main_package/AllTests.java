package main_package;
import database.ServerDB;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({})
public class AllTests {
	ServerDB testDatabase = ServerDB.getInstance();
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
	/*
	public void testInsertMap(){
		
	}
	public void testInsertPoint(){
		
	}
	public void testInsertEdge(){
		
	}
	public void testGetPoints(){
		
	}
	public void testGetMaps(){
		
	}
	public void testCheckObjectExists(){
		
	}
	public void testResultSetEmpty(){
		
	}
	public void testClearDatabase(){
		
	}
	public void testPopulateFromDatabase(){
		
	}
	public void testGetPoint(){
		
	}
	public void testUpdatePoint(){
		
	}
	public void testAStar(){
		
	}*/
	
	
	public void testGenTextDir() throws Exception {
		GenTextDir genTextDir = new GenTextDir();
		ArrayList<Point> testPoints = new ArrayList<Point>();
		testPoints.add(p8);
		testPoints.add(p6);
		testPoints.add(p5);
		testPoints.add(p2);
		testPoints.add(p1);
		//String[] testOneString = genTextDir.genTextDir(testPoints);
		
		//AssertEquals("Start your path at p1", testOneString[0]);
		//AssertEquals("Travel towards: p2", testOneString[1]);
	}
	
	public boolean AssertEquals(String s1, String s2){
		return s1.equals(s2);
	}
}
