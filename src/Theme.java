import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.InputStream;

public class Theme {

    public static final Color SURFACE   = Color.decode("#FFFFFF");
    public static final Color BG        = Color.decode("#F1F5F9");
    public static final Color HEADER    = Color.decode("#1E293B");
    public static final Color HEADER_2  = Color.decode("#334155");

    public static final Color TEXT      = Color.decode("#0F172A");
    public static final Color TEXT_2    = Color.decode("#64748B");
    public static final Color TEXT_ON_DARK = Color.decode("#F8FAFC");

    public static final Color PRIMARY   = Color.decode("#2563EB");
    public static final Color CTA       = Color.decode("#F97316");
    public static final Color BORDER    = Color.decode("#E2E8F0");
    public static final Color BORDER_2  = Color.decode("#CBD5E1");
    public static final Color HOVER_BG  = Color.decode("#F8FAFC");

    public static final Color SUCCESS   = Color.decode("#16A34A");
    public static final Color DANGER    = Color.decode("#DC2626");
    public static final Color WARNING   = Color.decode("#D97706");
    public static final Color STAR      = Color.decode("#F59E0B");
    public static final Color BADGE     = Color.decode("#EF4444");
    public static final Color HEART_ON  = Color.decode("#EF4444");
    public static final Color HEART_OFF = Color.decode("#CBD5E1");

    public static Color categoryAccent(Category cat) {
        return switch (cat) {
            case ELECTRONICS -> Color.decode("#2563EB");
            case STATIONERY  -> Color.decode("#7C3AED");
            case ACCESSORIES -> Color.decode("#DB2777");
            case SPORTS      -> Color.decode("#16A34A");
        };
    }

    public static Color categoryLight(Category cat) {
        return switch (cat) {
            case ELECTRONICS -> Color.decode("#EFF6FF");
            case STATIONERY  -> Color.decode("#F5F3FF");
            case ACCESSORIES -> Color.decode("#FDF2F8");
            case SPORTS      -> Color.decode("#F0FDF4");
        };
    }

    private static Font brandFont;
    private static Font headingFont;

    public static Font brand(float size) {
        if (brandFont == null) brandFont = loadFont("fonts/Original Fish.otf");
        return brandFont != null ? brandFont.deriveFont(size) : bold(size);
    }

    public static Font heading(float size) {
        if (headingFont == null) headingFont = loadFont("fonts/Brigends Expanded.otf");
        return headingFont != null ? headingFont.deriveFont(size) : bold(size);
    }

    public static Font body(float size)  { return new Font("SansSerif", Font.PLAIN, (int) size); }
    public static Font bold(float size)  { return new Font("SansSerif", Font.BOLD,  (int) size); }

    private static Font loadFont(String path) {
        try {
            InputStream is = Theme.class.getResourceAsStream("/" + path);
            if (is != null) return Font.createFont(Font.TRUETYPE_FONT, is);

            File file = new File("src", path);
            if (file.isFile()) return Font.createFont(Font.TRUETYPE_FONT, file);

            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
