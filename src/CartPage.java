import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class CartPage extends JPanel {

    private JFrame frame;
    private Map<String, CartItem> cartItems;
    private JPanel previousPage;

    // Color for subtle grid lines
    private static final Color GRID_LINE_COLOR = Color.decode("#003C4F");

    // Map to hold image paths for items
    private static final Map<String, String> itemImagePaths = new HashMap<>();

    static {
        itemImagePaths.put("Phone", "images/phone.png");
        itemImagePaths.put("Tablet", "images/tablet.png");
        itemImagePaths.put("Laptop", "images/laptop.png");
        itemImagePaths.put("Camera", "images/camera.png");
        itemImagePaths.put("TV", "images/tv.png");
        itemImagePaths.put("Console", "images/game.png");

        itemImagePaths.put("Notebook", "images/notebook.png");
        itemImagePaths.put("Pen", "images/pen.png");
        itemImagePaths.put("Pencil", "images/pencil.png");
        itemImagePaths.put("Eraser", "images/eraser.png");
        itemImagePaths.put("Marker", "images/marker.png");
        itemImagePaths.put("Sharpener", "images/sharpener.png");

        itemImagePaths.put("Hat", "images/hat.png");
        itemImagePaths.put("Sunglasses", "images/sunglasses.png");
        itemImagePaths.put("Watch", "images/watch.png");
        itemImagePaths.put("Gloves", "images/gloves.png");
        itemImagePaths.put("Backpack", "images/backpack.png");
        itemImagePaths.put("Scarf", "images/scarf.png");

        itemImagePaths.put("Football", "images/football.png");
        itemImagePaths.put("Basketball", "images/basketball.png");
        itemImagePaths.put("Baseball", "images/ball.png");
        itemImagePaths.put("Cricket Bat", "images/cricket-bat.png");
        itemImagePaths.put("Tennis Racket", "images/tennis-racket.png");
        itemImagePaths.put("Badminton Racket", "images/badminton-racket.png");
    }

    public CartPage(JFrame frame, Map<String, CartItem> cartItems, JPanel previousPage) {
        this.frame = frame;
        this.cartItems = cartItems;
        this.previousPage = previousPage;
        setLayout(new BorderLayout());

        // Set the primary background color
        setBackground(Color.decode("#021526"));

        // Title label
        JLabel cartLabel = new JLabel("Cart", SwingConstants.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cartLabel.setForeground(Color.decode("#F9E2AF"));
        add(cartLabel, BorderLayout.NORTH);

        // Display cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setBackground(Color.decode("#021526")); // Background color for items panel
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        updateCartDisplay(itemsPanel);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.decode("#021526")); // Background color for bottom panel

        // Total Price Label
        JLabel totalPriceLabel = new JLabel("Total Price: $" + calculateTotalPrice());
        totalPriceLabel.setForeground(Color.decode("#F9E2AF")); // Text color
        bottomPanel.add(totalPriceLabel);

        // Clear Cart Button
        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.setBackground(Color.decode("#009FBD")); // Button background color
        clearCartButton.setForeground(Color.decode("#021526")); // Button text color
        clearCartButton.addActionListener(e -> {
            cartItems.clear();
            updateCartDisplay(itemsPanel);
            totalPriceLabel.setText("Total Price: $0.0");
        });
        bottomPanel.add(clearCartButton);

        // Proceed to Pay Button
        JButton proceedButton = new JButton("Proceed to Pay");
        proceedButton.setBackground(Color.decode("#009FBD")); // Button background color
        proceedButton.setForeground(Color.decode("#021526")); // Button text color
        proceedButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Proceed to payment.");
        });
        bottomPanel.add(proceedButton);

        add(bottomPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#009FBD")); // Background color for top panel
        
        JButton backButton = createBackButton(previousPage);
        topPanel.add(backButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void updateCartDisplay(JPanel itemsPanel) {
        itemsPanel.removeAll();

        if (cartItems.isEmpty()) {
            // Create a panel for empty cart content
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.decode("#021526")); // Background color for the empty panel

            // Create a sub-panel to hold the image and text
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.decode("#021526")); // Match the background color

            // Add the image
            ImageIcon binIcon = createResizedImageIcon("images/empty.png", 100, 100); // Adjust size as needed
            JLabel binLabel = new JLabel(binIcon);
            binLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(binLabel);

            // Add some vertical space
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Add the text
            JLabel emptyCartLabel = new JLabel("Your cart is empty.");
            emptyCartLabel.setForeground(Color.decode("#F9E2AF")); // Text color
            emptyCartLabel.setFont(new Font("Arial", Font.BOLD, 16));
            emptyCartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyCartLabel);

            // Add the content panel to the empty panel
            emptyPanel.add(contentPanel);

            // Add the empty panel to the items panel
            itemsPanel.add(emptyPanel, BorderLayout.CENTER);
        } else {
            for (CartItem cartItem : cartItems.values()) {
                JPanel itemPanel = new JPanel(new GridBagLayout());
                itemPanel.setBackground(Color.decode("#021526")); // Background color for item panel
                itemPanel.setBorder(new LineBorder(GRID_LINE_COLOR, 1)); // Border for grid lines
                GridBagConstraints gbc = new GridBagConstraints();

                // Box A: Image
                JLabel itemImageLabel = new JLabel();
                String imagePath = itemImagePaths.getOrDefault(cartItem.getItemName(), "images/default.png");
                ImageIcon itemImage = createResizedImageIcon(imagePath, 75, 75);
                itemImageLabel.setIcon(itemImage);
                itemImageLabel.setPreferredSize(new Dimension(100, 100));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridheight = 2; // Span 2 rows
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.WEST;
                itemPanel.add(itemImageLabel, gbc);

                // Box B: Details
                JPanel boxB = new JPanel();
                boxB.setBackground(Color.decode("#021526")); // Background color for details box
                boxB.setLayout(new BoxLayout(boxB, BoxLayout.Y_AXIS)); // Stack details vertically

                JLabel itemNameLabel = new JLabel("<html><div style='width:100px;'>" + cartItem.getItemName() + "</div></html>");
                itemNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                itemNameLabel.setForeground(Color.decode("#F9E2AF")); // Text color
                JLabel itemPriceLabel = new JLabel("Price: $" + cartItem.getPrice());
                itemPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                itemPriceLabel.setForeground(Color.decode("#F9E2AF")); // Text color
                JLabel itemQtyLabel = new JLabel("Qty.: " + cartItem.getQuantity());
                itemQtyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                itemQtyLabel.setForeground(Color.decode("#F9E2AF")); // Text color
                boxB.add(itemNameLabel);
                boxB.add(itemPriceLabel);
                boxB.add(itemQtyLabel);
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.CENTER;
                itemPanel.add(boxB, gbc);

                // Box C: Total Price
                JLabel totalPriceLabel = new JLabel("$" + (cartItem.getPrice() * cartItem.getQuantity()));
                totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
                totalPriceLabel.setForeground(Color.decode("#F9E2AF")); // Text color
                totalPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                gbc.gridx = 2;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.EAST;
                itemPanel.add(totalPriceLabel, gbc);

                itemsPanel.add(itemPanel);
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private JButton createBackButton(JPanel p) {
        // Create a panel to hold the back image
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Center align image
        backPanel.setOpaque(false); // Make the panel transparent

        // Load and add the back image
        ImageIcon backIcon = createResizedImageIcon("images/back.png", 30, 30); // Adjust size as needed
        JLabel backImageLabel = new JLabel(backIcon);
        backPanel.add(backImageLabel);

        // Create a button with an empty icon
        JButton backButton = new JButton();
        backButton.setIcon(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))); // Create a transparent icon
        backButton.setContentAreaFilled(false); // Remove default background
        backButton.setBorderPainted(false); // Remove border
        backButton.setFocusPainted(false); // Remove focus border
        backButton.setOpaque(false); // Make button opaque (remove button color)
        backButton.setPreferredSize(new Dimension(100, 45)); // Set preferred size to avoid jiggling

        // Add the panel to the button
        backPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 37));
        backButton.setLayout(new BorderLayout()); // Use BorderLayout to ensure proper display
        backButton.add(backPanel, BorderLayout.CENTER);

        // Add action listener to the button
        backButton.addActionListener(e -> {
        	frame.setContentPane(p);
            frame.revalidate();
            frame.repaint();
        });

        // Add mouse listeners to handle hover effects
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setBorder(BorderFactory.createLineBorder(Color.decode("#FFAC33"), 2)); // 2-pixel border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });

        return backButton;
    }

    private ImageIcon createResizedImageIcon(String path, int width, int height) {
        BufferedImage bufferedImage = loadImage(path);
        if (bufferedImage != null) {
            Image resizedImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } else {
            return new ImageIcon("images/default.png");
        }
    }

    private BufferedImage loadImage(String path) {
        try {
            URL imgURL = getClass().getResource("/" + path);
            if (imgURL != null) {
                return ImageIO.read(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error reading image file: " + path);
            return null;
        }
    }

    private double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem cartItem : cartItems.values()) {
            totalPrice += cartItem.getPrice() * cartItem.getQuantity();
        }
        return totalPrice;
    }
}
