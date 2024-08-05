import javax.swing.*;
import java.awt.*;

public class CartPage extends JPanel {

    private JFrame frame;

    public CartPage(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        JLabel cartLabel = new JLabel("Cart", SwingConstants.CENTER);
        cartLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(cartLabel, BorderLayout.NORTH);

        // Placeholder for cart items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        JLabel placeholderLabel = new JLabel("Cart items will be listed here.");
        itemsPanel.add(placeholderLabel);

        add(itemsPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.setContentPane(new HomePage(frame));
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
    }
}
