import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomePage extends JPanel {

    private static final String PLACEHOLDER = "Search products...";
    private final JFrame frame;

    public HomePage(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        header.setPreferredSize(new Dimension(0, 64));

        JLabel title = new JLabel("VectorStore");
        title.setFont(Theme.brand(32f));
        title.setForeground(Theme.TEXT_ON_DARK);
        header.add(title, BorderLayout.WEST);

        header.add(buildSearchBar(), BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton wishBtn = UIUtils.navIconButton("images/shopping-bag.png", 26);
        wishBtn.setToolTipText("My Wishlist");
        wishBtn.addActionListener(e -> UIUtils.navigate(frame, new WishlistPage(frame)));

        right.add(wishBtn);
        right.add(UIUtils.cartBadgePanel(frame, () -> new HomePage(frame)));
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSearchBar() {
        RoundedPanel bar = new RoundedPanel(8, Color.WHITE, Theme.BORDER_2, 1.5f);
        bar.setLayout(new BorderLayout(0, 0));
        bar.setMaximumSize(new Dimension(420, 40));

        JTextField field = new JTextField(PLACEHOLDER);
        field.setFont(Theme.body(14f));
        field.setForeground(Color.decode("#9CA3AF"));
        field.setBackground(Color.WHITE);
        field.setOpaque(false);
        field.setCaretColor(Theme.TEXT);
        field.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
        field.setPreferredSize(new Dimension(0, 42));

        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (PLACEHOLDER.equals(field.getText())) {
                    field.setText("");
                    field.setForeground(Theme.TEXT);
                }
                bar.repaint();
            }

            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isBlank()) {
                    field.setText(PLACEHOLDER);
                    field.setForeground(Color.decode("#9CA3AF"));
                }
            }
        });
        field.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) doSearch(field.getText());
            }
        });

        RoundedButton searchBtn = new RoundedButton("Search", Theme.CTA, Color.WHITE, 0);
        searchBtn.setFont(Theme.bold(14f));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        searchBtn.addActionListener(e -> doSearch(field.getText()));

        bar.add(field, BorderLayout.CENTER);
        bar.add(searchBtn, BorderLayout.EAST);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrap.setOpaque(false);
        bar.setPreferredSize(new Dimension(380, 42));
        wrap.add(bar);
        return wrap;
    }

    private JPanel buildGrid() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Theme.BG);
        outer.setBorder(BorderFactory.createEmptyBorder(24, 24, 12, 24));

        JLabel heading = new JLabel("Shop by Category");
        heading.setFont(Theme.bold(20f));
        heading.setForeground(Theme.TEXT);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        outer.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(Theme.BG);
        for (Category cat : Category.values()) grid.add(buildTile(cat));
        outer.add(grid, BorderLayout.CENTER);

        return outer;
    }

    private JButton buildTile(Category cat) {
        Color accent = Theme.categoryAccent(cat);
        Color light = Theme.categoryLight(cat);

        JButton tile = new JButton();
        tile.setLayout(new BorderLayout(0, 0));
        tile.setBackground(Theme.SURFACE);
        tile.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        tile.setContentAreaFilled(true);
        tile.setOpaque(true);
        tile.setFocusPainted(false);
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tile.addActionListener(e -> UIUtils.navigate(frame, new CategoryPage(frame, cat)));

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setBackground(Theme.SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(22, 20, 16, 20));

        JLabel icon = new JLabel(ImageCache.get().icon(cat.iconPath, 120, 120), SwingConstants.CENTER);
        JLabel name = new JLabel(cat.label, SwingConstants.CENTER);
        name.setFont(Theme.heading(15f));
        name.setForeground(Theme.TEXT);

        int count = ProductCatalog.getByCategory(cat).size();
        JLabel sub = new JLabel(count + " products", SwingConstants.CENTER);
        sub.setFont(Theme.body(12f));
        sub.setForeground(Theme.TEXT_2);

        JPanel labels = new JPanel(new BorderLayout(0, 3));
        labels.setBackground(Theme.SURFACE);
        labels.add(name, BorderLayout.CENTER);
        labels.add(sub, BorderLayout.SOUTH);

        content.add(icon, BorderLayout.CENTER);
        content.add(labels, BorderLayout.SOUTH);
        tile.add(content, BorderLayout.CENTER);

        JPanel strip = new JPanel();
        strip.setBackground(accent);
        strip.setPreferredSize(new Dimension(0, 5));
        tile.add(strip, BorderLayout.SOUTH);

        tile.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                tile.setBorder(BorderFactory.createLineBorder(accent, 2));
                tile.setBackground(light);
                content.setBackground(light);
                labels.setBackground(light);
                tile.repaint();
            }

            @Override public void mouseExited(MouseEvent e) {
                tile.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
                tile.setBackground(Theme.SURFACE);
                content.setBackground(Theme.SURFACE);
                labels.setBackground(Theme.SURFACE);
                tile.repaint();
            }
        });

        return tile;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(Theme.HEADER);
        footer.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        JLabel lbl = new JLabel("© 2025 VectorStore • All Rights Reserved");
        lbl.setFont(Theme.body(11f));
        lbl.setForeground(Color.decode("#64748B"));
        footer.add(lbl);
        return footer;
    }

    private void doSearch(String text) {
        if (!text.isBlank() && !PLACEHOLDER.equals(text)) {
            UIUtils.navigate(frame, new SearchResultsPage(frame, text.trim()));
        }
    }
}
