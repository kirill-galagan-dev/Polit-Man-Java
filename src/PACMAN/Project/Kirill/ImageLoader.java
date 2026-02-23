package PACMAN.Project.Kirill;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    public static Image loadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                InputStream inputStream = ImageLoader.class.getClassLoader().getResourceAsStream(path);
                if (inputStream != null) {
                    return ImageIO.read(inputStream);
                } else {
                    throw new IOException("Resource not found: " + path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}