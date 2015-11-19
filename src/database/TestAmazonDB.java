package database;

import java.sql.*;

public class TestAmazonDB {

	//-----------------------------------------------------------Constants--------------------------------------------------------------------
		private static String DATABASE_URL = "jdbc:mysql://campusmapping.c5bohpl1biax.us-west-2.rds.amazonaws.com:3306/";	
		private static String DATABASE_NAME = "campusMapping_db";
		private static String userName = "Vectorr";
		private static String password = "mag";
		
		private static String MAP_TABLE_NAME = "Maps";
		private static String POINT_TABLE_NAME = "Points";
		private static String EDGE_TABLE_NAME = "WeightedEdges";
		
		private static String MAP_SCHEMA = "id INTEGER, name VARCHAR(30)";
		private static String POINT_SCHEMA = "id integer, name String, x integer, y integer, numEdges integer, idEdge1 integer,"
				+ " idEdge2 integer, idEdge3 integer, idEdge4 integer, idEdge5 integer, idEdge6 integer, idEdge7 integer, idEdge8 integer,"
				+ "idEdge9 integer, idEdge10 integer";
		private static String EDGE_SCHEMA = "id VARCHAR(30), idPoint1 INTEGER, idPoint2 INTEGER, weight INTEGER, isOutside BOOLEAN, isStairs INTEGER";
		
		
	public static void main (String args[])
	{
		createDB();																										//Create database if it does not already exist
		Connection connection = connect();																				
		Statement statement;
		
		try {																											//Add Map and Edge tables if they don't exist
			statement = connection.createStatement();
			statement.setQueryTimeout(5);  										
			System.out.println("Creating new tables");
			statement.executeUpdate("create table if not exists "+ MAP_TABLE_NAME +" ("+ MAP_SCHEMA + ")");
			statement.executeUpdate("create table if not exists "+ EDGE_TABLE_NAME +" ("+ EDGE_SCHEMA + ")");
			System.out.println("Finished ensuring existence of table(s):"+EDGE_TABLE_NAME + ", "+MAP_TABLE_NAME);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {																											//Print all existing table names
			printTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createDB()
	{
		Connection connection = null;
		try {
			 Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(DATABASE_URL, userName, password);
		} catch (SQLException e) {
			System.out.println("Failed to get connection to amazon server");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS "+DATABASE_NAME);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection connect()
	{
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DATABASE_URL + DATABASE_NAME, userName, password);
		} catch (SQLException e) {
			System.out.println("Failed to get connection to amazon server");
			e.printStackTrace();
		}
		return connection;
	}

	public static void printTables() throws SQLException
	{
		Connection connection = null;
		ResultSet rs = null;
		String selectTableNames = "SELECT table_name FROM information_schema.tables WHERE table_schema='"+DATABASE_NAME+"'";
		try {
			connection = DriverManager.getConnection(DATABASE_URL + DATABASE_NAME, userName, password);
		} catch (SQLException e) {
			System.out.println("Failed to get connection to amazon server");
			e.printStackTrace();
		}
		try {
			rs = connection.createStatement().executeQuery(selectTableNames);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (rs.next())
		{
			System.out.println(rs.getString(1));
		}
		
	}
}
