package exceptions;

/**
 * When the request for boundaries change is out of bounds
 * Use this exception
 *
 * @author Gilad Omesi Noam Cohen
 */
public class BoundariesException extends Exception {
    /**
     * exception sender.
     * @param message
     */
    public BoundariesException(String message) {
        super(message);
    }
}