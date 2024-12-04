package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SobelEdgeDetectionTest {

    private final GrayscaleAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
    private final EdgeDetectionAlgorithm edgeAlgorithm = new SobelEdgeDetection(grayscaleAlgorithm);

    @Test
    void testProcessThrowsForNullImage() {
        assertThrows(IllegalArgumentException.class, () -> edgeAlgorithm.process(null),
            "Expected to throw Illegal Argument exception when null image passed");
    }

    @Test
    void testProcessCorrectlyDetectsEdgesInLargerComplexImage() {
        int width = 5;
        int height = 5;

        BufferedImage originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelChange = 10 * x + 10 * y;
                int color = new Color(100 + pixelChange, 80 + pixelChange, 60 + pixelChange).getRGB();
                originalImage.setRGB(x, y, color);
            }
        }

        BufferedImage edgeProcessedImage = edgeAlgorithm.process(originalImage);

        int[][] expectedResults = {
            {0,   0,   0,   0, 0},
            {0, 113, 113, 113, 0},
            {0, 113, 113, 113, 0},
            {0, 113, 113, 113, 0},
            {0,   0,   0,   0, 0}
        };

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int processedPixel = new Color(edgeProcessedImage.getRGB(x, y)).getRed();
                assertEquals(expectedResults[y][x], processedPixel,
                    "Process method of Sobel Edge Detection expected to match the expected result");
            }
        }
    }

}
