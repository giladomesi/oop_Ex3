package ascii_art.img_to_char;
import image.Image;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.round;

public class BrightnessImgCharMatcher {
    private final int minBrightness = 0;
    private final int maxBrightness = 255;
    public char[] charSet = {' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'};
    private final Image img;
    private final String fontName;

    public BrightnessImgCharMatcher(Image img, String fontName) {
        this.img = img;
        this.fontName = fontName;
    }

    public char[][] chooseChars(int numCharsInRow, char[] charSet) {
        orderCharsByBrightness(charSet);
        char[][] chars = new char[numCharsInRow][numCharsInRow];
        int pixel_size = img.getHeight() / numCharsInRow;
        Image[][] subImages = img.getRepixelatedImage(pixel_size);

        for (int i = 0; i < numCharsInRow; i++) {
            for (int j = 0; j < numCharsInRow; j++) {
                chars[i][j] = chooseChar(subImages[i][j]);
            }
        }

        return chars;
    }

    private char chooseChar(Image img) {
        int brightness = 0;
        for (Color color : img.pixels()) {
            brightness += getBrightness(color);
        }
        int size = img.getWidth() * img.getHeight() * maxBrightness;
        double pixelBrightness = (double) brightness / (size);
        int index = Math.toIntExact(round(pixelBrightness * (charSet.length - 1)));
        return charSet[index];
    }

    private void orderCharsByBrightness(char[] charSet) {
        Map<Character, Double> brightnessMap = new HashMap<>();

        // Iterate through the charSet and calculate brightness
        for (int i = 0; i < charSet.length; i++) {
            char c = charSet[i];
            if (!brightnessMap.containsKey(c)) {
                brightnessMap.put(c, getBrightness(c));
            }
            double brightness = brightnessMap.get(c);

            // Use a temporary variable to avoid modifying the original charSet during sorting
            char temp = charSet[i];

            // Find the insertion position based on the brightness
            int j = i - 1;
            while (j >= 0 && brightnessMap.get(charSet[j]) > brightness) {
                charSet[j + 1] = charSet[j];
                j--;
            }

            // Place the current character at the correct position
            charSet[j + 1] = temp;
        }

        this.charSet = charSet;
    }

    private double getBrightness(char c) {
        boolean[][] charImg = CharRenderer.getImg(c, 16, fontName);
        int brightness = 0;
        for (boolean[] booleans : charImg) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    brightness++;
                }
            }
        }
        return (double) (brightness - minBrightness) / (maxBrightness - minBrightness);
    }

    private int getBrightness(Color color) {
        return (int) (color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722);
    }

}
