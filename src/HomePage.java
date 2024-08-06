import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends JPanel {

    private JFrame frame;
    private Map<String, CartItem> cartItems;
    
    // Color for subtle grid lines
    private static final Color GRID_LINE_COLOR = Color.decode("#003C4F");

    public HomePage(JFrame frame) {
        this.frame = frame;
        this.cartItems = new HashMap<>(); // Initialize the cartItems map
        setLayout(new BorderLayout());
        
        // Set the primary background color
        setBackground(Color.decode("#021526"));

        // Panel for the title label, centered
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Add horizontal and vertical gaps
        titlePanel.setBackground(Color.decode("#009FBD"));
        JLabel titleLabel = new JLabel("Untitled", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#021526")); // Text color
        //titleLabel.setBorder(new EmptyBorder(2, 120, 0, 0));
        titlePanel.add(titleLabel);

        // Panel to combine titlePanel (no buttonPanel now)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#021526")); // Background color for the top panel
        topPanel.add(titlePanel, BorderLayout.CENTER);

        // Add topPanel to the top of the main panel
        add(topPanel, BorderLayout.NORTH);

        // Categories panel using GridBagLayout
        JPanel categoriesPanel = new JPanel(new GridBagLayout());
        categoriesPanel.setBackground(Color.decode("#021526")); // Background color for the categories panel
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
        itemPanel.setBackground(Color.decode("#021526")); // Background color for the item panel
        itemPanel.setBorder(new LineBorder(GRID_LINE_COLOR, 1)); // Set the border for grid lines
        
        JButton button = createCategoryButton(category, imagePath);
        itemPanel.add(button, BorderLayout.CENTER);

        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(itemPanel, gbc);

        gbc.gridx = gridX * 2;  // Label at even columns
        gbc.gridy = gridY * 2 + 1;
        JLabel label = new JLabel(category, SwingConstants.CENTER);
        label.setForeground(Color.decode("#F9E2AF")); // Text color

        panel.add(label, gbc);
    }

    private JButton createCategoryButton(String category, String imagePath) {
        ImageIcon icon = createResizedImageIcon(imagePath, 150, 150); // Resize the image to fit the button
        JButton button = new JButton(); // Create an empty button

        // Set up a panel to hold the category image
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false); // Make the panel transparent
        JLabel iconLabel = new JLabel(icon);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        button.add(iconPanel);

        // Customize button appearance
        button.setPreferredSize(new Dimension(150, 150)); // Set the preferred size
        button.setContentAreaFilled(false); // Remove default background
        button.setBorderPainted(false); // Remove border
        button.setFocusPainted(false); // Remove focus border
        button.setOpaque(false); // Make button opaque (remove button color)

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
