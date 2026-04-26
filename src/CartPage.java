import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.function.Supplier;

public class CartPage extends JPanel {

    private final JFrame frame;
    private final Supplier<JPanel> backDest;
    private final AppState state = AppState.get();

    private JPanel itemsPanel;
    private JLabel subtotalLbl;
    private JLabel taxLbl;
    private JLabel totalLbl;

    public CartPage(JFrame frame, Supplier<JPanel> backDest) {
        this.frame = frame;
        this.backDest = backDest;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildSummary(), BorderLayout.SOUTH);

        refreshItems();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        header.setPreferredSize(new Dimension(0, 58));

        JButton back = UIUtils.navIconButton("images/back.png", 24);
        back.addActionListener(e -> UIUtils.navigate(frame, backDest.get()));
        header.add(back, BorderLayout.WEST);

        JLabel title = new JLabel("Your Cart", SwingConstants.CENTER);
        title.setFont(Theme.bold(18f));
        title.setForeground(Theme.TEXT_ON_DARK);
        header.add(title, BorderLayout.CENTER);

        return header;
    }

    private JScrollPane buildCenter() {
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Theme.BG);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));

        JScrollPane sc = new JScrollPane(itemsPanel);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(20);
        sc.getViewport().setBackground(Theme.BG);
        return sc;
    }

    private JPanel buildSummary() {
        JPanel south = new JPanel(new BorderLayout(0, 0));
        south.setBackground(Theme.SURFACE);
        south.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));

        JPanel prices = new JPanel(new GridBagLayout());
        prices.setBackground(Theme.SURFACE);

        subtotalLbl = summaryRow(prices, "Subtotal", 0, false);
        taxLbl = summaryRow(prices, "Tax (10%)", 1, false);

        GridBagConstraints div = new GridBagConstraints();
        div.gridx = 0;
        div.gridy = 2;
        div.gridwidth = 2;
        div.fill = GridBagConstraints.HORIZONTAL;
        div.insets = new Insets(6, 0, 6, 0);
        JPanel line = new JPanel();
        line.setBackground(Theme.BORDER);
        line.setPreferredSize(new Dimension(0, 1));
        prices.add(line, div);

        totalLbl = summaryRow(prices, "Order Total", 3, true);

        south.add(prices, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(Theme.SURFACE);

        JButton clearBtn = UIUtils.ghostButton("Clear Cart");
        clearBtn.addActionListener(e -> {
            state.clearCart();
            refreshItems();
        });

        RoundedButton checkoutBtn = UIUtils.darkButton("Checkout ->");
        checkoutBtn.addActionListener(e -> {
            if (state.getCart().isEmpty()) {
                ToastNotification.showError(frame, "Your cart is empty");
                return;
            }
            UIUtils.navigate(frame, new CheckoutPage(frame, backDest));
        });

        btns.add(clearBtn);
        btns.add(checkoutBtn);
        south.add(btns, BorderLayout.EAST);

        return south;
    }

    private JLabel summaryRow(JPanel parent, String label, int row, boolean large) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = row;
        g.insets = new Insets(3, 0, 3, 32);

        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel key = new JLabel(label);
        key.setFont(large ? Theme.bold(16f) : Theme.body(14f));
        key.setForeground(Theme.TEXT_2);
        parent.add(key, g);

        g.gridx = 1;
        g.anchor = GridBagConstraints.EAST;
        JLabel val = new JLabel("$0.00");
        val.setFont(large ? Theme.bold(18f) : Theme.bold(14f));
        val.setForeground(Theme.TEXT);
        parent.add(val, g);

        return val;
    }

    private void refreshItems() {
        itemsPanel.removeAll();
        Map<String, CartItem> cart = state.getCart();

        if (cart.isEmpty()) {
            itemsPanel.add(buildEmptyState());
        } else {
            for (CartItem item : cart.values()) {
                itemsPanel.add(buildRow(item));
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
        updateSummary();
    }

    private void updateSummary() {
        double sub = state.getCartTotal();
        double tax = sub * 0.10;
        subtotalLbl.setText(String.format("$%.2f", sub));
        taxLbl.setText(String.format("$%.2f", tax));
        totalLbl.setText(String.format("$%.2f", sub + tax));
    }

    private JPanel buildEmptyState() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.SURFACE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        p.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(Theme.SURFACE);

        JLabel icon = new JLabel(ImageCache.get().icon("images/empty.png", 80, 80));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("Your cart is empty");
        msg.setFont(Theme.bold(18f));
        msg.setForeground(Theme.TEXT);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Browse categories and add items to get started");
        sub.setFont(Theme.body(13f));
        sub.setForeground(Theme.TEXT_2);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(Box.createRigidArea(new Dimension(0, 20)));
        inner.add(icon);
        inner.add(Box.createRigidArea(new Dimension(0, 14)));
        inner.add(msg);
        inner.add(Box.createRigidArea(new Dimension(0, 6)));
        inner.add(sub);
        inner.add(Box.createRigidArea(new Dimension(0, 20)));

        p.add(inner);
        return p;
    }

    private JPanel buildRow(CartItem item) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(Theme.SURFACE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 8, 0, 8);

        g.gridx = 0;
        g.gridy = 0;
        g.gridheight = 2;
        row.add(new JLabel(ImageCache.get().icon(item.getProduct().imagePath, 68, 68)), g);
        g.gridheight = 1;

        g.gridx = 1;
        g.gridy = 0;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        JLabel name = new JLabel(item.getItemName());
        name.setFont(Theme.bold(15f));
        name.setForeground(Theme.TEXT);
        row.add(name, g);

        g.gridy = 1;
        g.fill = GridBagConstraints.NONE;
        JLabel unit = new JLabel(item.getProduct().formattedPrice() + " each");
        unit.setFont(Theme.body(12f));
        unit.setForeground(Theme.TEXT_2);
        row.add(unit, g);

        g.gridx = 2;
        g.gridy = 0;
        g.gridheight = 2;
        g.weightx = 0;
        g.anchor = GridBagConstraints.CENTER;
        JTextField qtyF = new JTextField(String.valueOf(item.getQuantity()), 2);
        qtyF.setHorizontalAlignment(JTextField.CENTER);
        qtyF.setFont(Theme.bold(14f));
        qtyF.setForeground(Theme.TEXT);
        qtyF.setBackground(Theme.SURFACE);
        qtyF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_2, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        qtyF.setPreferredSize(new Dimension(46, 30));

        JButton minus = UIUtils.qtyButton(false);
        JButton plus = UIUtils.qtyButton(true);

        minus.addActionListener(e -> {
            int qty = UIUtils.parseQty(qtyF) - 1;
            if (qty <= 0) state.removeFromCart(item.getItemName());
            else {
                state.setCartQty(item.getItemName(), qty);
                qtyF.setText(String.valueOf(qty));
            }
            refreshItems();
        });
        plus.addActionListener(e -> {
            int qty = UIUtils.parseQty(qtyF) + 1;
            state.setCartQty(item.getItemName(), qty);
            qtyF.setText(String.valueOf(qty));
            refreshItems();
        });

        JPanel stepper = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        stepper.setOpaque(false);
        stepper.add(minus);
        stepper.add(qtyF);
        stepper.add(plus);
        row.add(stepper, g);

        g.gridx = 3;
        g.anchor = GridBagConstraints.EAST;
        JLabel total = new JLabel(String.format("$%.2f", item.getTotal()));
        total.setFont(Theme.bold(16f));
        total.setForeground(Theme.TEXT);
        row.add(total, g);

        g.gridx = 4;
        JButton remove = new JButton("X");
        remove.setFont(Theme.bold(13f));
        remove.setForeground(Color.decode("#94A3B8"));
        UIUtils.makeTransparent(remove);
        remove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        remove.addActionListener(e -> {
            state.removeFromCart(item.getItemName());
            refreshItems();
        });
        remove.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { remove.setForeground(Theme.DANGER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { remove.setForeground(Color.decode("#94A3B8")); }
        });
        row.add(remove, g);

        return row;
    }
}
