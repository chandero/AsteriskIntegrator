// Source File Name:   MigrationException.java

package exceptions;


public class MigrationException extends Exception
{

    public MigrationException()
    {
    }

    public MigrationException(String message)
    {
        super(message);
    }

    public MigrationException(Throwable cause)
    {
        super(cause);
    }

    public MigrationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
