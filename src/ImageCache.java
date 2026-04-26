import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageCache {

    private static final ImageCache INSTANCE = new ImageCache();
    private final Map<String, BufferedImage> cache = new HashMap<>();

    private ImageCache() {}

    public static ImageCache get() { return INSTANCE; }

    public ImageIcon icon(String path, int w, int h) {
        BufferedImage img = raw(path);
        if (img == null) return blank(w, h);
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    public BufferedImage raw(String path) {
        return cache.computeIfAbsent(path, this::load);
    }

    private BufferedImage load(String path) {
        try {
            URL url = ImageCache.class.getResource("/" + path);
            if (url != null) return ImageIO.read(url);

            File file = new File("src", path);
            if (file.isFile()) return ImageIO.read(file);

            return null;
        } catch (IOException e) {
            System.err.println("ImageCache: failed to load " + path);
            return null;
        }
    }

    private ImageIcon blank(int w, int h) {
        return new ImageIcon(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
    }
}
