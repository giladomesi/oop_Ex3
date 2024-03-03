package image_char_matching;

import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;

public class SubImgCharMatcher {

    private static final int NORMALIZATION_FACTOR = 256;
    private TreeMap<Double, TreeSet<Character>> normalizedCharMap;
    private TreeMap<Double, TreeSet<Character>> charBrightnessMap;
    private double maxBrightness, minBrightness;

    public SubImgCharMatcher(char[] charset) {
        this.charBrightnessMap = new TreeMap<>();
        this.normalizedCharMap = new TreeMap<>();
        for (char currentChar : charset) {
            double key = getCharBrightness(currentChar);
            this.addCharHelperFunction(key, currentChar, charBrightnessMap);
        }
        this.maxBrightness = this.charBrightnessMap.lastKey();
        this.minBrightness = this.charBrightnessMap.firstKey();

        initNormalizeCharMap();
    }
    // public TreeMap<Double,TreeSet<Character>> getNormalized (){// FOor testing
    // return this.normalizedCharMap;
    // }

    public char getCharByImageBrightness(double brightness) {
        double normalizedBrightness = calculateNormalizedBrightness(brightness);

        var ceilKey = this.normalizedCharMap.ceilingKey(normalizedBrightness);
        var floorKey = this.normalizedCharMap.floorKey(normalizedBrightness);

        if (ceilKey != null && floorKey != null) {
            if (Math.abs(ceilKey - normalizedBrightness) > Math.abs(floorKey - normalizedBrightness)) {
                return this.normalizedCharMap.get(floorKey).first();
            } else {
                return this.normalizedCharMap.get(ceilKey).first();
            }
        }

        if (ceilKey == null && floorKey != null) {
            return this.normalizedCharMap.get(floorKey).first();
        } else {
            return this.normalizedCharMap.get(ceilKey).first();
        }
    }

    public char[] getCharSet() {
        char[] l = new char[] { 'a', 'b', 'c' };
        return l;
    }

    private double calculateNormalizedBrightness(double key) {
        double deltaBrightness = this.maxBrightness - this.minBrightness;
        return (key - this.minBrightness) / (deltaBrightness);

    }

    private void initNormalizeCharMap() {
        for (Map.Entry<Double, TreeSet<Character>> entry : charBrightnessMap.entrySet()) {
            TreeSet<Character> currCharSet = entry.getValue();
            for (char currentChar : currCharSet) {
                double charBrightness = entry.getKey();
                this.addCharHelperFunction(calculateNormalizedBrightness(charBrightness), currentChar,
                        normalizedCharMap);
            }
        }
    }

    private void addCharHelperFunction(double key, char currChar, TreeMap<Double, TreeSet<Character>> tm) {
        if (tm.get(key) != null) {
            tm.get(key).add(currChar);
        } else {
            TreeSet<Character> set = new TreeSet<>();
            set.add(currChar);
            tm.put(key, set);
        }
    }

    public void addChar(char c) {
        double key = getCharBrightness(c);
        if (key < this.maxBrightness && key > this.minBrightness) {
            addCharHelperFunction(key, c, this.charBrightnessMap);
            this.addCharHelperFunction(calculateNormalizedBrightness(key), c, normalizedCharMap);
        } else {
            if (key > this.maxBrightness) {
                this.maxBrightness = key;
            } else {
                this.minBrightness = key;
            }
            addCharHelperFunction(key, c, charBrightnessMap);
            this.normalizedCharMap.clear();
            initNormalizeCharMap();
        }
    }

    public void removeChar(char c) {
        // activate the ascii-score function and then remove the key where key is the
        // ascii score
        double key = getCharBrightness(c);

        if (this.charBrightnessMap.get(key).size() == 1) {
            this.charBrightnessMap.remove(key);
            this.normalizedCharMap.remove(calculateNormalizedBrightness(key));
            if (key == this.maxBrightness || key == this.minBrightness) {
                if (key == this.maxBrightness) {
                    this.maxBrightness = this.charBrightnessMap.lastKey();
                } else {
                    this.minBrightness = this.charBrightnessMap.firstKey();
                }
                this.normalizedCharMap.clear();
                initNormalizeCharMap();
            }
        } else {
            this.charBrightnessMap.get(key).remove(c);
            this.normalizedCharMap.get(calculateNormalizedBrightness(key)).remove(c);
        }
    }

    private double getCharBrightness(char c) {
        double true_counter = 0;
        boolean[][] array = CharConverter.convertToBoolArray(c);
        for (int row = 0; row < array.length; row++) {
            for (int col = 0; col < array[row].length; col++) {
                if (array[row][col] == true) {
                    true_counter++;
                }
            }
        }
        return true_counter / NORMALIZATION_FACTOR;
    }
}

// package image_char_matching;

// import java.util.TreeMap;
// import java.util.TreeSet;

// import static image_char_matching.CharConverter.DEFAULT_PIXEL_RESOLUTION;

// /**
// * A class that matches a character to a brightness value.
// */
// public class SubImgCharMatcher {

