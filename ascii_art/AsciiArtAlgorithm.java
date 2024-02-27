package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

/**
 * Class for the ASCII art algorithm.
 */
public class AsciiArtAlgorithm {

    private Image image;
    private char[] charSet;
    private int resolution;

    public AsciiArtAlgorithm(Image image, char[] charSet, int resolution) {
        this.image = image;
        this.charSet = charSet;
        this.resolution = resolution;
    }

    /**
     * 
     * Runs the algorithm.
     * 
     * @return char[][]
     */
    public char[][] run() {

        ImageEditor imageEditor = new ImageEditor();
        SubImgCharMatcher subImgCharMatcher = new SubImgCharMatcher(charSet);
        char[][] asciiArt = new char[image.getHeight() / resolution][image.getWidth() / resolution];
        Image paddedImage = imageEditor.paddImage(image);
        Image[][] repixelatedImage = imageEditor.getRepixelatedImage(paddedImage, resolution);

        for (int x = 0; x < repixelatedImage.length; x++) {
            for (int y = 0; y < repixelatedImage[0].length; y++) {
                double brightness = imageEditor.getBrightness(repixelatedImage[x][y]);
                asciiArt[x][y] = subImgCharMatcher.getCharByImageBrightness(brightness);
            }
        }
        return asciiArt;
    }
}
