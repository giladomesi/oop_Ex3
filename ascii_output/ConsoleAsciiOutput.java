package ascii_output;

/**
 * Output a 2D array of chars to the console.
 *
 * @author Dan Nirel
 */
public class ConsoleAsciiOutput implements AsciiOutput {
    /**
     * Output the specified 2D array of chars to the console.
     *
     * @param chars the 2D array of chars to output
     */
    @Override
    public void out(char[][] chars) {
        for (char[] aChar : chars) {
            for (char c : aChar) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }
}
