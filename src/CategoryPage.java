import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Supplier;

public class CategoryPage extends JPanel {

    private final JFrame    frame;
    private final Category  category;
    private SortOrder       sortOrder = SortOrder.DEFAULT;
    private JScrollPane     scroll;

    public CategoryPage(JFrame frame, Category category) {
        this.frame    = frame;
        this.category = category;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        add(buildTopBar(), BorderLayout.NORTH);
        scroll = buildScrollPane();
        add(scroll, BorderLayout.CENTER);
    }

    // ── Top bar: header + toolbar in one block ────────────────────────────────

    private JPanel buildTopBar() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(buildHeader(),  BorderLayout.NORTH);
        wrap.add(buildToolbar(), BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        header.setPreferredSize(new Dimension(0, 58));

        // Back
        JButton back = UIUtils.navIconButton("images/back.png", 24);
        back.addActionListener(e -> UIUtils.navigate(frame, new HomePage(frame)));
        header.add(back, BorderLayout.WEST);

        // Breadcrumb: Home > Category
        JPanel crumb = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        crumb.setOpaque(false);
        JLabel home = crumbLink("Home");
        home.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                UIUtils.navigate(frame, new HomePage(frame));
            }
        });
        JLabel sep  = crumbSep();
        JLabel curr = new JLabel(category.label);
        curr.setFont(Theme.bold(14f));
        curr.setForeground(Theme.TEXT_ON_DARK);
        crumb.add(home); crumb.add(sep); crumb.add(curr);
        header.add(crumb, BorderLayout.CENTER);

        // Right icons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton wishBtn = UIUtils.navIconButton("images/shopping-bag.png", 24);
        wishBtn.setToolTipText("My Wishlist");
        wishBtn.addActionListener(e -> UIUtils.navigate(frame, new WishlistPage(frame)));

        Supplier<JPanel> backHere = () -> new CategoryPage(frame, category);
        right.add(wishBtn);
        right.add(UIUtils.cartBadgePanel(frame, backHere));
        header.add(right, BorderLayout.EAST);

        // Category accent underline
        JPanel accent = new JPanel();
        accent.setBackground(Theme.categoryAccent(category));
        accent.setPreferredSize(new Dimension(0, 3));
        header.add(accent, BorderLayout.SOUTH);

        return header;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bar.setBackground(Theme.SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JLabel lbl = new JLabel("Sort by:");
        lbl.setFont(Theme.bold(13f));
        lbl.setForeground(Theme.TEXT_2);

        JComboBox<SortOrder> sortBox = new JComboBox<>(SortOrder.values());
        sortBox.setFont(Theme.body(13f));
        sortBox.setForeground(Theme.TEXT);
        sortBox.setBackground(Theme.SURFACE);
        sortBox.addActionListener(e -> {
            sortOrder = (SortOrder) sortBox.getSelectedItem();
            rebuildGrid();
        });

        int count = ProductCatalog.getByCategory(category).size();
        JLabel countLbl = new JLabel(count + " items");
        countLbl.setFont(Theme.body(12f));
        countLbl.setForeground(Theme.TEXT_2);

        bar.add(lbl);
        bar.add(sortBox);
        bar.add(countLbl);
        return bar;
    }

    // ── Product grid ──────────────────────────────────────────────────────────

    private JScrollPane buildScrollPane() {
        JScrollPane sc = new JScrollPane(buildGrid());
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(20);
        sc.getViewport().setBackground(Theme.BG);
        return sc;
    }

    private JPanel buildGrid() {
        List<Product> products = ProductCatalog.sort(
                ProductCatalog.getByCategory(category), sortOrder);

        JPanel panel = new JPanel(new GridLayout(0, 3, 16, 16));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        for (Product p : products) panel.add(UIUtils.productCard(p, frame));
        return panel;
    }

    private void rebuildGrid() {
        scroll.setViewportView(buildGrid());
        scroll.revalidate();
        scroll.repaint();
    }

    // ── Breadcrumb helpers ────────────────────────────────────────────────────

    private static JLabel crumbLink(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.body(14f));
        lbl.setForeground(Color.decode("#94A3B8"));
        lbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                lbl.setForeground(Theme.TEXT_ON_DARK);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                lbl.setForeground(Color.decode("#94A3B8"));
            }
        });
        return lbl;
    }

    private static JLabel crumbSep() {
        JLabel sep = new JLabel("/");
        sep.setFont(Theme.body(14f));
        sep.setForeground(Color.decode("#64748B"));
        return sep;
    }
}
