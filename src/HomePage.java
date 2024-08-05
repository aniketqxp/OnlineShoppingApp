import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {

    private JFrame frame;
    private Cart cart;

    public HomePage(JFrame frame, Cart cart) {
        this.frame = frame;
        this.cart = cart; // Initialize Cart instance
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Shopping App", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new GridLayout(2, 2, 20, 20));

        // Create buttons for each category
        JButton electronicsButton = createCategoryButton("Electronics");
        JButton stationeryButton = createCategoryButton("Stationery");
        JButton accessoriesButton = createCategoryButton("Accessories");
        JButton sportsButton = createCategoryButton("Sports");

        // Add buttons to the panel
        categoriesPanel.add(electronicsButton);
        categoriesPanel.add(stationeryButton);
        categoriesPanel.add(accessoriesButton);
        categoriesPanel.add(sportsButton);

        add(categoriesPanel, BorderLayout.CENTER);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cartButton = new JButton("Cart");
        cartButton.addActionListener(e -> {
            frame.setContentPane(new CartPage(frame, cart)); // Pass Cart instance
            frame.revalidate();
            frame.repaint();
        });
        topRightPanel.add(cartButton);
        add(topRightPanel, BorderLayout.NORTH);
    }

    private JButton createCategoryButton(String category) {
        JButton button = new JButton(category);
        button.setPreferredSize(new Dimension(100, 100));
        button.addActionListener(e -> {
            // Pass the Cart instance to CategoryPage
            frame.setContentPane(new CategoryPage(frame, category, cart));
            frame.revalidate();
            frame.repaint();
        });
        return button;
    }
}
