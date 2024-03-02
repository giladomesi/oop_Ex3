package ascii_output;

/**
 * An object implementing this interface can output a 2D array of chars
 * in some fashion.
 *
 * @author Dan Nirel
 */
public interface AsciiOutput {
    /**
     * Output the specified 2D array of chars
     *
     * @param chars the 2D array of chars to output
     */
    void out(char[][] chars);
}
