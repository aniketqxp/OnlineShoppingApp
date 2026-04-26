import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WishlistPage extends JPanel {

    private final JFrame frame;
    private final AppState state = AppState.get();
    private JScrollPane scroll;

    public WishlistPage(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        scroll = new JScrollPane(buildGrid());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getViewport().setBackground(Theme.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        header.setPreferredSize(new Dimension(0, 58));

        JButton back = UIUtils.navIconButton("images/back.png", 24);
        back.addActionListener(e -> UIUtils.navigate(frame, new HomePage(frame)));
        header.add(back, BorderLayout.WEST);

        JLabel title = new JLabel("My Wishlist", SwingConstants.CENTER);
        title.setFont(Theme.bold(18f));
        title.setForeground(Theme.TEXT_ON_DARK);
        header.add(title, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(UIUtils.cartBadgePanel(frame, () -> new WishlistPage(frame)));
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel buildGrid() {
        Set<String> wishlist = state.getWishlist();
        List<Product> products = ProductCatalog.getAll().stream()
                .filter(p -> wishlist.contains(p.name))
                .collect(Collectors.toList());

        if (products.isEmpty()) return buildEmptyState();

        JPanel panel = new JPanel(new GridLayout(0, 3, 16, 16));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        for (Product p : products) panel.add(buildCard(p));
        return panel;
    }

    private void rebuildGrid() {
        scroll.setViewportView(buildGrid());
        scroll.revalidate();
        scroll.repaint();
    }

    private JPanel buildEmptyState() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.BG);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(Theme.BG);

        JLabel heart = UIUtils.heartLabel(56, Theme.HEART_OFF, false);
        heart.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Your wishlist is empty");
        msg.setFont(Theme.bold(18f));
        msg.setForeground(Theme.TEXT);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Tap the heart button on any product to save it here");
        sub.setFont(Theme.body(13f));
        sub.setForeground(Theme.TEXT_2);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(heart);
        inner.add(Box.createRigidArea(new Dimension(0, 12)));
        inner.add(msg);
        inner.add(Box.createRigidArea(new Dimension(0, 6)));
        inner.add(sub);

        p.add(inner);
        return p;
    }

    private JPanel buildCard(Product product) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(3, 12, 3, 12);

        JLabel img = new JLabel(ImageCache.get().icon(product.imagePath, 100, 100), SwingConstants.CENTER);
        img.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        img.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                UIUtils.showDetail(product, frame);
            }
        });
        g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(16, 12, 8, 12);
        card.add(img, g);
        g.insets = new Insets(3, 12, 3, 12);

        JLabel name = new JLabel(product.name, SwingConstants.CENTER);
        name.setFont(Theme.bold(14f));
        name.setForeground(Theme.TEXT);
        g.gridy = 1;
        card.add(name, g);

        g.gridy = 2;
        card.add(UIUtils.starLabel(UIUtils.displayRating(product)), g);

        JLabel price = new JLabel(product.formattedPrice(), SwingConstants.CENTER);
        price.setFont(Theme.bold(18f));
        price.setForeground(Theme.TEXT);
        g.gridy = 3;
        g.insets = new Insets(4, 12, 4, 12);
        card.add(price, g);

        RoundedButton addBtn = UIUtils.primaryButton("Add to Cart");
        addBtn.addActionListener(e -> {
            state.addToCart(product, 1);
            ToastNotification.showSuccess(frame, product.name + " added to cart!");
        });

        JButton removeBtn = UIUtils.ghostButton("Remove");
        removeBtn.addActionListener(e -> {
            state.toggleWishlist(product.name);
            rebuildGrid();
        });

        JPanel btns = new JPanel(new GridLayout(1, 2, 8, 0));
        btns.setOpaque(false);
        btns.add(addBtn);
        btns.add(removeBtn);
        g.gridy = 4;
        g.insets = new Insets(6, 12, 14, 12);
        card.add(btns, g);

        UIUtils.addHoverBorder(card, Theme.CTA, 2, Theme.BORDER, 1);
        return card;
    }
}
