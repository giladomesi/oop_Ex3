package ascii_art;

import ascii_output.HtmlAsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import exceptions.BoundariesException;
import exceptions.EmptyCharSetException;
import exceptions.FormatException;
import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Shell for running image asciArt on different images.
 * @author Gilad Omesi Noam Cohen
 */
public class Shell {
    private static final String NEW_COMMAND = ">>> ";
    private static final int MIN_ASCII_CHAR = 32;
    private static final int MAX_ASCII_CHAR = 126;
    private static final int INITIAL_CHARS_IN_ROW = 128;
    private static final String INITIAL_IMAGE = "cat.jpeg";
    private static final int COMMAND_EXIT = 0;
    private static final int COMMAND_CHARS = 1;
    private static final int COMMAND_ADD = 2;
    private static final int COMMAND_REMOVE = 3;
    private static final int COMMAND_RES = 4;
    private static final int COMMAND_IMAGE = 5;
    private static final int COMMAND_OUTPUT = 6;
    private static final int COMMAND_ASCII_ART = 7;
    private static final int CHANGE_COMMANDS_LENGTH = 2;
    private static final int INITIAL_MAX_ASCII_CHAR = 10;
    private static final int MULTI_CHAR_REMOVAL_LENGTH = 3;
    private static final int RES_MULTIPLIER = 2;

    private static final String[] COMMANDS = {"exit", "chars", "add", "remove", "res",
            "image", "output", "asciiArt"};
    private static final String[] SPECIAL_COMMANDS = {"all", "space"};
    private static final String[] RESOLUTION_CHANGE = {"up", "down"};
    private static final String CONSOLE_COMMAND = "console";
    private static final String HTML_COMMAND = "html";
    private static final int ADD_ERROR = 0;
    private static final int REMOVE_ERROR = 1;
    private static final int RES_ERROR = 2;
    private static final int IMAGE_ERROR = 3;
    private static final int OUTPUT_ERROR = 4;
    private static final int BOUNDARIES_ERROR = 5;
    private static final int ASCII_ART_ERROR = 6;
    private static final Map<Integer, String> ERROR_MESSAGES = Map.of(
            ADD_ERROR, "Did not add due to incorrect format",
            REMOVE_ERROR, "Did not remove due to incorrect format",
            RES_ERROR, "Did not change resolution due to incorrect format.",
            IMAGE_ERROR, "Did not execute due to problem with image file.",
            OUTPUT_ERROR, "Did not change output method due to incorrect format.",
            BOUNDARIES_ERROR, "Did not change due to exceeding boundaries",
            ASCII_ART_ERROR, "Did not execute. Charset is empty."
    );
    private static final String RESOLUTION_SETTER_MESSAGE = "Resolution set to ";
    private static final String INVALID_COMMAND_MESSAGE = "Invalid command.";
    private static final String HTML_FILE_NAME = "out.html";
    private static final String FONT_NAME = "Courier New";
    private int minCharsInRow;
    private int maxCharsInRow;
    private int charsInRow;
    private Image img;
    private final SubImgCharMatcher subImgCharMatcher;
    private boolean isConsole;

    private AsciiArtAlgorithm asciiArtAlgo;

    /**
     * Constructor for the shell. Uses initial parameters for charset and image.
     * @throws IOException
     */
    public Shell() throws IOException {
        subImgCharMatcher = new SubImgCharMatcher(new char[0]);
        for (int i = 0; i < INITIAL_MAX_ASCII_CHAR; i++) {
            subImgCharMatcher.addChar((char) ('0' + i));
        }
        this.img = new Image(INITIAL_IMAGE);

        minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        maxCharsInRow = img.getWidth();
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        isConsole = true;

        asciiArtAlgo = new AsciiArtAlgorithm(img, charsInRow, subImgCharMatcher);
    }

    /**
     * Run command for the shell,
     * recieves different commands and acts accordingly.
     */
    public void run()  {
        boolean isActive = true;
        while (isActive) {
            System.out.print(NEW_COMMAND);
            String[] inputs = KeyboardInput.readLine().split(" ", CHANGE_COMMANDS_LENGTH);
            String command = inputs[0];
            try {
                switch (getCommandIndex(command)) {
                    case COMMAND_EXIT: isActive = false; break;
                    case COMMAND_CHARS: System.out.println(subImgCharMatcher.getCharSet()); break;
                    case COMMAND_ADD:
                    case COMMAND_REMOVE: handleAddRemoveCommand(inputs, command); break;
                    case COMMAND_RES: handleResolutionChange(inputs); break;
                    case COMMAND_IMAGE: handleImageChange(inputs); break;
                    case COMMAND_OUTPUT: handleOutputChange(inputs); break;
                    case COMMAND_ASCII_ART: generateAsciiArt(); break;
                    default: System.out.println(INVALID_COMMAND_MESSAGE);
                }
            } catch (Exception e) { System.out.println(e.getMessage()); }
        }
    }
    private int getCommandIndex(String command) {
        for (int i = 0; i < COMMANDS.length; i++) {
            if (command.equals(COMMANDS[i])) {
                return i;
            }
        }
        return -1;
    }

