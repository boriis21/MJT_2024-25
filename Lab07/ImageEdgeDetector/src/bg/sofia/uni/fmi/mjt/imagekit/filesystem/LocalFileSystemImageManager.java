package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    private static final String JPG_FILE_EXTENSION = "JPG";
    private static final String JPEG_FILE_EXTENSION = "JPEG";
    private static final String PNG_FILE_EXTENSION = "PNG";
    private static final String BMP_FILE_EXTENSION = "BMP";

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("Image file does not exist or is not a regular file");
        }

        if (!isSupportedFormat(getImageFormat(imageFile))) {
            throw new IOException("Image file is in unsupported format");
        }

        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("Failed to read the image file");
        }

        return image;
    }

    private String getImageFormat(File imageFile) {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        String fileName = imageFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(dotIndex + 1).toUpperCase();
    }

    private boolean isSupportedFormat(String format) {
        if (format == null || format.isBlank()) {
            throw new IllegalArgumentException("Format cannot be null or empty");
        }

        return format.equals(JPG_FILE_EXTENSION) ||
            format.equals(JPEG_FILE_EXTENSION) ||
            format.equals(PNG_FILE_EXTENSION) ||
            format.equals(BMP_FILE_EXTENSION);
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Images directory cannot be null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("Images directory does not exist or is not a directory");
        }

        List<BufferedImage> images = new ArrayList<>();
        File[] directoryFiles = imagesDirectory.listFiles();

        if (directoryFiles == null) {
            throw new IllegalArgumentException("Directory files cannot be null");
        }

        for (File imageFile : directoryFiles) {
            images.add(loadImage(imageFile));
        }

        return images;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        if (imageFile == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        File parentDirectory = imageFile.getParentFile();
        if (parentDirectory == null || !parentDirectory.exists()) {
            throw new IOException("Parent directory does not exist");
        }

        if (imageFile.exists()) {
            throw new IOException(imageFile.getName() + " already exists");
        }

        String imageFormat = getImageFormat(imageFile);
        if (imageFormat == null || imageFormat.isBlank()) {
            throw new IOException("Unsupported format");
        }

        if (!ImageIO.write(image, imageFormat.toLowerCase(), imageFile)) {
            throw new IOException("Failed to save image");
        }
    }
}
