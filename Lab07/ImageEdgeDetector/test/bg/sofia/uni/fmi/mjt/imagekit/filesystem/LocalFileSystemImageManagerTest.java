package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalFileSystemImageManagerTest {

    private final LocalFileSystemImageManager fileSystem = new LocalFileSystemImageManager();

    @Test
    void testLoadImageThrowsForNullImage() {
        assertThrows(IllegalArgumentException.class, () -> fileSystem.loadImage(null),
            "Load image expected to throw Illegal Argument Exception when null image is passed");
    }

    @Test
    void testLoadImageThrowsForUnsupportedFormat() {
        assertThrows(IOException.class, () -> fileSystem.loadImage(new File("image.ppm")),
            "Load image expected to throw IOException when image with unsupported format is passed");
    }

    @Test
    void testLoadImageThrowsForNonExistingFile() {
        File nonExistingFile = mock();
        when(nonExistingFile.exists()).thenReturn(false);
        assertThrows(IOException.class, () -> fileSystem.loadImage(nonExistingFile),
            "Load image expected to throw IOException when non existing image is passed");
    }

    @Test
    void testLoadImageThrowsForNotFile() {
        File notFile = mock();
        when(notFile.exists()).thenReturn(true);
        when(notFile.isFile()).thenReturn(false);

        assertThrows(IOException.class, () -> fileSystem.loadImage(notFile),
            "Load image expected to throw IOException when not a file is passed");
    }

    @Test
    void testLoadImageCorrectBehaviour() throws IOException {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int pixelChange = 10 * x + 10 * y;
                image.setRGB(x, y, new Color(50 + pixelChange, 100 + pixelChange, 150 + pixelChange).getRGB());
            }
        }

        byte[] imageBytes;
        try (var byteOutput = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", byteOutput);
            imageBytes = byteOutput.toByteArray();
        }

        File tempFile = Files.createTempFile("test-image", ".png").toFile();
        tempFile.deleteOnExit();

        try (var fileOutput = new FileOutputStream(tempFile)) {
            fileOutput.write(imageBytes);
        }

        BufferedImage loadedImage = fileSystem.loadImage(tempFile);

        assertEquals(image.getWidth(), loadedImage.getWidth(),
            "Load image expected to get correct image width");
        assertEquals(image.getHeight(), loadedImage.getHeight(),
            "Load image expected to get correct image width");
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                assertEquals(image.getRGB(x, y), loadedImage.getRGB(x, y),
                    "Load image expected to get correct image pixels");
            }
        }
    }

    @Test
    void testLoadImagesFromDirectoryThrowsForNullDirectory() {
        assertThrows(IllegalArgumentException.class, () -> fileSystem.loadImagesFromDirectory(null),
            "Load images from directory expected to throw Illegal Argument exception when null directory is passed");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsForNonExistingDirectory() {
        File nonExistingDirectory = mock();
        when(nonExistingDirectory.exists()).thenReturn(false);
        assertThrows(IOException.class, () -> fileSystem.loadImagesFromDirectory(nonExistingDirectory),
            "Load images from directory expected to throw IOException when non existing image is passed");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsForNotDirectory() {
        File notDirectory = mock();
        when(notDirectory.exists()).thenReturn(true);
        when(notDirectory.isDirectory()).thenReturn(false);

        assertThrows(IOException.class, () -> fileSystem.loadImagesFromDirectory(notDirectory),
            "Load images from directory expected to throw IOException when not a directory is passed");
    }

    @Test
    void testLoadImageFromDirectoryCorrectBehaviour() throws IOException {
        File tempDirectory = Files.createTempDirectory("images-directory").toFile();
        tempDirectory.deleteOnExit();

        BufferedImage image1 = new BufferedImage(10, 14, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(14, 8, BufferedImage.TYPE_INT_RGB);
        BufferedImage image3 = new BufferedImage(9, 4, BufferedImage.TYPE_INT_RGB);

        File imageFile1 = new File(tempDirectory, "image1.png");
        File imageFile2 = new File(tempDirectory, "image2.jpg");
        File imageFile3 = new File(tempDirectory, "image3.bmp");

        ImageIO.write(image1, "png", imageFile1);
        ImageIO.write(image2, "jpg", imageFile2);
        ImageIO.write(image3, "bmp", imageFile3);

        List<BufferedImage> loadedImages = fileSystem.loadImagesFromDirectory(tempDirectory);

        assertEquals(3, loadedImages.size(),
            "Load images from directory expected to load all images inside the directory");
        assertEquals(image3.getWidth(), loadedImages.get(0).getWidth(),
            "Load images from directory expected to get the width of a image correctly");
        assertEquals(image3.getHeight(), loadedImages.get(0).getHeight(),
            "Load images from directory expected to get the height of a image correctly");
        assertEquals(image2.getWidth(), loadedImages.get(1).getWidth(),
            "Load images from directory expected to get the width of a image correctly");
        assertEquals(image2.getHeight(), loadedImages.get(1).getHeight(),
            "Load images from directory expected to get the height of a image correctly");
        assertEquals(image1.getWidth(), loadedImages.get(2).getWidth(),
            "Load images from directory expected to get the width of a image correctly");
        assertEquals(image1.getHeight(), loadedImages.get(2).getHeight(),
            "Load images from directory expected to get the height of a image correctly");
    }

    @Test
    void testSaveImageThrowsForNullImage() {
        File file = mock();
        assertThrows(IllegalArgumentException.class, () -> fileSystem.saveImage(null, file),
            "Save image expected to throw Illegal Argument exception when null image passed");
    }

    @Test
    void testSaveImageThrowsForNullImageFile() {
        BufferedImage image = mock();
        assertThrows(IllegalArgumentException.class, () -> fileSystem.saveImage(image, null),
            "Save image expected to throw Illegal Argument exception when null image file passed");
    }

    @Test
    void testSaveImageThrowsForNullParentDirectory() {
        BufferedImage image = mock();
        File imageFile = mock();
        when(imageFile.getParentFile()).thenReturn(null);

        assertThrows(IOException.class, () -> fileSystem.saveImage(image, imageFile),
            "Save image expected to throw IOException when the parent directory of the fileImage is null");
    }

    @Test
    void testSaveImageThrowsForNonExistingParentDirectory() {
        BufferedImage image = mock();
        File imageFile = mock();
        File parentDirectory = mock();
        when(imageFile.getParentFile()).thenReturn(parentDirectory);
        when(parentDirectory.exists()).thenReturn(false);

        assertThrows(IOException.class, () -> fileSystem.saveImage(image, imageFile),
            "Save image expected to throw IOException when the parent directory of the fileImage is non existing");
    }

    @Test
    void testSaveImageThrowsForAlreadyExistingImage() {
        BufferedImage image = mock();
        File imageFile = mock();
        File parentDirectory = mock();
        when(imageFile.getParentFile()).thenReturn(parentDirectory);
        when(parentDirectory.exists()).thenReturn(true);
        when(imageFile.exists()).thenReturn(true);

        assertThrows(IOException.class, () -> fileSystem.saveImage(image, imageFile),
            "Save image expected to throw IOException when trying to save in already existing file");
    }

    @Test
    void testSaveImageThrowsForInvalidFormat() {
        BufferedImage image = mock();
        File imageFile = mock();
        File parentDirectory = mock();
        when(imageFile.getParentFile()).thenReturn(parentDirectory);
        when(parentDirectory.exists()).thenReturn(true);
        when(imageFile.exists()).thenReturn(false);
        when(imageFile.getName()).thenReturn("invalid.Format");

        assertThrows(IOException.class, () -> fileSystem.saveImage(image, imageFile),
            "Save image expected to throw IOException when trying to save image with invalid format");
    }

    @Test
    void testSaveImageThrowsForBlankFormat() {
        BufferedImage image = mock();
        File imageFile = mock();
        File parentDirectory = mock();
        when(imageFile.getParentFile()).thenReturn(parentDirectory);
        when(parentDirectory.exists()).thenReturn(true);
        when(imageFile.exists()).thenReturn(false);
        when(imageFile.getName()).thenReturn("invalidFormat.");

        assertThrows(IOException.class, () -> fileSystem.saveImage(image, imageFile),
            "Save image expected to throw IOException when trying to save image with blank format");
    }

    @Test
    void testSaveImageCorrectBehaviour() throws IOException {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int pixelChange = 10 * x + 10 * y;
                image.setRGB(x, y, new Color(50 + pixelChange, 100 + pixelChange, 150 + pixelChange).getRGB());
            }
        }

        File parentDirectory = Files.createTempDirectory("parent").toFile();
        parentDirectory.deleteOnExit();

        File imageFile = new File(parentDirectory, "test-image.png");

        fileSystem.saveImage(image, imageFile);

        BufferedImage savedImage = ImageIO.read(imageFile);
        assertEquals(image.getWidth(), savedImage.getWidth(),
            "Saved image expected to match the original image width");
        assertEquals(image.getHeight(), savedImage.getHeight(),
            "Saved image expected to match the original image height");

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                assertEquals(image.getRGB(x, y), savedImage.getRGB(x, y),
                    "Saved image expected to match the original image pixels");
            }
        }
    }

}
