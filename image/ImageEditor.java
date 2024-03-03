package image;

import java.awt.*;

/**
 * A class for editing images.
 */
public class ImageEditor {
    /**
     * The ratio of red color in brightness calculation.
     */
    public ImageEditor() {

    }
    private static final float RED_RATIO = 0.2126f;

    /**
     * The ratio of green color in brightness calculation.
     */
    private static final float GREEN_RATIO = 0.7152f;

    /**
     * The ratio of blue color in brightness calculation.
     */
    private static final float BLUE_RATIO = 0.0722f;

    /**
     * The maximum brightness value for ASCII conversion.
     */
    private static final float MAX_ASCII = 255;

    /**
     * The color value for white.
     */
    private static final int WHITE = 255;

    /**
     * The base for calculating the closest power of 2.
     */
    private static final int BASE_TWO = 2;

    /**
     * represent the move 1 left of the list index
     */
    private static final int LAST_INDEX_MOVE = 1;

    /**
     * Index of the first character in an array or string.
     */
    private static final int FIRST_INDEX = 0;

    /**
     * init a counter value to 0
     */
    private static final int INITIAL_COUNTER = 0;

    /**
     * constant represent half value divide
     */
    private static final int HALF_VAL = 2;

    /**
     * Calculates grayscale brightness values for each sub-image of the given image with the specified
     * resolution.
     *
     * @param image      the input image.
     * @param resolution the resolution for dividing the image into sub-images.
     * @return a 2D array containing grayscale brightness values for each sub-image.
     */
    public Image[][] getRepixelatedImage(Image image, int resolution) {
        // Resize the image to the nearest power of 2
        image = resize(image);
        // Divide the resized image into sub-images
        // Get grayscale brightness values for each sub-image
        return divideImage(image, resolution);
    }

    /**
     * Finds the closest power of 2 for a given number.
     *
     * @param num the number for which to find the closest power of 2.
     * @return the closest power of 2.
     */
    private static int findClosestPowerOf2(int num) {
        double div = Math.log(num) / Math.log(BASE_TWO);
        int ceilVal = (int) Math.ceil(div);
        return (int) Math.pow(BASE_TWO, ceilVal);
    }

    /**
     * Adds rows of white pixels to the pixel array to match the new height.
     *
     * @param pixelArray the original pixel array.
     * @param height     the original height.
     * @param newHeight  the new height.
     * @param newWidth   the new width.
     */
    private static void addRows(Color[][] pixelArray, int height, int newHeight, int newWidth) {
        for (int i = 0; i < (newHeight - height) / HALF_VAL; i++) {
            for (int j = 0; j < newWidth; j++) {
                pixelArray[i][j] = new Color(WHITE, WHITE, WHITE);
                pixelArray[newHeight - i - LAST_INDEX_MOVE][j] = new Color(WHITE, WHITE, WHITE);
            }
        }
    }

    /**
     * Adds columns of white pixels to the pixel array to match the new width.
     *
     * @param pixelArray the original pixel array.
     * @param width      the original width.
     * @param newWidth   the new width.
     * @param newHeight  the new height.
     */
    private static void addCols(Color[][] pixelArray, int width, int newWidth, int newHeight) {
        for (int i = 0; i < (newWidth - width) / HALF_VAL; i++) {
            for (int j = 0; j < newHeight; j++) {
                pixelArray[j][i] = new Color(WHITE, WHITE, WHITE);
                pixelArray[j][newWidth - i - LAST_INDEX_MOVE] = new Color(WHITE, WHITE, WHITE);
            }
        }
    }

    /**
     * Fills the original image into the resized pixel array.
     *
     * @param image      the original image.
     * @param pixelArray the resized pixel array.
     * @param width      the original width.
     * @param newWidth   the new width.
     * @param height     the original height.
     * @param newHeight  the new height.
     */
    private static void fillOriginalImage(Image image, Color[][] pixelArray, int width, int newWidth,
                                          int height, int newHeight) {
        for (int i = (newHeight - height) / HALF_VAL; i < newHeight - (newHeight - height) / 2; i++) {
            for (int j = (newWidth - width) / HALF_VAL; j < newWidth - (newWidth - width) / 2; j++) {
                pixelArray[i][j] = image.getPixel(i - (newHeight - height) / 2, j -
                        (newWidth - width) / 2);
            }
        }
    }

    /**
     * Resizes the image to the nearest power of 2.
     *
     * @param image the original image.
     * @return the resized image.
     */
    private static Image resize(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int newWidth = findClosestPowerOf2(width);
        int newHeight = findClosestPowerOf2(height);
        Color[][] pixelArray = new Color[newHeight][newWidth];
        addRows(pixelArray, height, newHeight, newWidth);
        addCols(pixelArray, width, newWidth, newHeight);
        fillOriginalImage(image, pixelArray, width, newWidth, height, newHeight);
        return new Image(pixelArray, newWidth, newHeight);
    }

