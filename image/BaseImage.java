package image;

import java.awt.*;

public class BaseImage implements Image {
    private final Color[][] pixelArray;

    public BaseImage(Color[][] pixelArray) {
        this.pixelArray = pixelArray;
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
    public Image[][] getRepixelatedImage(int pixelSize) {
        return new Image[0][];
    }

    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[y][x];
    }
}
