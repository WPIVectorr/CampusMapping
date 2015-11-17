package database;

public class AlreadyExistsException extends Exception{
	public AlreadyExistsException(String message)
	{
		super(message);
	}
}
