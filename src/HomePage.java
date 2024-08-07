import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends JPanel {

    private JFrame frame;
    private Map<String, CartItem> cartItems;
    private Font categoryFont;

    // Define color constants
    private static final Color PRIMARY_BACKGROUND_COLOR = Color.decode("#FFFDED");
    private static final Color TITLE_BACKGROUND_COLOR = Color.decode("#201335");
    private static final Color TITLE_TEXT_COLOR = Color.decode("#FCE762");
    private static final Color CATEGORY_TEXT_COLOR = Color.decode("#4F4789");
    private static final Color GRID_LINE_COLOR = Color.decode("#1F2833");

    public HomePage(JFrame frame) {
        this.frame = frame;
        this.cartItems = new HashMap<>(); // Initialize the cartItems map
        setLayout(new BorderLayout());

        // Set the primary background color
        setBackground(PRIMARY_BACKGROUND_COLOR);
        
        // Load the custom font
        Font titleFont = loadCustomFont("fonts/Original Fish.otf", 34f); // Adjust size as needed
        
        categoryFont = loadCustomFont("fonts/Brigends Expanded.otf", 14f);

        // Panel for the title label, centered
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Add horizontal and vertical gaps
        titlePanel.setBackground(TITLE_BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Amazon", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(TITLE_TEXT_COLOR); // Text color
        titlePanel.add(titleLabel);

        // Panel to combine titlePanel (no buttonPanel now)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for the top panel
        topPanel.add(titlePanel, BorderLayout.CENTER);

        // Add topPanel to the top of the main panel
        add(topPanel, BorderLayout.NORTH);

        // Categories panel using GridBagLayout
        JPanel categoriesPanel = new JPanel(new GridBagLayout());
        categoriesPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for the categories panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Create buttons and labels for each category
        addCategoryItem(categoriesPanel, gbc, "Electronics", "images/gadgets.png", 0, 0);
        addCategoryItem(categoriesPanel, gbc, "Stationery", "images/stationery.png", 1, 0);
        addCategoryItem(categoriesPanel, gbc, "Accessories", "images/accessories.png", 0, 1);
        addCategoryItem(categoriesPanel, gbc, "Sports", "images/sports.png", 1, 1);

        add(categoriesPanel, BorderLayout.CENTER);
    }

    private void addCategoryItem(JPanel panel, GridBagConstraints gbc, String category, String imagePath, int gridX, int gridY) {
        gbc.gridx = gridX * 2;  // Button at even columns
        gbc.gridy = gridY * 2;

        // Create a panel for the category item with a border
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for the item panel
        itemPanel.setBorder(new LineBorder(GRID_LINE_COLOR, 0)); // Set the border for grid lines

        JButton button = createCategoryButton(category, imagePath);
        itemPanel.add(button, BorderLayout.CENTER);

        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(itemPanel, gbc);

        gbc.gridx = gridX * 2;  // Label at even columns
        gbc.gridy = gridY * 2 + 1;
    }

    private JButton createCategoryButton(String category, String imagePath) {
        ImageIcon icon = createResizedImageIcon(imagePath, 150, 150); // Resize the image to fit the button
        JButton button = new JButton(); // Create an empty button

        // Create a panel to hold the category image and text
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false); // Make the panel transparent

        // Create a panel for the image
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false); // Make the panel transparent
        JLabel iconLabel = new JLabel(icon);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        // Create a panel for the text
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false); // Make the panel transparent
        JLabel textLabel = new JLabel(category, SwingConstants.CENTER);
        textLabel.setForeground(CATEGORY_TEXT_COLOR); // Text color
        textLabel.setFont(categoryFont);
        textPanel.add(textLabel, BorderLayout.CENTER);

        // Combine the image and text panels
        contentPanel.add(iconPanel, BorderLayout.CENTER);
        contentPanel.add(textPanel, BorderLayout.SOUTH);

        button.add(contentPanel);

        // Customize button appearance
        button.setPreferredSize(new Dimension(150, 150)); // Set the preferred size
        button.setContentAreaFilled(false); // Remove default background
        button.setBorderPainted(false); // Remove border
        button.setFocusPainted(false); // Remove focus border
        button.setOpaque(false); // Make button opaque (remove button color)

        // Add mouse listener to handle mouse hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorderPainted(true); // Show border when mouse enters
                button.setBorder(BorderFactory.createLineBorder(GRID_LINE_COLOR, 5)); // Set border color and thickness
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorderPainted(false); // Hide border when mouse exits
            }
        });

        // Add action listener to handle button clicks
        button.addActionListener(e -> {
            switch (category) {
                case "Electronics":
                    frame.setContentPane(new ElectronicsPage(frame, category, cartItems));
                    break;
                case "Stationery":
                    frame.setContentPane(new StationeryPage(frame, category, cartItems));
                    break;
                case "Accessories":
                    frame.setContentPane(new AccessoriesPage(frame, category, cartItems));
                    break;
                case "Sports":
                    frame.setContentPane(new SportsPage(frame, category, cartItems));
                    break;
                default:
                    JOptionPane.showMessageDialog(frame, "Category not recognized.");
                    return;
            }
            frame.revalidate();
            frame.repaint();
        });

        return button;
    }
    
    private Font loadCustomFont(String fontPath, float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + fontPath);
            if (is == null) {
                System.err.println("Font file not found: " + fontPath);
                return null;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size); // Derive the font with the specified size
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ImageIcon createResizedImageIcon(String path, int width, int height) {
        BufferedImage bufferedImage = loadImage(path);
        if (bufferedImage != null) {
            Image resizedImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } else {
            return null;
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            URL imgURL = getClass().getResource("/" + path); // Ensure leading slash
            if (imgURL != null) {
                return ImageIO.read(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }
}
