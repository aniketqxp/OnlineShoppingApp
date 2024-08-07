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

    // Color constants
    private static final Color PRIMARY_BACKGROUND_COLOR = Color.decode("#FFFDED");
    private static final Color TITLE_TEXT_COLOR = Color.decode("#4F4789");
    private static final Color GRID_LINE_COLOR = Color.decode("#1F2833");
    private static final Color BUTTON_BACKGROUND_COLOR = Color.decode("#201335");
    private static final Color BUTTON_TEXT_COLOR = Color.decode("#FFFDED");
    private static final Color HOVER_BORDER_COLOR = Color.decode("#FFAC33");

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
        itemImagePaths.put("Badminton Racket", "images/badminton.png");
    }

    public CartPage(JFrame frame, Map<String, CartItem> cartItems, JPanel previousPage) {
        this.frame = frame;
        this.cartItems = cartItems;
        this.previousPage = previousPage;
        setLayout(new BorderLayout());

        // Set the primary background color
        setBackground(PRIMARY_BACKGROUND_COLOR);

        // Title label
        JLabel cartLabel = new JLabel("Cart", SwingConstants.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cartLabel.setForeground(TITLE_TEXT_COLOR);
        add(cartLabel, BorderLayout.NORTH);

        // Display cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for items panel
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        updateCartDisplay(itemsPanel);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for clear cart and checkout buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for bottom panel

        // Clear Cart Button
        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.setBackground(BUTTON_BACKGROUND_COLOR); // Button background color
        clearCartButton.setForeground(BUTTON_TEXT_COLOR); // Button text color
        clearCartButton.addActionListener(e -> {
            cartItems.clear();
            updateCartDisplay(itemsPanel);
        });
        bottomPanel.add(clearCartButton);

        // Checkout Button
        JButton proceedButton = new JButton("Checkout");
        proceedButton.setBackground(BUTTON_BACKGROUND_COLOR); // Button background color
        proceedButton.setForeground(BUTTON_TEXT_COLOR); // Button text color
        proceedButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Proceed to payment.");
        });
        bottomPanel.add(proceedButton);

        add(bottomPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BUTTON_BACKGROUND_COLOR); // Background color for top panel

        JButton backButton = createBackButton(previousPage);
        topPanel.add(backButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void updateCartDisplay(JPanel itemsPanel) {
        itemsPanel.removeAll();

        if (cartItems.isEmpty()) {
            // Create a panel for empty cart content
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for the empty panel

            // Create a sub-panel to hold the image and text
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Match the background color

            // Add the image
            ImageIcon binIcon = createResizedImageIcon("images/empty.png", 100, 100); // Adjust size as needed
            JLabel binLabel = new JLabel(binIcon);
            binLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(binLabel);

            // Add some vertical space
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // Add the text
            JLabel emptyCartLabel = new JLabel("Your cart is empty.");
            emptyCartLabel.setForeground(TITLE_TEXT_COLOR); // Text color
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
                itemPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for item panel
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
                boxB.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for details box
                boxB.setLayout(new BoxLayout(boxB, BoxLayout.Y_AXIS)); // Stack details vertically

                JLabel itemNameLabel = new JLabel("<html><div style='width:100px;'>" + cartItem.getItemName() + "</div></html>");
                itemNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                itemNameLabel.setForeground(TITLE_TEXT_COLOR); // Text color
                JLabel itemPriceLabel = new JLabel("Price: $" + cartItem.getPrice());
                itemPriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                itemPriceLabel.setForeground(TITLE_TEXT_COLOR); // Text color
                JLabel itemQtyLabel = new JLabel("Qty.: " + cartItem.getQuantity());
                itemQtyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                itemQtyLabel.setForeground(TITLE_TEXT_COLOR); // Text color
                boxB.add(itemNameLabel);
                boxB.add(itemPriceLabel);
                boxB.add(itemQtyLabel);
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.CENTER;
                itemPanel.add(boxB, gbc);

                // Box C: Total Price
                JLabel itemTotalPriceLabel = new JLabel("$" + (cartItem.getPrice() * cartItem.getQuantity()));
                itemTotalPriceLabel.setFont(new Font("Arial", Font.BOLD, 24));
                itemTotalPriceLabel.setForeground(TITLE_TEXT_COLOR); // Text color
                itemTotalPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                gbc.gridx = 2;
                gbc.gridy = 0;
                gbc.gridheight = 2; // Span 2 rows
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.EAST;
                itemPanel.add(itemTotalPriceLabel, gbc);

                itemsPanel.add(itemPanel);
            }

            // Display total price
            double totalPrice = cartItems.values().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
            JLabel totalPriceLabel = new JLabel("Total: $" + totalPrice);
            totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 28));
            totalPriceLabel.setForeground(TITLE_TEXT_COLOR); // Text color
            totalPriceLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add total price label with GridBagConstraints
            JPanel totalPanel = new JPanel(new GridBagLayout());
            totalPanel.setBackground(PRIMARY_BACKGROUND_COLOR);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.anchor = GridBagConstraints.CENTER;

            totalPanel.add(totalPriceLabel, gbc);
            itemsPanel.add(totalPanel);
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private ImageIcon createResizedImageIcon(String path, int width, int height) {
        try {
            URL imageUrl = getClass().getResource(path);
            if (imageUrl == null) {
                throw new IOException("Image not found: " + path);
            }
            BufferedImage originalImage = ImageIO.read(imageUrl);
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
}
