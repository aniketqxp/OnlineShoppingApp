import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CartPage extends JPanel {

    private JFrame frame;
    private Map<String, CartItem> cartItems;

    public CartPage(JFrame frame, Map<String, CartItem> cartItems) {
        this.frame = frame;
        this.cartItems = cartItems;
        setLayout(new BorderLayout());

        JLabel cartLabel = new JLabel("Cart", SwingConstants.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(cartLabel, BorderLayout.NORTH);

        // Display cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        if (cartItems.isEmpty()) {
            JLabel placeholderLabel = new JLabel("Cart is empty.");
            itemsPanel.add(placeholderLabel);
        } else {
            for (CartItem cartItem : cartItems.values()) {
                JLabel itemLabel = new JLabel(cartItem.getItemName() + " - $" + cartItem.getPrice() + " x " + cartItem.getQuantity());
                itemsPanel.add(itemLabel);
            }
        }

        add(itemsPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            // Navigate to a specific page, or use a HomePage or appropriate page
            frame.setContentPane(new HomePage(frame)); // Use HomePage to go back
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }
}
