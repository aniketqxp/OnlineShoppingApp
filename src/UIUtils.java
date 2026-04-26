import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class UIUtils {

    public static RoundedButton primaryButton(String text) {
        return new RoundedButton(text, Theme.CTA, Color.WHITE, 8);
    }

    public static RoundedButton darkButton(String text) {
        return new RoundedButton(text, Theme.HEADER, Theme.TEXT_ON_DARK, 8);
    }

    public static RoundedButton dangerButton(String text) {
        return new RoundedButton(text, Theme.DANGER, Color.WHITE, 8);
    }

    public static RoundedButton successButton(String text) {
        return new RoundedButton(text, Theme.SUCCESS, Color.WHITE, 8);
    }

    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.bold(13f));
        btn.setForeground(Theme.TEXT_2);
        btn.setBackground(Theme.SURFACE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_2, 1),
                BorderFactory.createEmptyBorder(7, 18, 7, 18)));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(Theme.HOVER_BG); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(Theme.SURFACE); }
        });
        return btn;
    }

    public static JButton navIconButton(String imagePath, int size) {
        JButton btn = new JButton(ImageCache.get().icon(imagePath, size, size));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBorderPainted(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.decode("#94A3B8"), 1));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBorderPainted(false);
            }
        });
        return btn;
    }

    public static JButton iconButton(String imagePath, int size) {
        JButton btn = new JButton(ImageCache.get().icon(imagePath, size, size));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void makeTransparent(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
    }

    public static JPanel starLabel(double rating) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setOpaque(false);

        int roundedRating = (int) Math.ceil(rating);
        for (int i = 0; i < 5; i++) {
            double fill = (i < roundedRating) ? 1.0 : 0.0;
            panel.add(new JLabel(new StarIcon(16, fill)));
        }

        JLabel value = new JLabel(String.format(" %.0f", (double)roundedRating));
        value.setForeground(Theme.STAR);
        value.setFont(Theme.bold(12f));
        panel.add(value);
        return panel;
    }

    public static double displayRating(Product product) {
        AppState state = AppState.get();
        return state.hasRated(product.name) ? state.getProductRating(product.name) : product.rating;
    }

    public static JButton heartButton(boolean on) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        makeTransparent(btn);
        applyHeartIcon(btn, on);
        return btn;
    }

    public static void applyHeartIcon(JButton button, boolean on) {
        Icon icon = new HeartIcon(22, on ? Theme.HEART_ON : Theme.HEART_OFF, on);
        button.setIcon(icon);
        button.setPressedIcon(icon);
        button.setRolloverIcon(icon);
    }

    public static JLabel heartLabel(int size, Color color, boolean filled) {
        return new JLabel(new HeartIcon(size, color, filled));
    }

    public static JButton ratingStarButton(boolean filled) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        makeTransparent(btn);
        applyRatingStar(btn, filled);
        return btn;
    }

    public static void applyRatingStar(JButton button, boolean filled) {
        Icon icon = new StarIcon(18, filled ? 1.0 : 0.0);
        button.setIcon(icon);
        button.setPressedIcon(icon);
        button.setRolloverIcon(icon);
    }

    public static JButton qtyButton(boolean plus) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setBackground(Theme.SURFACE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BORDER_2, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setIcon(new MathIcon(14, Theme.TEXT, plus));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(Theme.BG); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(Theme.SURFACE); }
        });
        return btn;
    }

    public static JPanel cartBadgePanel(JFrame frame, java.util.function.Supplier<JPanel> backDest) {
        AppState state = AppState.get();

        JLabel cartImg = new JLabel(ImageCache.get().icon("images/cart.png", 26, 26));
        JLabel countLbl = new JLabel("", SwingConstants.CENTER);
        countLbl.setOpaque(true);
        countLbl.setBackground(Theme.BADGE);
        countLbl.setForeground(Color.WHITE);
        countLbl.setFont(Theme.bold(9f));
        countLbl.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));

        JPanel container = new JPanel(null) {
            private final StateChangeListener listener = () ->
                SwingUtilities.invokeLater(() -> refreshBadge(countLbl, state.getCartItemCount()));
            {
                state.addListener(listener);
                refreshBadge(countLbl, state.getCartItemCount());
            }
            @Override public void removeNotify() {
                super.removeNotify();
                state.removeListener(listener);
            }
        };
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(46, 42));

        cartImg.setBounds(4, 8, 26, 26);
        countLbl.setBounds(20, 4, 22, 15);
        container.add(cartImg);
        container.add(countLbl);

        container.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        container.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                navigate(frame, new CartPage(frame, backDest));
            }
        });
        return container;
    }

    private static void refreshBadge(JLabel lbl, int count) {
        lbl.setVisible(count > 0);
        lbl.setText(count > 99 ? "99+" : String.valueOf(count));
    }

    public static void navigate(JFrame frame, JPanel page) {
        frame.setContentPane(page);
        frame.revalidate();
        frame.repaint();
    }

    public static JPanel productCard(Product product, JFrame frame) {
        AppState state = AppState.get();

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(2, 12, 2, 12);

        JButton heart = heartButton(state.isWishlisted(product.name));
        heart.addActionListener(e -> {
            state.toggleWishlist(product.name);
            boolean on = state.isWishlisted(product.name);
            applyHeartIcon(heart, on);
            ToastNotification.show(frame, on ? product.name + " saved to wishlist"
                                             : product.name + " removed from wishlist");
        });
        g.gridy = 0;
        g.anchor = GridBagConstraints.NORTHEAST;
        g.insets = new Insets(8, 0, 0, 12);
        card.add(heart, g);
        g.insets = new Insets(2, 12, 2, 12);

        JLabel img = new JLabel(ImageCache.get().icon(product.imagePath, 110, 110), SwingConstants.CENTER);
        img.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        img.setToolTipText("Click for details");
        img.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showDetail(product, frame); }
        });
        g.gridy = 1;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(4, 12, 8, 12);
        card.add(img, g);

        JLabel name = new JLabel(product.name, SwingConstants.CENTER);
        name.setFont(Theme.bold(14f));
        name.setForeground(Theme.TEXT);
        g.gridy = 2;
        g.insets = new Insets(2, 12, 2, 12);
        card.add(name, g);

        g.gridy = 3;
        card.add(starLabel(displayRating(product)), g);

        JLabel price = new JLabel(product.formattedPrice(), SwingConstants.CENTER);
        price.setFont(Theme.bold(18f));
        price.setForeground(Theme.TEXT);
        g.gridy = 4;
        g.insets = new Insets(4, 12, 2, 12);
        card.add(price, g);

        JTextField qtyField = styledQtyField("0");
        JButton minus = qtyButton(false);
        JButton plus = qtyButton(true);

        minus.addActionListener(e -> adjustQty(qtyField, -1));
        plus.addActionListener(e -> adjustQty(qtyField, 1));

        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        stepper.setOpaque(false);
        stepper.add(minus);
        stepper.add(qtyField);
        stepper.add(plus);
        g.gridy = 5;
        g.insets = new Insets(4, 12, 4, 12);
        card.add(stepper, g);

        RoundedButton addBtn = primaryButton("  Add to Cart");
        addBtn.addActionListener(e -> {
            int qty = parseQty(qtyField);
            if (qty < 1) {
                ToastNotification.showError(frame, "Quantity must be at least 1");
                return;
            }
            state.addToCart(product, qty);
            qtyField.setText("0");
            ToastNotification.showSuccess(frame, product.name + " added to cart!");
        });
        g.gridy = 6;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 12, 12, 12);
        card.add(addBtn, g);

        addHoverBorder(card, Theme.CTA, 2, Theme.BORDER, 1);
        return card;
    }

    public static void showDetail(Product p, JFrame owner) {
        JDialog dlg = new JDialog(owner, p.name, true);
        dlg.setSize(420, 560);
        dlg.setLocationRelativeTo(owner);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.SURFACE);

        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imgPanel.setBackground(Theme.BG);
        imgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
        imgPanel.add(new JLabel(ImageCache.get().icon(p.imagePath, 160, 160)));
        root.add(imgPanel, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Theme.SURFACE);
        info.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel nameL = new JLabel(p.name);
        nameL.setFont(Theme.bold(22f));
        nameL.setForeground(Theme.TEXT);
        nameL.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel starsL = starLabel(displayRating(p));
        starsL.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceL = new JLabel(p.formattedPrice());
        priceL.setFont(Theme.bold(26f));
        priceL.setForeground(Theme.TEXT);
        priceL.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(p.description);
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFont(Theme.body(13f));
        desc.setForeground(Theme.TEXT_2);
        desc.setBorder(BorderFactory.createEmptyBorder());
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        String stockText = p.stock > 5 ? "In Stock (" + p.stock + " available)" : "Only " + p.stock + " left";
        JLabel stockL = new JLabel(stockText);
        stockL.setFont(Theme.bold(12f));
        stockL.setForeground(p.stock > 5 ? Theme.SUCCESS : Theme.DANGER);
        stockL.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField qtyF = styledQtyField("0");
        JButton minBtn = qtyButton(false);
        JButton plusBtn = qtyButton(true);
        RoundedButton addBtn = primaryButton("Add to Cart");
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        minBtn.addActionListener(e -> adjustQty(qtyF, -1));
        plusBtn.addActionListener(e -> adjustQty(qtyF, 1));
        addBtn.addActionListener(e -> {
            int qty = parseQty(qtyF);
            if (qty < 1) {
                ToastNotification.showError(owner, "Quantity must be at least 1");
                return;
            }
            AppState.get().addToCart(p, qty);
            ToastNotification.showSuccess(owner, p.name + " added to cart!");
            dlg.dispose();
        });

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        addRow.setOpaque(false);
        addRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        addRow.add(minBtn);
        addRow.add(qtyF);
        addRow.add(plusBtn);
        addRow.add(addBtn);

        info.add(nameL);
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(starsL);
        info.add(Box.createRigidArea(new Dimension(0, 6)));
        info.add(priceL);
        info.add(Box.createRigidArea(new Dimension(0, 10)));
        info.add(desc);
        info.add(Box.createRigidArea(new Dimension(0, 8)));
        info.add(stockL);
        info.add(Box.createRigidArea(new Dimension(0, 14)));
        info.add(addRow);

        JScrollPane scroll = new JScrollPane(info);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        dlg.add(root);
        dlg.setVisible(true);
    }

    static void addHoverBorder(JPanel card, Color hoverColor, int hoverThick,
                               Color normalColor, int normalThick) {
        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(hoverColor, hoverThick));
            }
            @Override public void mouseExited(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), card);
                if (!card.contains(p)) {
                    card.setBorder(BorderFactory.createLineBorder(normalColor, normalThick));
                }
            }
        };
        applyToAll(card, ma);
    }

    private static void applyToAll(Container c, MouseAdapter ma) {
        c.addMouseListener(ma);
        for (Component child : c.getComponents()) {
            child.addMouseListener(ma);
            if (child instanceof Container) applyToAll((Container) child, ma);
        }
    }

    private static JTextField styledQtyField(String val) {
        JTextField f = new JTextField(val, 2);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setFont(Theme.bold(14f));
        f.setForeground(Theme.TEXT);
        f.setBackground(Theme.SURFACE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_2, 1),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        f.setPreferredSize(new Dimension(46, 30));
        return f;
    }

    private static void adjustQty(JTextField field, int delta) {
        field.setText(String.valueOf(Math.max(0, parseQty(field) + delta)));
    }

    static int parseQty(JTextField field) {
        try {
            return Math.max(0, Integer.parseInt(field.getText().trim()));
        } catch (NumberFormatException e) {
            field.setText("1");
            return 1;
        }
    }

    private static final class HeartIcon implements Icon {
        private final int size;
        private final Color color;
        private final boolean filled;

        private HeartIcon(int size, Color color, boolean filled) {
            this.size = size;
            this.color = color;
            this.filled = filled;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            double s = size;
            GeneralPath heart = new GeneralPath();
            heart.moveTo(s * 0.5, s * 0.9);
            heart.curveTo(s * 0.08, s * 0.65, s * 0.02, s * 0.28, s * 0.28, s * 0.18);
            heart.curveTo(s * 0.42, s * 0.12, s * 0.5, s * 0.24, s * 0.5, s * 0.3);
            heart.curveTo(s * 0.5, s * 0.24, s * 0.58, s * 0.12, s * 0.72, s * 0.18);
            heart.curveTo(s * 0.98, s * 0.28, s * 0.92, s * 0.65, s * 0.5, s * 0.9);
            heart.closePath();

            g2.setColor(color);
            if (filled) {
                g2.fill(heart);
            } else {
                g2.setStroke(new BasicStroke(Math.max(1.6f, size / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(heart);
            }
            g2.dispose();
        }
    }

    private static final class StarIcon implements Icon {
        private final int size;
        private final double fill;

        private StarIcon(int size, double fill) {
            this.size = size;
            this.fill = Math.max(0.0, Math.min(1.0, fill));
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            Shape star = buildStar(size);

            g2.setColor(Theme.BORDER_2);
            g2.fill(star);

            if (fill > 0) {
                Graphics2D fillGraphics = (Graphics2D) g2.create();
                fillGraphics.setClip(new Rectangle2D.Double(0, 0, size * fill, size));
                fillGraphics.setColor(Theme.STAR);
                fillGraphics.fill(star);
                fillGraphics.dispose();
            }

            g2.setColor(Theme.STAR.darker());
            g2.setStroke(new BasicStroke(Math.max(1.0f, size / 14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(star);
            g2.dispose();
        }

        private Shape buildStar(int size) {
            double cx = size / 2.0;
            double cy = size / 2.0;
            double outer = size * 0.46;
            double inner = outer * 0.45;
            GeneralPath path = new GeneralPath();
            for (int i = 0; i < 10; i++) {
                double angle = -Math.PI / 2 + i * Math.PI / 5;
                double radius = (i % 2 == 0) ? outer : inner;
                double px = cx + Math.cos(angle) * radius;
                double py = cy + Math.sin(angle) * radius;
                if (i == 0) path.moveTo(px, py);
                else path.lineTo(px, py);
            }
            path.closePath();
            return path;
        }
    }

    private static final class MathIcon implements Icon {
        private final int size;
        private final Color color;
        private final boolean plus;

        private MathIcon(int size, Color color, boolean plus) {
            this.size = size;
            this.color = color;
            this.plus = plus;
        }

        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(2.0f, size / 5f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            double midX = x + size / 2.0;
            double midY = y + size / 2.0;
            double inset = size * 0.18;
            g2.draw(new Line2D.Double(x + inset, midY, x + size - inset, midY));
            if (plus) {
                g2.draw(new Line2D.Double(midX, y + inset, midX, y + size - inset));
            }
            g2.dispose();
        }
    }
}
