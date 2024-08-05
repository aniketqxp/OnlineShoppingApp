import javax.swing.*;
import java.awt.*;

public class CategoryPage extends JPanel {

    private JFrame frame;
    private String category;

    public CategoryPage(JFrame frame, String category) {
        this.frame = frame;
        this.category = category;
        setLayout(new BorderLayout());

        JLabel categoryLabel = new JLabel(category, SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(categoryLabel, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new GridLayout(2, 3, 20, 20));

        String[] stationeryItems = {"Pencil", "Eraser", "Sharpener", "Ruler", "Marker", "Notebook"};
        for (String item : stationeryItems) {
            JButton itemButton = createItemButton(item);
            itemsPanel.add(itemButton);
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
            frame.setContentPane(new CartPage(frame));
            frame.revalidate();
            frame.repaint();
        });
        topPanel.add(cartButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JButton createItemButton(String item) {
        JButton button = new JButton(item);
        button.setPreferredSize(new Dimension(100, 100));
        // Add action listener to add item to cart here in future
        return button;
    }
}