    private void generateAsciiArt() throws EmptyCharSetException {
        if (subImgCharMatcher.getCharSet().length ==0)
        {
            throw new EmptyCharSetException(ERROR_MESSAGES.get(ASCII_ART_ERROR));
        }
        char[][] asciiArt = asciiArtAlgo.run();
        if (isConsole) {
            printToConsole(asciiArt);
        } else {
            saveToHtml(asciiArt);
        }
    }

    private void handleAddRemoveCommand(String[] inputs, String command) throws FormatException {
        boolean isRemove = command.equals(COMMANDS[COMMAND_REMOVE]);
        int error =isRemove? REMOVE_ERROR:ADD_ERROR;
        if (inputs.length != CHANGE_COMMANDS_LENGTH) {
            throw new FormatException(ERROR_MESSAGES.get(error));
        }
        String parameter = inputs[CHANGE_COMMANDS_LENGTH-1];
        if (!addRemoveFunction(parameter, isRemove)) {
            throw new FormatException(ERROR_MESSAGES.get(error));
        }
    }

    private void handleResolutionChange(String[] inputs) throws FormatException, BoundariesException {
        if (inputs.length == CHANGE_COMMANDS_LENGTH &&
                (inputs[1].equals(RESOLUTION_CHANGE[0]) || inputs[1].equals(RESOLUTION_CHANGE[1])))
        {
            updateRes(inputs[1]);
        } else {
            throw new FormatException(ERROR_MESSAGES.get(RES_ERROR));
        }
    }

    private void handleImageChange(String[] inputs) throws IOException, FormatException {
        if (inputs.length == CHANGE_COMMANDS_LENGTH) {
            try {
                this.img = new Image(inputs[1]);
                minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
                maxCharsInRow = img.getWidth();
                charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
                asciiArtAlgo = new AsciiArtAlgorithm(img, charsInRow, subImgCharMatcher);
            } catch (IOException e) {
                throw new IOException(ERROR_MESSAGES.get(IMAGE_ERROR));
            }
        } else {
            throw new FormatException(ERROR_MESSAGES.get(IMAGE_ERROR));
        }
    }

    private void handleOutputChange(String[] inputs) throws FormatException {
        if (inputs.length == CHANGE_COMMANDS_LENGTH) {
            if (Objects.equals(inputs[1], CONSOLE_COMMAND)) {
                isConsole = true;
            } else if (Objects.equals(inputs[1], HTML_COMMAND)) {
                isConsole = false;
            } else {
                throw new FormatException(ERROR_MESSAGES.get(OUTPUT_ERROR));

            }
        } else {
            throw new FormatException(ERROR_MESSAGES.get(OUTPUT_ERROR));
        }
    }

    private void updateRes(String change) throws BoundariesException {
        minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        maxCharsInRow = img.getWidth();
        if (change.equals(RESOLUTION_CHANGE[0])) {
            if (charsInRow * RES_MULTIPLIER > maxCharsInRow) {
                throw new BoundariesException(ERROR_MESSAGES.get(BOUNDARIES_ERROR));
            }
            charsInRow = charsInRow * RES_MULTIPLIER;
        } else if (change.equals(RESOLUTION_CHANGE[1])) {
            if (charsInRow / RES_MULTIPLIER < minCharsInRow) {
                throw new BoundariesException(ERROR_MESSAGES.get(BOUNDARIES_ERROR));
            }
            charsInRow = Math.max(charsInRow / RES_MULTIPLIER, minCharsInRow);
        }
        System.out.println(RESOLUTION_SETTER_MESSAGE + charsInRow);
        asciiArtAlgo = new AsciiArtAlgorithm(img, charsInRow, subImgCharMatcher);
    }

    private boolean addRemoveFunction(String c, boolean isRemove) {
        if (c.equals(SPECIAL_COMMANDS[0])) {
            if (isRemove) {
                for (int i = MIN_ASCII_CHAR; i < MAX_ASCII_CHAR; i++)
                    subImgCharMatcher.removeChar((char) i);
            } else addAll();
            return true;
        }
        if (c.equals(SPECIAL_COMMANDS[1]) || c.length() == 1) {
            addRemoveChar(c.charAt(0), isRemove);
            return true;
        }
        if (c.length() == MULTI_CHAR_REMOVAL_LENGTH && c.charAt(1) == '-') {
            char start = c.charAt(0), end = c.charAt(MULTI_CHAR_REMOVAL_LENGTH-1);
            if (start > end) { char tmp = start; start = end; end = tmp; }
            for (char i = start; i <= end; i++) addRemoveChar(i, isRemove);
            return true;
        }
        return false;
    }

    private void addAll() {
        for (int i = MIN_ASCII_CHAR; i < MAX_ASCII_CHAR; i++) {
            subImgCharMatcher.addChar((char) i);
        }
    }

    private void addRemoveChar(char c, boolean isRemove) {
        if (isRemove) {
            subImgCharMatcher.removeChar(c);
        } else {
            subImgCharMatcher.addChar(c);
        }
    }


    private void printToConsole(char[][] rendered) {
        ConsoleAsciiOutput consoleOutput = new ConsoleAsciiOutput();
        consoleOutput.out(rendered);
    }

    private void saveToHtml(char[][] rendered) {
        HtmlAsciiOutput htmlOutput = new HtmlAsciiOutput(HTML_FILE_NAME, FONT_NAME);
        htmlOutput.out(rendered);
    }

    /**
     * Main functoin.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Shell shell = new Shell();
        shell.run();
    }
}
