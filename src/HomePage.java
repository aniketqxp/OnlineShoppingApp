import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HomePage extends JPanel {

    private JFrame frame;
    private Map<String, CartItem> cartItems;

    public HomePage(JFrame frame) {
        this.frame = frame;
        this.cartItems = new HashMap<>(); // Initialize the cartItems map
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Shopping App", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new GridLayout(2, 2, 20, 20));

        JButton electronicsButton = createCategoryButton("Electronics");
        JButton stationeryButton = createCategoryButton("Stationery");
        JButton accessoriesButton = createCategoryButton("Accessories");
        JButton sportsButton = createCategoryButton("Sports");

        categoriesPanel.add(electronicsButton);
        categoriesPanel.add(stationeryButton);
        categoriesPanel.add(accessoriesButton);
        categoriesPanel.add(sportsButton);

        add(categoriesPanel, BorderLayout.CENTER);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cartButton = new JButton("Cart");
        cartButton.addActionListener(e -> {
            frame.setContentPane(new CartPage(frame, cartItems));
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
            switch (category) {
                case "Electronics":
                    frame.setContentPane(new ElectronicsPage(frame, category, cartItems));
                    break;
                case "Stationery":
                    frame.setContentPane(new StationeryPage(frame, category, cartItems));
                    break;
                case "Accessories":
                    frame.setContentPane(new AccessoriesPage(frame, category, cartItems));
                    break;
                case "Sports":
                    frame.setContentPane(new SportsPage(frame, category, cartItems));
                    break;
                default:
                    JOptionPane.showMessageDialog(frame, "Category not recognized.");
                    return;
            }
            frame.revalidate();
            frame.repaint();
        });
        return button;
    }
}
