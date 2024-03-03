package ascii_art;

import java.util.Scanner;

/**
 * A class for reading input from the keyboard.
 */
class KeyboardInput {
    private static KeyboardInput keyboardInputObject = null;
    private final Scanner scanner;

    // Private constructor to prevent client from creating more than one instance.
    private KeyboardInput() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns the single instance of the class.
     */
    public static KeyboardInput getObject() {
        if (KeyboardInput.keyboardInputObject == null) {
            KeyboardInput.keyboardInputObject = new KeyboardInput();
        }
        return KeyboardInput.keyboardInputObject;
    }

    /**
     * Reads a line from the keyboard.
     */
    public static String readLine() {
        return KeyboardInput.getObject().scanner.nextLine().trim();
    }
}