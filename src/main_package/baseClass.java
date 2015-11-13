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
	}
	public static void testDatabase ()
	{
		MappingDatabase testDB = new MappingDatabase();
		testDB.initDatabase();
		testDB.testInsert();
		//testDB.printDatabase(true, true, true);
	}
}
