package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class LuminosityGrayscaleTest {

    private final GrayscaleAlgorithm grayscale = new LuminosityGrayscale();

    @Test
    void testProcessThrowsForNullImage() {
        assertThrows(IllegalArgumentException.class, () -> grayscale.process(null),
            "Process of Luminosity expected to throw Illegal Argument exception when null image passed");
    }

    @Test
    void testProcessCorrectlyTransformsToGrayscaleSinglePixelImage() {
        BufferedImage originalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        originalImage.setRGB(0, 0, new Color(255, 0, 0).getRGB());

        BufferedImage grayscaleImage = grayscale.process(originalImage);

        int expectedGray = (int) (0.21 * 255 + 0.72 * 0 + 0.07 * 0);
        assertEquals(expectedGray, new Color(grayscaleImage.getRGB(0, 0)).getRed(),
            "Process of Luminosity expected to evaluate correctly the grayscale value");
    }

    @Test
    void testProcessCorrectlyTransformsToGrayscaleMultiplePixelsImage() {
        BufferedImage originalImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        originalImage.setRGB(0, 0, new Color(255, 0, 0).getRGB());
        originalImage.setRGB(1, 0, new Color(0, 255, 0).getRGB());
        originalImage.setRGB(2, 0, new Color(0, 0, 255).getRGB());

        BufferedImage grayscaleImage = grayscale.process(originalImage);

        int expectedGrayRed = (int) (0.21 * 255 + 0.72 * 0 + 0.07 * 0);
        int expectedGrayGreen = (int) (0.21 * 0 + 0.72 * 255 + 0.07 * 0);
        int expectedGrayBlue = (int) (0.21 * 0 + 0.72 * 0 + 0.07 * 255);

        assertEquals(expectedGrayRed, new Color(grayscaleImage.getRGB(0, 0)).getRed(),
            "Process of Luminosity expected to evaluate correctly the grayscale value");
        assertEquals(expectedGrayGreen, new Color(grayscaleImage.getRGB(1, 0)).getRed(),
            "Process of Luminosity expected to evaluate correctly the grayscale value");
        assertEquals(expectedGrayBlue, new Color(grayscaleImage.getRGB(2, 0)).getRed(),
            "Process of Luminosity expected to evaluate correctly the grayscale value");
    }

    @Test
    void testProcessCorrectlyTransformsToGrayscaleDifferentImageSizes() {
        BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                image.setRGB(x, y, new Color(100, 150, 200).getRGB());
            }
        }

        BufferedImage grayscaleImage = grayscale.process(image);

        int expectedGray = (int) (0.21 * 100 + 0.72 * 150 + 0.07 * 200);
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals(expectedGray, new Color(grayscaleImage.getRGB(x, y)).getRed(),
                    "Process of Luminosity expected to evaluate correctly the grayscale value");
            }
        }
    }
}
