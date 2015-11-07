package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class pointDatabase 
{
	private static Connection connection;
	
	public static void main(String args[]) throws ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");											//Look into exactly what this is intended to do
		connection = null;
		try
		{
																					// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:pointDatabase.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  										// set timeout to 30 sec.

			statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("create table person (id integer, name string)");
			statement.executeUpdate("insert into person values(1, 'Tyrome')");
			statement.executeUpdate("insert into person values(2, 'Paula')");
			ResultSet rs = statement.executeQuery("select * from person");
			while(rs.next())														// read the result set
			{
				System.out.println("name = " + rs.getString("name"));
				System.out.println("id = " + rs.getInt("id"));
			}
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
}	
