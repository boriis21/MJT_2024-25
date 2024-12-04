package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class LuminosityGrayscale implements GrayscaleAlgorithm {

    public static final double RED_LUMINOSITY_COEFFICIENT = 0.21;
    public static final double GREEN_LUMINOSITY_COEFFICIENT = 0.72;
    public static final double BLUE_LUMINOSITY_COEFFICIENT = 0.07;

    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        BufferedImage grayscaleImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int pixel = image.getRGB(x, y);

                int red = ColorModel.getRGBdefault().getRed(pixel);
                int green = ColorModel.getRGBdefault().getGreen(pixel);
                int blue = ColorModel.getRGBdefault().getBlue(pixel);

                int luminosityValue = (int) getLuminosityValue(red, green, blue);
                int grayRGB = luminosityValue << RED_SHIFT | luminosityValue << GREEN_SHIFT | luminosityValue;
                grayscaleImage.setRGB(x, y, grayRGB);
            }
        }

        return grayscaleImage;
    }

    private double getLuminosityValue(int red, int green, int blue) {
        return RED_LUMINOSITY_COEFFICIENT * red +
            GREEN_LUMINOSITY_COEFFICIENT * green +
            BLUE_LUMINOSITY_COEFFICIENT * blue;
    }

}
