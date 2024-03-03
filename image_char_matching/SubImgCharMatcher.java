package image_char_matching;

import java.util.*;

/**
 * The SubImgCharMatcher class provides methods to match characters to image
 * brightness values using a
 * custom character set.
 */
public class SubImgCharMatcher {

    /**
     * The default space character.
     */
    private static final char SPACE_CHAR = ' ';
    /**
     * Index of the first character in an array or string.
     */
    private static final int FIRST_INDEX = 0;
    /**
     * init a counter value to 0
     */
    private static final int INITIAL_COUNTER = 0;
    /**
     * init a counter value to 1
     */
    private static final int NEXT_COUNTER = 1;

    /**
     * The character brightness history map.
     */
    private static final HashMap<Character, Float> charBrightnessHistoryMap = new HashMap<>();

    /**
     * The mapping of normalized brightness values to characters.
     */
    private final TreeMap<Float, List<Character>> floatToCharsMap;

    /**
     * The minimum character brightness value.
     */
    private float minCharBrightness = Float.MAX_VALUE;

    /**
     * The maximum character brightness value.
     */
    private float maxCharBrightness = Float.MIN_VALUE;

    /**
     * Constructs a SubImgCharMatcher object with the specified character set.
     *
     * @param charset an array of characters forming the character set for the
     *                algorithm.
     */
    public SubImgCharMatcher(char[] charset) {
        this.floatToCharsMap = new TreeMap<>();
        for (char c : charset) {
            charBrightnessHistoryMap.put(c, getCharBrightness(c));
            updateMin(c);
            updateMax(c);
        }
        initNormalMap();
    }

    /**
     * Retrieves the character associated with the specified image brightness value.
     *
     * @param brightness the brightness value of the image.
     * @return the character corresponding to the given brightness value.
     * @throws RuntimeException if the character set is empty.
     */
    public char getCharByImageBrightness(double brightness) throws RuntimeException {
        if (floatToCharsMap.isEmpty()) {
            throw new RuntimeException("Character set is empty");
        }
        if (floatToCharsMap.containsKey((float) brightness)) {
            return asciiMinValue(floatToCharsMap.get((float) brightness));
        }
        Map.Entry<Float, List<Character>> closestBrightnessDown = floatToCharsMap.lowerEntry((float) brightness);
        Map.Entry<Float, List<Character>> closestBrightnessUp = floatToCharsMap.higherEntry((float) brightness);

        float disFromUp = (closestBrightnessUp != null) ? Math.abs(closestBrightnessUp.getKey() - (float) brightness)
                : Float.MAX_VALUE;
        float disFromDown = (closestBrightnessDown != null)
                ? Math.abs(closestBrightnessDown.getKey() - (float) brightness)
                : Float.MAX_VALUE;

        if (closestBrightnessUp != null && disFromUp < disFromDown) {
            return asciiMinValue(closestBrightnessUp.getValue());
        } else if (closestBrightnessDown != null) {
            return asciiMinValue(closestBrightnessDown.getValue());
        }
        return SPACE_CHAR;
    }

    /**
     * Adds a character to the character set.
     *
     * @param c The character to add.
     */
    public void addChar(char c) {
        float cNormalBrightness = normalBrightness(c);
        if (floatToCharsMap.containsKey(cNormalBrightness) &&
                floatToCharsMap.get(cNormalBrightness).contains(c)) {
            return;
        }
        if (!charBrightnessHistoryMap.containsKey(c)) {
            charBrightnessHistoryMap.put(c, getCharBrightness(c));
        }
        List<Character> cBrightnessList = floatToCharsMap.get(cNormalBrightness);
        if (cBrightnessList == null) {
            floatToCharsMap.put(cNormalBrightness, new ArrayList<>());
            cBrightnessList = floatToCharsMap.get(cNormalBrightness);
        }
        cBrightnessList.add(c);
        if (updateMin(c) || updateMax(c)) {
            updateNormalMap();
        }
    }

    /**
     * Retrieves a sorted list of all characters in the character set.
     *
     * @return a sorted list of characters.
     */
    public List<Character> GetAllCharsSorted() {
        Collection<List<Character>> charSet = floatToCharsMap.values();
        List<Character> flattenedList = new ArrayList<>();
        for (List<Character> sublist : charSet) {
            flattenedList.addAll(sublist);
        }
        List<Character> sortedList = new ArrayList<>(flattenedList);
        Collections.sort(sortedList);
        return sortedList;
    }

    /**
     * Removes a character from the character set used by the algorithm.
     *
     * @param c The character to be removed from the character map.
     */
    public void removeChar(char c) {
        float cNormalBrightness = normalBrightness(c);
        if ((!floatToCharsMap.containsKey(cNormalBrightness)) ||
                !floatToCharsMap.get(cNormalBrightness).contains(c)) {
            return;
        }
        List<Character> cBrightnessList = floatToCharsMap.get(normalBrightness(c));
        cBrightnessList.removeIf(element -> Objects.equals(element, c));
        if (cBrightnessList.isEmpty()) {
            floatToCharsMap.remove(normalBrightness(c), cBrightnessList);
        }
        if (checkMinUpdate(c) || checkMaxUpdate(c)) {
            updateNormalMap();
        }
    }

