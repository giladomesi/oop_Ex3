package image_char_matching;

import java.util.TreeMap;
import java.util.TreeSet;

import static image_char_matching.CharConverter.DEFAULT_PIXEL_RESOLUTION;

/**
 * A class that matches a character to a brightness value.
 */
public class SubImgCharMatcher {

    private char[] charSet;
    private final TreeMap<Double, TreeSet<Character>> charBrightnessMap;
    private TreeMap<Double, TreeSet<Character>> normalizedMap;

    private double minBrightness = DEFAULT_PIXEL_RESOLUTION * DEFAULT_PIXEL_RESOLUTION;
    private double maxBrightness = 0;

    /**
     * Constructor for the SubImgCharMatcher class.
     *
     * @param charset the character set to use
     */
    public SubImgCharMatcher(char[] charset) {
        this.charSet = charset;
        charBrightnessMap = new TreeMap<>();
        updateBrightnessMap();
        updateNormalizedMap();
    }

    /**
     * Getter for the char set.
     * returns char set sorted by ascii.
     *
     * @return char set
     */
    public char[] getCharSet() {
        char[] sortedCharSet = charSet.clone();
        for (int i = 0; i < sortedCharSet.length; i++) {
            for (int j = i + 1; j < sortedCharSet.length; j++) {
                if (sortedCharSet[i] > sortedCharSet[j]) {
                    char temp = sortedCharSet[i];
                    sortedCharSet[i] = sortedCharSet[j];
                    sortedCharSet[j] = temp;
                }
            }
        }
        return sortedCharSet;
    }

    /**
     * Returns the character that best matches the given brightness.
     *
     * @param brightness the brightness to match
     * @return char
     */
    public char getCharByImageBrightness(double brightness) {
        if (normalizedMap.containsKey(brightness)) {
            return normalizedMap.get(brightness).first();
        }

        double diff = 1;
        double key_match = normalizedMap.lastKey();
        for (double key : normalizedMap.keySet()) {
            if (Math.abs(key - brightness) == diff) {
                key_match = getMinCharForBrightness(key, key_match);
            }
            if (Math.abs(key - brightness) < diff) {
                diff = Math.abs(key - brightness);
                key_match = key;
            }
        }

        return normalizedMap.get(key_match).first();
    }

    private double getMinCharForBrightness(double key1, double key2) {
        char char1 = normalizedMap.get(key1).first();
        char char2 = normalizedMap.get(key2).first();

        return char1 < char2 ? key1 : key2;
    }

    // sync the brightness map with the character set
    private void updateBrightnessMap() {
        charBrightnessMap.clear();
        for (char c : charSet) {
            addCharToBrightnessMap(c);
        }
    }

    /**
     * Adds a character to the character set.
     *
     * @param c the character to add
     */
    public void addChar(char c) {
        if (new String(charSet).contains(String.valueOf(c))) {
            return;
        }
        char[] newCharSet = new char[charSet.length + 1];
        System.arraycopy(charSet, 0, newCharSet, 0, charSet.length);
        newCharSet[charSet.length] = c;
        charSet = newCharSet;
        addCharToBrightnessMap(c);
        updateNormalizedMap();
    }

    // Add a character to the brightness map
    private void addCharToBrightnessMap(char c) {
        double newBrightness = getBrightness(c);
        if (!charBrightnessMap.containsKey(newBrightness)) {
            charBrightnessMap.put(newBrightness, new TreeSet<>());
        }
        charBrightnessMap.get(newBrightness).add(c);

        if (newBrightness < minBrightness) {
            minBrightness = newBrightness;
        } else if (newBrightness > maxBrightness) {
            maxBrightness = newBrightness;
        }
    }

    /**
     * Removes a character from the character set.
     *
     * @param c the character to remove
     */
    public void removeChar(char c) {
        int index = 0;
        while (charSet.length > index) {
            if (charSet[index] == c) {
                char tmp = charSet[index];
                charSet[index] = charSet[0];
                charSet[0] = tmp;

                char[] newCharSet = new char[charSet.length - 1];
                System.arraycopy(charSet, 1, newCharSet, 0, charSet.length - 1);
                charSet = newCharSet;
                removeCharFromBrightnessMap(c);
                updateNormalizedMap();
                return;
            }
            index++;
        }
    }

    // Remove a character from the brightness map
    private void removeCharFromBrightnessMap(char c) {
        if (charSet.length == 0) {
            minBrightness = DEFAULT_PIXEL_RESOLUTION * DEFAULT_PIXEL_RESOLUTION;
            maxBrightness = 0;
            charBrightnessMap.clear();
            return;
        }

        double brightness = getBrightness(c);
        charBrightnessMap.get(brightness).remove(c);
        if (charBrightnessMap.get(brightness).isEmpty()) {
            charBrightnessMap.remove(brightness);
            if (brightness == minBrightness) {
                minBrightness = charBrightnessMap.firstKey();
            } else if (brightness == maxBrightness) {
                maxBrightness = charBrightnessMap.lastKey();
            }
        }
    }

    // Brightness values for a single character, based on the boolean array.
    private int getBrightness(char c) {
        boolean[][] charImg = CharConverter.convertToBoolArray(c);
        int brightness = 0;
        for (boolean[] booleans : charImg) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    brightness++;
                }
            }
        }
        return brightness;
    }

    // Update the normalized map
    private void updateNormalizedMap() {
        TreeMap<Double, TreeSet<Character>> newNormalizedMap = new TreeMap<>();
        for (double key : charBrightnessMap.keySet()) {
            double normalizedKey = (key - minBrightness) / (maxBrightness - minBrightness);
            newNormalizedMap.put(normalizedKey, charBrightnessMap.get(key));
        }
        normalizedMap = newNormalizedMap;
    }
}
