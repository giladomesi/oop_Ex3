package ascii_output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Output a 2D array of chars to an HTML file viewable in a web browser.
 *
 * @author Dan Nirel
 */
public class HtmlAsciiOutput implements AsciiOutput {
    private static final double BASE_LINE_SPACING = 0.8;
    private static final double BASE_FONT_SIZE = 150.0;

    private final String fontName;
    private final String filename;

    /**
     * Constructor for the HTML ASCII output.
     *
     * @param filename the name of the file to write to
     * @param fontName the name of the font to use
     */
    public HtmlAsciiOutput(String filename, String fontName) {
        this.fontName = fontName;
        this.filename = filename;
    }

    /**
     * Output the specified 2D array of chars to an HTML file.
     *
     * @param chars the 2D array of chars to output
     */
    @Override
    public void out(char[][] chars) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.format(
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<body style=\"" +
                            "\tCOLOR:#000000;" +
                            "\tTEXT-ALIGN:center;" +
                            "\tFONT-SIZE:1px;\">\n" +
                            "<p style=\"" +
                            "\twhite-space:pre;" +
                            "\tFONT-FAMILY:%s;" +
                            "\tFONT-SIZE:%frem;" +
                            "\tLETTER-SPACING:0.15em;" +
                            "\tLINE-HEIGHT:%fem;\">\n",
                    fontName, BASE_FONT_SIZE / chars[0].length, BASE_LINE_SPACING));

            for (char[] aChar : chars) {
                for (char c : aChar) {
                    String htmlRep;
                    switch (c) {
                        case '<':
                            htmlRep = "&lt;";
                            break;
                        case '>':
                            htmlRep = "&gt;";
                            break;
                        case '&':
                            htmlRep = "&amp;";
                            break;
                        default:
                            htmlRep = String.valueOf(c);
                    }
                    writer.write(htmlRep);
                }
                writer.newLine();
            }
            writer.write(
                "</p>\n"+
                "</body>\n"+
                "</html>\n");
        } catch(IOException e) {
            Logger.getGlobal().severe(String.format("Failed to write to \"%s\"", filename));
        }
    }
}
