package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

/**
 * Class for the ASCII art algorithm.
 */
public class AsciiArtAlgorithm {

    private final float[][] brightness;
    private final SubImgCharMatcher subImgCharMatcher;

    /**
     * Constructor for the algorithm, assumes that the image won't be
     * updated without creating a new algorithm.
     *
     * @param image             the image to convert to ASCII art
     * @param resolution        the resolution of the ASCII art
     * @param subImgCharMatcher the character matcher
     */
    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher subImgCharMatcher) {
        this.subImgCharMatcher = subImgCharMatcher;
        ImageEditor imageEditor = new ImageEditor();
        Image[][] repixelatedImage = imageEditor.getRepixelatedImage(image, resolution);
        this.brightness = new float[repixelatedImage.length][repixelatedImage[0].length];
        for (int x = 0; x < repixelatedImage.length; x++) {
            for (int y = 0; y < repixelatedImage[0].length; y++) {
                brightness[x][y] = imageEditor.getBrightness(repixelatedImage[x][y]);
            }
        }
    }

    /**
     * Runs the algorithm.
     *
     * @return char[][] the ASCII art image as a 2D array of characters
     */
    public char[][] run() {
        char[][] asciiArt = new char[brightness.length][brightness[0].length];
        for (int x = 0; x < brightness.length; x++) {
            for (int y = 0; y < brightness[0].length; y++) {
                asciiArt[x][y] = subImgCharMatcher.getCharByImageBrightness(brightness[x][y]);
            }
        }
        return asciiArt;
    }
}
