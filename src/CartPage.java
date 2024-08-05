import javax.swing.*;
import java.awt.*;

public class CartPage extends JPanel {

    private JFrame frame;
    private Cart cart; // Add Cart instance

    public CartPage(JFrame frame, Cart cart) {
        this.frame = frame;
        this.cart = cart; // Initialize Cart
        setLayout(new BorderLayout());

        JLabel cartLabel = new JLabel("Cart", SwingConstants.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(cartLabel, BorderLayout.NORTH);

        // Create a panel to display cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        // Populate items from the cart
        updateCartDisplay(itemsPanel);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.setContentPane(new HomePage(frame, cart)); // Navigate back to HomePage
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Proceeding to checkout...");
            // Implement checkout logic here
        });
        topPanel.add(checkoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void updateCartDisplay(JPanel itemsPanel) {
        itemsPanel.removeAll(); // Clear existing items

        if (cart.getItems().isEmpty()) {
            JLabel emptyCartLabel = new JLabel("Your cart is empty.");
            itemsPanel.add(emptyCartLabel);
        } else {
            for (String item : cart.getItems()) {
                JLabel itemLabel = new JLabel(item);
                itemsPanel.add(itemLabel);
            }
        }

        itemsPanel.revalidate(); // Refresh the panel
        itemsPanel.repaint();
    }
}

