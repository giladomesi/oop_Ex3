package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import image.Image;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;


public class Shell {
    private static final String NEW_COMMAND = "<<< ";
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final String FONT_NAME = "Courier New";

    private final HashSet<Character> charSet = new HashSet<>();
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final Image img;
    private boolean isConsole;

    public Shell(Image img) {
        for (int i = 0; i < 10; i++) {
            charSet.add((char) ('0' + i));
        }
        this.img = img;

        minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        isConsole = false;
    }

    public void run() {
        boolean isActive = true;
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object

        while (isActive) {
            System.out.print(NEW_COMMAND);
            String input = myObj.nextLine();

            String[] inputs = input.split(" ");
            switch (inputs[0]) {
                case "exit":
                    isActive = false;
                    break;
                case "add":
                    if ((inputs.length != 2) || (!addRemoveFunction(inputs[1], false))) {
                        System.out.println("Did not add due to incorrect format");
                    }
                    break;
                case "remove":
                    if ((inputs.length != 2) || (!addRemoveFunction(inputs[1], true))) {
                        System.out.println("Did not remove due to incorrect format");
                    }
                    break;
                case "res":
                    if (inputs.length == 2) {
                        updateRes(inputs[1]);
                    } else {
                        System.out.println("Did not change due to incorrect format");
                    }
                    break;
                case "render":
                    BrightnessImgCharMatcher charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
                    char[][] rendered = charMatcher.chooseChars(charsInRow, getCharSet());
                    if (isConsole) {
                        printToConsole(rendered);
                    } else {
                        saveToHtml(rendered);
                    }
                    break;
                case "chars":
                    System.out.println(charSet);
                    break;
                case "console":
                    isConsole = true;
                    break;
                case "help":
                    System.out.println("""
                            exit - exit the program
                            console - print the current set of characters
                            add <chars> - add the given characters to the set
                            remove <chars> - remove the given characters from the set
                            res <up/down> - increase/decrease the resolution
                            render <filename> - render the image to the given file
                            chars - print the current set of characters
                            help - print this help message""");
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }


    private void updateRes(String change) {
        if (change.equals("up")) {
            if (charsInRow * 2 > maxCharsInRow) {
                System.out.println("Did not change due to exceeding boundaries");
                return;
            }
            charsInRow = Math.min(charsInRow * 2, maxCharsInRow);
            System.out.println("Width set to " + charsInRow);
        } else if (change.equals("down")) {
            if (charsInRow / 2 < minCharsInRow) {
                System.out.println("Did not change due to exceeding boundaries");
                return;
            }
            charsInRow = Math.max(charsInRow / 2, minCharsInRow);
            System.out.println("Width set to " + charsInRow);
        } else {
            System.out.println("Invalid input");
        }

    }

    private boolean addRemoveFunction(String c, boolean isRemove) {
        if (c.equals("all")) {
            if (isRemove) {
                charSet.clear();
            } else {
                addAll();
            }
            return true;
        } else if (c.equals("space")) {
            addRemoveChar(' ', isRemove);
            return true;
        } else if ((c.length() == 3) && (c.charAt(1) == '-')) {
            char start = c.charAt(0);
            char end = c.charAt(2);
            if (start > end) {
                char tmp = start;
                start = end;
                end = tmp;
            }
            for (char i = start; i <= end; i++) {
                addRemoveChar(i, isRemove);
            }
            return true;

        } else if (c.length() == 1) {
            addRemoveChar(c.charAt(0), isRemove);
            return true;
        }
        return false;
    }

    private void addAll() {
        for (int i = 0; i < 128; i++) {
            charSet.add((char) i);
        }
    }

    private void addRemoveChar(char c, boolean isRemove) {
        if (isRemove) {
            charSet.remove(c);
        } else {
            charSet.add(c);
        }
    }

    private char[] getCharSet() {
        char[] charSetArr = new char[charSet.size()];
        int i = 0;
        for (char c : charSet) {
            charSetArr[i] = c;
            i++;
        }
        return charSetArr;
    }

    private void printToConsole(char[][] rendered) {
        for (char[] chars : rendered) {
            for (int j = 0; j < rendered[0].length; j++) {
                System.out.print(chars[j]);
            }
            System.out.println();
        }
    }

    private void saveToHtml(char[][] rendered) {
        String htmlStart = """
                <html>
                <head>
                <style>
                body {background-color: black;}
                </style>
                </head>
                <body>
                """;
        String htmlEnd = "</body>\n" +
                "</html>";
        StringBuilder htmlBody = new StringBuilder();
        for (char[] chars : rendered) {
            for (int j = 0; j < rendered[0].length; j++) {
                htmlBody.append(chars[j]);
            }
            htmlBody.append("<br>\n");
        }
        String html = htmlStart + htmlBody + htmlEnd;
        try {
            FileWriter myWriter = new FileWriter("out.html");
            myWriter.write(html);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
