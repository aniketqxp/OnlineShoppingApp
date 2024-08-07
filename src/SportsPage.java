import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import javax.swing.border.LineBorder;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class SportsPage extends JPanel {

    private JFrame frame;
    private String category;
    private Map<String, CartItem> cartItems;
    
    // Define theme colors
    private static final Color PRIMARY_BACKGROUND_COLOR = Color.decode("#FFFDED");
    private static final Color SECONDARY_BACKGROUND_COLOR = Color.decode("#201335");
    private static final Color TITLE_TEXT_COLOR = Color.decode("#FCE762");
    private static final Color OTHER_TEXT_COLOR = Color.decode("#4F4789");
    private static final Color GRID_LINE_COLOR = Color.decode("#1F2833");

    public SportsPage(JFrame frame, String category, Map<String, CartItem> cartItems) {
        this.frame = frame;
        this.category = category;
        this.cartItems = cartItems != null ? cartItems : new HashMap<>();
        setLayout(new BorderLayout());
        setBackground(PRIMARY_BACKGROUND_COLOR);

        JLabel categoryLabel = new JLabel(category, SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(categoryLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(2, 3, 20, 20)); // 2 rows, 3 columns grid
        itemsPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for items panel

        // Define the items and their images
        String[] sportsItems = {"Football", "Basketball", "Baseball", "Cricket Bat", "Tennis Racket", "Badminton Racket"};
        int[] sportsPrices = {25, 30, 15, 50, 40, 20}; // Sample prices for the items
        String[] imagePaths = {"images/football.png", "images/basketball.png", "images/ball.png", 
                                "images/cricket-bat.png", "images/tennis-racket.png", "images/badminton.png"};

        for (int i = 0; i < sportsItems.length; i++) {
            JPanel itemPanel = createItemPanel(sportsItems[i], sportsPrices[i], imagePaths[i]);
            itemsPanel.add(itemPanel);
        }

        add(itemsPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SECONDARY_BACKGROUND_COLOR); // Background color for the top panel
        JButton backButton = createBackButton(frame);
        topPanel.add(backButton, BorderLayout.WEST);

        JButton cartButton = createCartButton();
        topPanel.add(cartButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createItemPanel(String item, int price, String imagePath) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridBagLayout());
        itemPanel.setBackground(PRIMARY_BACKGROUND_COLOR); // Background color for item panel
        itemPanel.setBorder(new LineBorder(GRID_LINE_COLOR, 1)); // Add grid line border
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Set up the constraints for the image
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10); // Padding
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Load and resize image for the item
        ImageIcon itemImage = createResizedImageIcon(imagePath, 100, 100);
        JLabel itemImageLabel = new JLabel(itemImage);
        itemPanel.add(itemImageLabel, gbc);

        // Set up the constraints for the item name
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 5, 10); // Padding
        JLabel nameLabel = new JLabel(item, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Smaller font for name
        nameLabel.setForeground(OTHER_TEXT_COLOR); // Other text color
        itemPanel.add(nameLabel, gbc);

        // Set up the constraints for the item price
        gbc.gridy = 2;
        JLabel priceLabel = new JLabel("$" + price, SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Larger font for price
        priceLabel.setForeground(OTHER_TEXT_COLOR); // Other text color
        itemPanel.add(priceLabel, gbc);

        // Panel for quantity controls and add to cart button
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Center align with padding
        controlPanel.setOpaque(false); // Make controlPanel transparent

        // Load and resize images for buttons
        ImageIcon minusIcon = createResizedImageIcon("images/minus.png", 20, 20);
        ImageIcon plusIcon = createResizedImageIcon("images/plus.png", 20, 20);
        ImageIcon addToCartIcon = createResizedImageIcon("images/add-to-cart.png", 30, 30);

        JButton minusButton = new JButton(minusIcon);
        JButton plusButton = new JButton(plusIcon);
        JTextField quantityField = new JTextField("0", 2);
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        quantityField.setPreferredSize(new Dimension(40, 20)); // Set preferred size to avoid scaling issues
        quantityField.setOpaque(false); // Make text field transparent
        quantityField.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        
        // Set font size and color for the quantity field
        Font font = new Font("Arial", Font.PLAIN, 12); // Font size is twice what it was (14*2)
        quantityField.setFont(font);
        quantityField.setForeground(OTHER_TEXT_COLOR); // Set text color

        quantityField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (quantity < 0) {
                        showAlert("Please enter a valid number greater than or equal to 0.");
                        quantityField.setText("0");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Please enter a valid number.");
                    quantityField.setText("0");
                }
            }
        });

        JButton addToCartButton = new JButton(addToCartIcon);

        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                quantityField.setText(String.valueOf(quantity + 1));
            }
        });

        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    quantityField.setText(String.valueOf(quantity - 1));
                }
            }
        });

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    CartItem cartItem = cartItems.get(item);
                    if (cartItem != null) {
                        cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    } else {
                        cartItems.put(item, new CartItem(item, price, quantity));
                    }
                    JOptionPane.showMessageDialog(frame, item + " added to cart.");
                } else {
                    showAlert("Please enter a quantity greater than 0.");
                }
            }
        });

        // Set transparent backgrounds for buttons
        minusButton.setOpaque(false);
        minusButton.setContentAreaFilled(false);
        minusButton.setBorderPainted(false);
        
        plusButton.setOpaque(false);
        plusButton.setContentAreaFilled(false);
        plusButton.setBorderPainted(false);
        
        addToCartButton.setOpaque(false);
        addToCartButton.setContentAreaFilled(false);
        addToCartButton.setBorderPainted(false);

        // Add buttons to the control panel
        controlPanel.add(minusButton);
        controlPanel.add(quantityField);
        controlPanel.add(plusButton);
        controlPanel.add(addToCartButton);

        // Set up the constraints for the control panel
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        itemPanel.add(controlPanel, gbc);

        return itemPanel;
    }
    
    private JButton createBackButton(JFrame frame) {
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
            frame.setContentPane(new HomePage(frame));
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
    
    private JButton createCartButton() {
        // Create a panel to hold the cart image
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Center align image
        cartPanel.setOpaque(false); // Make the panel transparent

        // Load and add the cart image
        ImageIcon cartIcon = createResizedImageIcon("images/cart.png", 30, 30); // Adjust size as needed
        JLabel cartImageLabel = new JLabel(cartIcon);
        cartPanel.add(cartImageLabel);

        // Create a button with an empty icon
        JButton cartButton = new JButton();
        cartButton.setIcon(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))); // Create a transparent icon
        cartButton.setContentAreaFilled(false); // Remove default background
        cartButton.setBorderPainted(false); // Remove border
        cartButton.setFocusPainted(false); // Remove focus border
        cartButton.setOpaque(false); // Make button opaque (remove button color)
        cartButton.setPreferredSize(new Dimension(100, 45)); // Set preferred size to avoid jiggling

        // Add the panel to the button
        cartButton.setLayout(new BorderLayout()); // Use BorderLayout to ensure proper display
        cartButton.add(cartPanel, BorderLayout.CENTER);

        // Add action listener to the button
        cartButton.addActionListener(e -> {
            frame.setContentPane(new CartPage(frame, cartItems, this));
            frame.revalidate();
            frame.repaint();
        });

        // Add mouse listeners to handle hover effects
        cartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                cartButton.setBorder(BorderFactory.createLineBorder(Color.decode("#FFAC33"), 2)); // 2-pixel border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                cartButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border when not hovering
            }
        });

        return cartButton;
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

    private void showAlert(String message) {
        JOptionPane.showMessageDialog(frame, message, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }
}
