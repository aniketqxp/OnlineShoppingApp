import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class StationeryPage extends JPanel {

    private JFrame frame;
    private String category;
    private Map<String, CartItem> cartItems;

    public StationeryPage(JFrame frame, String category, Map<String, CartItem> cartItems) {
        this.frame = frame;
        this.category = category;
        this.cartItems = cartItems != null ? cartItems : new HashMap<>();
        setLayout(new BorderLayout());

        JLabel categoryLabel = new JLabel(category, SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(categoryLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(2, 3, 20, 20)); // 2 rows, 3 columns grid

        // Define the items and their images
        String[] stationeryItems = {"Notebook", "Pen", "Pencil", "Eraser", "Marker", "Sharpener"};
        int[] stationeryPrices = {10, 1, 1, 3, 2, 1}; // Sample prices for the items
        String[] imagePaths = {"images/notebook.png", "images/pen.png", "images/pencil.png", 
                                "images/eraser.png", "images/marker.png", "images/sharpener.png"};

        for (int i = 0; i < stationeryItems.length; i++) {
            JPanel itemPanel = createItemPanel(stationeryItems[i], stationeryPrices[i], imagePaths[i]);
            itemsPanel.add(itemPanel);
        }

        add(itemsPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.setContentPane(new HomePage(frame));
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JButton cartButton = new JButton("Cart");
        cartButton.addActionListener(e -> {
        	frame.setContentPane(new CartPage(frame, cartItems, this));
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(cartButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createItemPanel(String item, int price, String imagePath) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Set up the constraints for the image
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 5, 10); // Padding
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load image for the item
        ImageIcon itemImage = createResizedImageIcon(imagePath, 150, 150);
        JLabel itemImageLabel = new JLabel(itemImage);
        itemPanel.add(itemImageLabel, gbc);

        // Set up the constraints for the item name
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 5, 10); // Padding
        JLabel nameLabel = new JLabel(item, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Smaller font for name
        itemPanel.add(nameLabel, gbc);

        // Set up the constraints for the item price
        gbc.gridy = 2;
        JLabel priceLabel = new JLabel("$" + price, SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Larger font for price
        itemPanel.add(priceLabel, gbc);

        // Panel for quantity controls and add to cart button
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton minusButton = new JButton("-");
        JTextField quantityField = new JTextField("0", 2);
        quantityField.setHorizontalAlignment(JTextField.CENTER);

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

        JButton plusButton = new JButton("+");
        JButton addToCartButton = new JButton("Add to cart");

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

        controlPanel.add(minusButton);
        controlPanel.add(quantityField);
        controlPanel.add(plusButton);
        controlPanel.add(addToCartButton);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        itemPanel.add(controlPanel, gbc);

        return itemPanel;
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
