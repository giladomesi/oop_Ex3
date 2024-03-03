package ascii_art;

import java.util.Scanner;

public class KeyboardInputTests
{
    private static KeyboardInputTests keyboardInputObject = null;
    private Scanner scanner;
    
    private KeyboardInputTests()
    {
        this.scanner = new Scanner(System.in);
    }

    public static KeyboardInputTests getObject()
    {
        if(KeyboardInputTests.keyboardInputObject == null)
        {
            KeyboardInputTests.keyboardInputObject = new KeyboardInputTests();
        }
        return KeyboardInputTests.keyboardInputObject;
    }

    public static void Refresh() {
        KeyboardInputTests.getObject().scanner.close();
        KeyboardInputTests.getObject().scanner = new Scanner(System.in);
    }

    public static String readLine()
    {
        return KeyboardInputTests.getObject().scanner.nextLine().trim();
    }
}