    /**
     * Calculates grayscale brightness values for each image in a 2D array of images.
     *
     * @param images the 2D array of images.
     * @return a 2D array containing grayscale brightness values for each image.
     */
    private static float[][] getBrightnessArray(Image[][] images) {
        int height = images.length;
        int width = images[FIRST_INDEX].length;
        float[][] brightnessArray = new float[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                brightnessArray[i][j] = getImgBrightness(images[i][j]);
            }
        }
        return brightnessArray;
    }

    /**
     * Calculates the brightness value of a single image.
     *
     * @param image the input image.
     * @return the brightness value of the image.
     */
    private static float getImgBrightness(Image image) {
        float val = INITIAL_COUNTER;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = image.getPixel(i, j);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                float greyVal = red * RED_RATIO + blue * BLUE_RATIO + green * GREEN_RATIO;
                val += greyVal;
            }
        }
        return val / (image.getHeight() * image.getWidth() * MAX_ASCII);
    }

    /**
     * Divides the given image into sub-images with the specified resolution.
     *
     * @param image      the input image.
     * @param resolution the resolution for dividing the image.
     * @return a 2D array of sub-images.
     */
    private static Image[][] divideImage(Image image, int resolution) {
        int resolutionSize = image.getWidth() / resolution;
        int numOfRows = image.getHeight() / resolutionSize;
        Image[][] imageArray = new Image[numOfRows][resolution];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < resolution; j++) {
                imageArray[i][j] = getSubImage(image, i, j, resolutionSize);
            }
        }
        return imageArray;
    }

    /**
     * Retrieves a sub-image from the original image.
     *
     * @param image          the original image.
     * @param x              the row index of the sub-image.
     * @param y              the column index of the sub-image.
     * @param resolutionSize the size of the sub-image.
     * @return the sub-image.
     */
    private static Image getSubImage(Image image, int x, int y, int resolutionSize) {
        int startRow = x * resolutionSize;
        int startCol = y * resolutionSize;
        Color[][] pixelArray = new Color[resolutionSize][resolutionSize];
        for (int i = 0; i < resolutionSize; i++) {
            for (int j = 0; j < resolutionSize; j++) {
                pixelArray[i][j] = image.getPixel(startRow + i, startCol + j);
            }
        }
        return new Image(pixelArray, resolutionSize, resolutionSize);
    }
    // Returns the greyscale value of the given color.
    private int greyScale(Color color) {
        return (int) (color.getRed() * RED_RATIO + color.getGreen() * GREEN_RATIO + color.getBlue() * BLUE_RATIO);
    }
    public float getBrightness(Image image) {
        int sum = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                sum += greyScale(image.getPixel(x, y));
            }
        }

        return (float) sum / (image.getWidth() * image.getHeight() * 255);
    }
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
                    pixelArray[y][x] = Color.WHITE;
                }
            }
        }
        return new Image(pixelArray, newWidth, newHeight);
    }
    private int getNewSize(int origSize) {
        int newSize = 1;

        while (newSize < origSize) {
            newSize <<= 1;// newSize = newSize * 2;
        }

        return newSize;
    }


}

    //
//    private static final Color DEFAULT_COLOR = Color.WHITE;
//    private static final int MAX_BRIGHTNESS = 255;
//    private static final double RED_CONST = 0.2126f;
//    private static final double GREEN_CONST = 0.7152f;
//    private static final double BLUE_CONST = 0.0722f;
//
//    // Gets a sub image from the original image.
//    private Image getSubImage(int x, int y, int size, Image originalImage) {
//        Color[][] subImage = new Color[size][size];
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                int rgb = originalImage.getPixel((x * size) + j, (y * size) + i).getRGB();
//                subImage[i][j] = new Color(rgb, true);
//            }
//        }
//        return new Image(subImage, size, size);
//    }
//
//    // Returns the next power of 2 for the given size.
//

//
//    /**
//     * Constructor for the ImageEditor class.
//     */
//
//    /**
//     * Returns the brightness of the image.
//     *
//     * @param image the image to get the brightness of
//     * @return double
//     */
//
//    /**
//     * Repixelates the image to a multiple images with new resolution.
//     *
//     * @param image      the image to repixelate
//     * @param resolution the new resolution
//     * @return Image[][] the repixelated image
//     */
//    public Image[][] getRepixelatedImage(Image image, int resolution) {
//
//        int newWidth = image.getWidth() / resolution;
//        int yResolution = image.getHeight() / newWidth;
//
//        Image[][] repixelatedImage = new Image[resolution][yResolution];
//
//        for (int y = 0; y < yResolution; y++) {
//            for (int x = 0; x < resolution; x++) {
//                repixelatedImage[x][y] = getSubImage(x, y, newWidth, image);
//            }
//        }
//
//        return repixelatedImage;
//    }
//
//    /**
//     * Scales the image to a size which is a square of 2.
//     *
//     * @param image the image to scale
//     * @return Image
//     */

//}
