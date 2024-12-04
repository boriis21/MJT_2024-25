package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private static final int[][] HORIZONTAL_KERNEL = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    private static final int[][] VERTICAL_KERNEL = {
        {-1, -2, -1},
        { 0,  0,  0},
        { 1,  2,  1}
    };

    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int MAX_RGB_VALUE = 255;
    private final ImageAlgorithm grayscaleAlgorithm;

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        if (grayscaleAlgorithm == null) {
            throw new IllegalArgumentException("Grayscale algorithm cannot be null");
        }

        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);
        int width = grayscaleImage.getWidth();
        int height = grayscaleImage.getHeight();
        BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = applyKernel(grayscaleImage, x, y, HORIZONTAL_KERNEL);
                int gy = applyKernel(grayscaleImage, x, y, VERTICAL_KERNEL);
                int g = (int) Math.sqrt(gx * gx + gy * gy);
                g = Math.min(MAX_RGB_VALUE, g);
                int edgeColor = (g << RED_SHIFT) | (g << GREEN_SHIFT) | g;
                processedImage.setRGB(x, y, edgeColor);
            }
        }

        return processedImage;
    }

    private int applyKernel(BufferedImage image, int x, int y, int[][] kernel) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        int result = 0;

        for (int ky = 0; ky < kernel.length; ky++) {
            for (int kx = 0; kx < kernel.length; kx++) {
                int currentX = x + kx - 1;
                int currentY = y + ky - 1;
                int grayValue = ColorModel.getRGBdefault().getBlue(image.getRGB(currentX, currentY));
                result += grayValue * kernel[ky][kx];
            }
        }

        return result;
    }

}
