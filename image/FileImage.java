package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 *
 * @author Dan Nirel
 */
public class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Color[][] pixelArray;
    private BaseImage[][] repixelatedImage;
    private int pixelSize = 0;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();

        int newWidth = getNewSize(origWidth);
        int newHeight = getNewSize(origHeight);

        pixelArray = new Color[newHeight][newWidth];

        fitImage(im, pixelArray);

        repixelatedImage = null;
    }

    private int getNewSize(int origSize) {
        int newSize = 1;

        while (newSize < origSize) {
            newSize <<= 1;//newSize = newSize * 2;
        }

        return newSize;
    }

    private void fitImage(java.awt.image.BufferedImage im, Color[][] pixelArray) {
        int imWidth = im.getWidth(), imHeight = im.getHeight();
        int arrayWidth = pixelArray.length, arrayHeight = pixelArray[0].length;

        int paddingWidth = (arrayWidth - imWidth) / 2;
        int paddingHeight = (arrayHeight - imHeight) / 2;

        for (int y = 0; y < imHeight; y++) {
            for (int x = 0; x < imWidth; x++) {
                int rgb = im.getRGB(x, y);
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
    }

    @Override
    public int getWidth() {
        return pixelArray[0].length;
    }

    @Override
    public int getHeight() {
        return pixelArray.length;
    }

    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[y][x];
    }

    @Override
    public Image[][] getRepixelatedImage(int pixelSize) {
        if (repixelatedImage != null && this.pixelSize == pixelSize) {
            return repixelatedImage;
        }
        int newWidth = getWidth() / pixelSize;
        int newHeight = getHeight() / pixelSize;

        repixelatedImage = new BaseImage[newHeight][newWidth];
        this.pixelSize = pixelSize;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                Image subImage = getSubImage(x, y, pixelSize);
                repixelatedImage[y][x] = (BaseImage) subImage;
            }
        }

        return repixelatedImage;
    }

    public Image getSubImage(int x, int y, int size) {
        Color[][] subImage = new Color[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(pixelArray[(y * pixelSize) + i], (x * pixelSize), subImage[i], 0, size);
        }
        return new BaseImage(subImage);
    }
}