// private char[] charSet;
// private final TreeMap<Double, TreeSet<Character>> charBrightnessMap;
// private double minBrightness = DEFAULT_PIXEL_RESOLUTION *
// DEFAULT_PIXEL_RESOLUTION;
// private double maxBrightness = 0;

// /**
// * Constructor for the SubImgCharMatcher class.
// *
// * @param charset the character set to use
// */
// public SubImgCharMatcher(char[] charset) {
// this.charSet = charset;
// charBrightnessMap = new TreeMap<>();
// updateBrightnessMap();
// }

// /**
// * Getter for the char set.
// *
// * @return char set
// */
// public char[] getCharSet() {
// return charSet;
// }

// /**
// * Returns the character that best matches the given brightness.
// *
// * @param brightness the brightness to match
// * @return char
// */
// public char getCharByImageBrightness(double brightness) {
// TreeMap<Double, TreeSet<Character>> normalizedMap =
// getNormalizingCharBrightnessMap();
// if (normalizedMap.containsKey(brightness)) {
// return normalizedMap.get(brightness).first();
// }

// double diff = 1;
// double key_match = normalizedMap.lastKey();
// for (double key : normalizedMap.keySet()) {
// if (Math.abs(key - brightness) == diff) {
// key_match = getMinCharForBrightness(normalizedMap, key, key_match);
// }
// if (Math.abs(key - brightness) < diff) {
// diff = Math.abs(key - brightness);
// key_match = key;
// }
// }

// return normalizedMap.get(key_match).first();
// }

// private double getMinCharForBrightness(TreeMap<Double, TreeSet<Character>>
// normalizedMap,
// double key1, double key2) {
// char char1 = normalizedMap.get(key1).first();
// char char2 = normalizedMap.get(key2).first();

// return char1 < char2 ? key1 : key2;
// }

// // sync the brightness map with the character set
// private void updateBrightnessMap() {
// charBrightnessMap.clear();
// for (char c : charSet) {
// addCharToBrightnessMap(c);
// }
// }

// /**
// * Adds a character to the character set.
// *
// * @param c the character to add
// */
// public void addChar(char c) {
// if (new String(charSet).contains(String.valueOf(c))) {
// return;
// }
// char[] newCharSet = new char[charSet.length + 1];
// System.arraycopy(charSet, 0, newCharSet, 0, charSet.length);
// newCharSet[charSet.length] = c;
// charSet = newCharSet;
// addCharToBrightnessMap(c);
// }

// // Add a character to the brightness map
// private void addCharToBrightnessMap(char c) {
// double newBrightness = getBrightness(c);
// if (!charBrightnessMap.containsKey(newBrightness)) {
// charBrightnessMap.put(newBrightness, new TreeSet<>());
// }
// charBrightnessMap.get(newBrightness).add(c);

// if (newBrightness < minBrightness) {
// minBrightness = newBrightness;
// } else if (newBrightness > maxBrightness) {
// maxBrightness = newBrightness;
// }
// }

// /**
// * Removes a character from the character set.
// *
// * @param c the character to remove
// */
// public void removeChar(char c) {
// int index = 0;
// while (charSet.length > index) {
// if (charSet[index] == c) {
// char tmp = charSet[index];
// charSet[index] = charSet[0];
// charSet[0] = tmp;

// char[] newCharSet = new char[charSet.length - 1];
// System.arraycopy(charSet, 1, newCharSet, 0, charSet.length - 1);
// charSet = newCharSet;
// removeCharFromBrightnessMap(c);
// return;
// }
// index++;
// }
// }

// // Remove a character from the brightness map
// private void removeCharFromBrightnessMap(char c) {
// if (charSet.length == 0) {
// minBrightness = DEFAULT_PIXEL_RESOLUTION * DEFAULT_PIXEL_RESOLUTION;
// maxBrightness = 0;
// charBrightnessMap.clear();
// return;
// }

// double brightness = getBrightness(c);
// charBrightnessMap.get(brightness).remove(c);
// if (charBrightnessMap.get(brightness).isEmpty()) {
// charBrightnessMap.remove(brightness);
// if (brightness == minBrightness) {
// minBrightness = charBrightnessMap.firstKey();
// } else if (brightness == maxBrightness) {
// maxBrightness = charBrightnessMap.lastKey();
// }
// }
// }

// // Brightness values for a single character, based on the boolean array.
// private int getBrightness(char c) {
// boolean[][] charImg = CharConverter.convertToBoolArray(c);
// int brightness = 0;
// for (boolean[] booleans : charImg) {
// for (boolean aBoolean : booleans) {
// if (aBoolean) {
// brightness++;
// }
// }
// }
// return brightness;
// }

// // returns TreeMap of brightness values and the corresponding characters
// // after normalizing the brightness values
// private TreeMap<Double, TreeSet<Character>> getNormalizingCharBrightnessMap()
// {
// TreeMap<Double, TreeSet<Character>> normalizedMap = new TreeMap<>();
// for (double key : charBrightnessMap.keySet()) {
// double normalizedKey = (key - minBrightness) / (maxBrightness -
// minBrightness);
// normalizedMap.put(normalizedKey, charBrightnessMap.get(key));
// }
// return normalizedMap;
// }
// }
