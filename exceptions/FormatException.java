package exceptions;

/**
 * When the user input is in the wrong format
 * send this exception.
 *
 * @author Gilad Omesi and Noam Cohen
 */
public class FormatException extends Exception {

    /**
     * exception sender.
     *
     * @param message the message to send
     */
    public FormatException(String message) {
        super(message);
    }
}
