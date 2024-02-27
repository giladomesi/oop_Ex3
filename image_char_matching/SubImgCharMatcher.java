package image_char_matching;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * A class that matches a character to a brightness value.
 * 
 */
public class SubImgCharMatcher {

    private char[] charSet;
    private Map<Character, Double> brightnessMap;
    private double minBrightness;
    private double maxBrightness;

    /**
     * 
     * Constructor for the SubImgCharMatcher class.
     * 
     * @param charset
     */
    public SubImgCharMatcher(char[] charset) {
        this.charSet = charset;
        brightnessMap = new HashMap<>();
    }

    /**
     * 
     * Returns the character that best matches the given brightness.
     * 
     * @param brightness
     * @return char
     */
    public char getCharByImageBrightness(double brightness) {
        orderCharsByBrightness(charSet);
        int index = Math.toIntExact(Math.round(brightness * (charSet.length - 1)));
        return charSet[index];
    }

    /**
     * 
     * Adds a character to the character set.
     * 
     * @param c
     */
    public void addChar(char c) {
        char[] newCharSet = new char[charSet.length + 1];
        System.arraycopy(charSet, 0, newCharSet, 0, charSet.length);
        newCharSet[charSet.length] = c;
        charSet = newCharSet;
        addCharToMap(c);
    }

    private void addCharToMap(char c) {
        double newBrightness = getBrightness(c);
        brightnessMap.put(c, newBrightness);
        if (newBrightness < minBrightness){
            minBrightness = newBrightness;
        } else if (newBrightness > maxBrightness){
            maxBrightness = newBrightness;
        }
    }


    /**
     * 
     * Removes a character from the character set.
     * 
     * @param c
     */
    public void removeChar(char c) {
        char[] newCharSet = new char[charSet.length - 1];
        int j = 0;
        for (char c1 : charSet) {
            if (c1 != c) {
                newCharSet[j++] = c1;
            }
        }
        charSet = newCharSet;

    }

    // Brightness values for a single character, based on the noolean array.
    private double getBrightness(char c) {
        boolean[][] charImg = CharConverter.convertToBoolArray(c);
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

    // Order the characters by brightness
    private void orderCharsByBrightness(char[] charSet) {
        // Iterate through the charSet and calculate brightness
        for (int i = 0; i < charSet.length; i++) {
            char c = charSet[i];
            if (!brightnessMap.containsKey(c)) {
                addCharToMap(c);
            }
            double brightness = brightnessMap.get(c);
            // Use a temporary variable to avoid modifying the original charSet during
            // sorting
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
}
