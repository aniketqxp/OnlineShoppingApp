import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {

    private JFrame frame;

    public HomePage(JFrame frame) {
        this.frame = frame;
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
            frame.setContentPane(new CartPage(frame));
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
            frame.setContentPane(new CategoryPage(frame, category));
            frame.revalidate();
            frame.repaint();
        });
        return button;
    }
}