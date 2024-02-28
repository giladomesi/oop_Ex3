package ascii_art;

import image.Image;
import image.ImageEditor;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;

/**
 * Class for the ASCII art algorithm.
 */
public class AsciiArtAlgorithm {

    private final Image image;
    private final int resolution;

    private final SubImgCharMatcher subImgCharMatcher;

    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher subImgCharMatcher) throws IOException {
        this.image = image;
        this.resolution = resolution;
        this.subImgCharMatcher = subImgCharMatcher;
    }

    /**
     * 
     * Runs the algorithm.
     * 
     * @return char[][]
     */
    public char[][] run() {

        ImageEditor imageEditor = new ImageEditor();
        Image paddedImage = imageEditor.paddImage(image);
        Image[][] repixelatedImage = imageEditor.getRepixelatedImage(paddedImage, resolution);
        char[][] asciiArt = new char[repixelatedImage.length][repixelatedImage[0].length];
        for (int x = 0; x < repixelatedImage.length; x++) {
            for (int y = 0; y < repixelatedImage[0].length; y++) {
                double brightness = imageEditor.getBrightness(repixelatedImage[x][y]);
                asciiArt[x][y] = subImgCharMatcher.getCharByImageBrightness(brightness);
            }
        }
        return asciiArt;
    }
}
