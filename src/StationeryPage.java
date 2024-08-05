import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
        itemsPanel.setLayout(new GridLayout(2, 3, 20, 20));

        // Define the items and their prices
        String[] stationeryItems = {"Notebook", "Pen", "Pencil", "Eraser", "Marker", "Sharpener"};
        int[] stationeryPrices = {10, 1, 1, 3, 2, 1}; // Sample prices for the items

        for (int i = 0; i < stationeryItems.length; i++) {
            JPanel itemPanel = createItemPanel(stationeryItems[i], stationeryPrices[i]);
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
            frame.setContentPane(new CartPage(frame, cartItems));
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(cartButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createItemPanel(String item, int price) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());

        String buttonText = String.format("<html><center>%s<br><span style='font-size:12px;'>$%d</span></center></html>", item, price);
        JButton itemButton = new JButton(buttonText);
        itemButton.setPreferredSize(new Dimension(100, 100));
        itemPanel.add(itemButton, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JButton plusButton = new JButton("+");
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

        JButton minusButton = new JButton("-");
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

        controlPanel.add(plusButton);
        controlPanel.add(quantityField);
        controlPanel.add(minusButton);
        controlPanel.add(addToCartButton);

        itemPanel.add(controlPanel, BorderLayout.SOUTH);

        return itemPanel;
    }

    private void showAlert(String message) {
        JOptionPane.showMessageDialog(frame, message, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }
}
