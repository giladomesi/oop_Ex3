package exceptions;

/**
 * When the user input is trying to use an empty charset
 * use this exception.
 *
 * @author Gilad Omesi and Noam Cohen
 */
public class EmptyCharSetException extends Exception{
    /**
     * exception sender.
     * @param message
     */
    public EmptyCharSetException(String message) {
        super(message);
    }
}