    /**
     * Retrieves the character with the minimum ASCII value from a list of
     * characters.
     *
     * @param characters the list of characters.
     * @return the character with the minimum ASCII value.
     */
    private char asciiMinValue(List<Character> characters) {
        char minAsciiChar = characters.get(FIRST_INDEX);
        for (char c : characters) {
            if (c < minAsciiChar) {
                minAsciiChar = c;
            }
        }
        return minAsciiChar;
    }

    /**
     * Checks whether the maximum character brightness needs to be updated after a
     * character removal.
     *
     * @param c The character being removed.
     * @return true if the maximum brightness needs to be updated, otherwise false.
     */
    private boolean checkMaxUpdate(char c) {
        if (floatToCharsMap.isEmpty()) {
            maxCharBrightness = Float.MIN_VALUE;
            return true;
        }
        if (charBrightnessHistoryMap.get(c) == maxCharBrightness) {
            maxCharBrightness = floatToCharsMap.lastKey();
            return true;
        }
        return false;
    }

    /**
     * Checks whether the minimum character brightness needs to be updated after a
     * character removal.
     *
     * @param c The character being removed.
     * @return true if the minimum brightness needs to be updated, otherwise false.
     */
    private boolean checkMinUpdate(char c) {
        if (floatToCharsMap.isEmpty()) {
            minCharBrightness = Float.MAX_VALUE;
            return true;
        }
        if (charBrightnessHistoryMap.get(c) == minCharBrightness) {
            maxCharBrightness = floatToCharsMap.firstKey();
            return true;
        }
        return false;
    }

    /**
     * Updates the minimum character brightness value if necessary after a character
     * addition.
     *
     * @param c The character being added.
     * @return true if the minimum brightness is updated, otherwise false.
     */
    private boolean updateMin(char c) {
        if (charBrightnessHistoryMap.get(c) < minCharBrightness) {
            minCharBrightness = charBrightnessHistoryMap.get(c);
            return true;
        }
        return false;
    }

    /**
     * Updates the maximum character brightness value if necessary after a character
     * addition.
     *
     * @param c The character being added.
     * @return true if the maximum brightness is updated, otherwise false.
     */
    private boolean updateMax(char c) {
        if (charBrightnessHistoryMap.get(c) > maxCharBrightness) {
            maxCharBrightness = charBrightnessHistoryMap.get(c);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the brightness value of a character.
     *
     * @param c The character.
     * @return the brightness value.
     */
    private float getCharBrightness(char c) {
        // TODO make this static
        if (charBrightnessHistoryMap.containsKey(c)) {
            return charBrightnessHistoryMap.get(c);
        }
        boolean[][] greyArray = CharConverter.convertToBoolArray(c);
        int count = INITIAL_COUNTER;
        for (boolean[] booleans : greyArray) {
            for (boolean b : booleans) {
                if (b) {
                    count += NEXT_COUNTER;
                }
            }
        }
        return (float) count / (greyArray.length * greyArray[FIRST_INDEX].length);
    }

    /**
     * Calculates the normalized brightness value of a character.
     *
     * @param c The character.
     * @return the normalized brightness value.
     */
    private float normalBrightness(char c) {
        return (getCharBrightness(c) - minCharBrightness) /
                (maxCharBrightness - minCharBrightness);

    }

    /**
     * Updates the mapping of normalized brightness values to characters.
     */
    private void updateNormalMap() {
        TreeMap<Float, List<Character>> tempMap = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Float, List<Character>> entry : floatToCharsMap.entrySet()) {
            List<Character> characterList = entry.getValue();
            float newKey = normalBrightness(characterList.get(FIRST_INDEX));
            tempMap.put(newKey, characterList);
        }

        floatToCharsMap.clear();

        floatToCharsMap.putAll(tempMap);

    }

    public char[] getCharSet() {
        char[] l = new char[] { 'a', 'b', 'c' };
        return l;
    }

    /**
     * Initializes the mapping of normalized brightness values to characters.
     */
    private void initNormalMap() {
        for (Map.Entry<Character, Float> pair : charBrightnessHistoryMap.entrySet()) {
            float cNormalBrightness = normalBrightness(pair.getKey());
            List<Character> cBrightnessList = floatToCharsMap.get(cNormalBrightness);
            if (cBrightnessList == null) {
                floatToCharsMap.put(cNormalBrightness, new ArrayList<>());
                cBrightnessList = floatToCharsMap.get(cNormalBrightness);
            }
            cBrightnessList.add(pair.getKey());

        }
    }
}