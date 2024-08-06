import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CartPage extends JPanel {

    private JFrame frame;
    private Map<String, CartItem> cartItems;
    private JPanel previousPage; // Reference to the previous page

    public CartPage(JFrame frame, Map<String, CartItem> cartItems, JPanel previousPage) {
        this.frame = frame;
        this.cartItems = cartItems;
        this.previousPage = previousPage; // Store the previous page reference
        setLayout(new BorderLayout());

        JLabel cartLabel = new JLabel("Cart", SwingConstants.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(cartLabel, BorderLayout.NORTH);

        // Display cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        updateCartDisplay(itemsPanel);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Clear Cart Button
        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.addActionListener(e -> {
            cartItems.clear(); // Clear cart items
            updateCartDisplay(itemsPanel); // Refresh display
        });
        bottomPanel.add(clearCartButton);

        // Total Price Label
        JLabel totalPriceLabel = new JLabel("Total Price: $" + calculateTotalPrice());
        bottomPanel.add(totalPriceLabel);

        // Proceed to Pay Button
        JButton proceedButton = new JButton("Proceed to Pay");
        proceedButton.addActionListener(e -> {
            // Proceed to payment logic here
            JOptionPane.showMessageDialog(frame, "Proceed to payment.");
        });
        bottomPanel.add(proceedButton);

        add(bottomPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.setContentPane(previousPage); // Go back to the previous page
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void updateCartDisplay(JPanel itemsPanel) {
        itemsPanel.removeAll(); // Clear existing items

        if (cartItems.isEmpty()) {
            JLabel emptyCartLabel = new JLabel("Your cart is empty.");
            itemsPanel.add(emptyCartLabel);
        } else {
            for (CartItem cartItem : cartItems.values()) {
                JLabel itemLabel = new JLabel(cartItem.getItemName() + " - $" + cartItem.getPrice() + " x " + cartItem.getQuantity());
                itemsPanel.add(itemLabel);
            }
        }

        itemsPanel.revalidate(); // Refresh the panel
        itemsPanel.repaint();
    }

    private double calculateTotalPrice() {
        double total = 0.0;
        for (CartItem cartItem : cartItems.values()) {
            total += cartItem.getPrice() * cartItem.getQuantity();
        }
        return total;
    }
}