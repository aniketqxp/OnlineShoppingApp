import javax.swing.*;
import java.awt.*;

public class CategoryPage extends JPanel {

    private JFrame frame;
    private String category;
    private Cart cart; // Cart instance

    public CategoryPage(JFrame frame, String category, Cart cart) {
        this.frame = frame;
        this.category = category;
        this.cart = cart; // Initialize Cart
        setLayout(new BorderLayout());

        JLabel categoryLabel = new JLabel(category, SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(categoryLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(2, 3, 20, 20));

        // Define items based on category
        String[] items;
        switch (category) {
            case "Electronics":
                items = new String[]{"TV", "Radio", "Laptop", "Phone", "Headphones", "Camera"};
                break;
            case "Stationery":
                items = new String[]{"Pencil", "Eraser", "Sharpener", "Ruler", "Marker", "Notebook"};
                break;
            case "Accessories":
                items = new String[]{"Watch", "Bracelet", "Necklace", "Ring", "Earrings", "Sunglasses"};
                break;
            case "Sports":
                items = new String[]{"Football", "Basketball", "Tennis Racket", "Golf Club", "Baseball Bat", "Hockey Stick"};
                break;
            default:
                items = new String[]{};
                break;
        }

        // Create buttons for each item
        for (String item : items) {
            JButton itemButton = createItemButton(item);
            itemsPanel.add(itemButton);
        }

        add(itemsPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.setContentPane(new HomePage(frame, cart)); // Return to HomePage with Cart
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        JButton cartButton = new JButton("Cart");
        cartButton.addActionListener(e -> {
            frame.setContentPane(new CartPage(frame, cart)); // Pass Cart instance to CartPage
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(cartButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JButton createItemButton(String item) {
        JButton button = new JButton(item);
        button.setPreferredSize(new Dimension(100, 100));
        button.addActionListener(e -> {
            cart.addItem(item); // Add item to cart
            JOptionPane.showMessageDialog(this, item + " added to cart.");
        });
        return button;
    }
}
