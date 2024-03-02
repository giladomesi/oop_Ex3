package image;

import java.awt.*;

/**
 * A class for editing images.
 */
public class ImageEditor {

    private static final Color DEFAULT_COLOR = Color.WHITE;

    // Gets a sub image from the original image.
    private Image getSubImage(int x, int y, int size, Image originalImage) {
        Color[][] subImage = new Color[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int rgb = originalImage.getPixel((x * size) + j, (y * size) + i).getRGB();
                subImage[i][j] = new Color(rgb, true);
            }
        }
        return new Image(subImage, size, size);
    }

    // Returns the next power of 2 for the given size.
    private int getNewSize(int origSize) {
        int newSize = 1;

        while (newSize < origSize) {
            newSize <<= 1;// newSize = newSize * 2;
        }

        return newSize;
    }

    // Returns the greyscale value of the given color.
    private int greyScale(Color color) {
        return (int) (color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722);
    }

    /**
     * Constructor for the ImageEditor class.
     */
    public ImageEditor() {

    }

    /**
     * Returns the brightness of the image.
     *
     * @param image the image to get the brightness of
     * @return double
     */
    public float getBrightness(Image image) {
        int sum = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                sum += greyScale(image.getPixel(x, y));
            }
        }
        int maxBrightness = 255;
        return (float) sum / (image.getWidth() * image.getHeight() * maxBrightness);
    }

    /**
     * Repixelates the image to a multiple images with new resolution.
     *
     * @param image      the image to repixelate
     * @param resolution the new resolution
     * @return Image[][] the repixelated image
     */
    public Image[][] getRepixelatedImage(Image image, int resolution) {

        int newWidth = image.getWidth() / resolution;
        int yResolution = image.getHeight() / newWidth;

        Image[][] repixelatedImage = new Image[resolution][yResolution];

        for (int y = 0; y < yResolution; y++) {
            for (int x = 0; x < resolution; x++) {
                repixelatedImage[x][y] = getSubImage(x, y, newWidth, image);
            }
        }

        return repixelatedImage;
    }

    /**
     * Scales the image to a size which is a square of 2.
     *
     * @param image the image to scale
     * @return Image
     */
    public Image paddImage(Image image) {
        int origWidth = image.getWidth(), origHeight = image.getHeight();
        int newWidth = getNewSize(origWidth);
        int newHeight = getNewSize(origHeight);
        Color[][] pixelArray = new Color[newHeight][newWidth];

        int imWidth = image.getWidth(), imHeight = image.getHeight();
        int arrayWidth = pixelArray.length, arrayHeight = pixelArray[0].length;

        int paddingWidth = (arrayWidth - imWidth) / 2;
        int paddingHeight = (arrayHeight - imHeight) / 2;

        for (int y = 0; y < imHeight; y++) {
            for (int x = 0; x < imWidth; x++) {
                int rgb = image.getPixel(y, x).getRGB();
                pixelArray[y + paddingHeight][x + paddingWidth] = new Color(rgb, true);
            }
        }

        // Fill the remaining space with white pixels
        for (int y = 0; y < arrayHeight; y++) {
            for (int x = 0; x < arrayWidth; x++) {
                if (y < paddingHeight || y >= paddingHeight + imHeight ||
                        x < paddingWidth || x >= paddingWidth + imWidth) {
                    pixelArray[y][x] = DEFAULT_COLOR;
                }
            }
        }
        return new Image(pixelArray, newWidth, newHeight);
    }

}
