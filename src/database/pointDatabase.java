package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class pointDatabase 
{
	private static String DATABASE_NAME = "pointDatabase.db";
	private static String POINT_SCHEMA = "id integer, xCoord integer, yCoord integer, name String";
	private static Connection connection;
	
	public static void main(String args[]) throws ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");											//Look into exactly what this is intended to do
		connection = null;															//Initialize connection
		try
		{							
			connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_NAME);	// create a database connection
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  										// set timeout to 30 sec.

			statement.executeUpdate("drop table if exists points");
			statement.executeUpdate("create table points ("+ POINT_SCHEMA + ")");
			statement.executeUpdate("insert into points values(1, 23, 45, 'SL105')");
			statement.executeUpdate("insert into points values(2, 46, 98, 'AK206')");
			statement.executeUpdate("insert into points values(3, 90, 12, 'FL120')");
			statement.executeUpdate("insert into points values(4, 16, 25, 'KV121')");
			printDatabase();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());										// if the error message is "out of memory", 
																					// it probably means no database file is found
		}
		finally
		{
			try
			{
			 if(connection != null)
			   connection.close();
			}
			catch(SQLException e)
			{
			 // connection close failed.
			 System.err.println(e);
			}
		}
	}
	
	public void insertPoint()
	{
		
	}
	
	public static void printDatabase()
	{
		try
		{
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from points");
			while(rs.next())														// read the result set
			{
				System.out.println("name = " + rs.getString("name"));
				System.out.println("id = " + rs.getInt("id"));
				System.out.print("xCoord = " + rs.getInt("xCoord"));
				System.out.println(", yCoord = " + rs.getInt("yCoord"));
				System.out.println();
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());										// if the error message is "out of memory", 
																					// it probably means no database file is found
		}
	}

}	
