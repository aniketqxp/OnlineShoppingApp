import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CheckoutPage extends JPanel {

    private static final String STEP_DETAILS = "DETAILS";
    private static final String STEP_CONFIRM = "CONFIRM";

    private final JFrame frame;
    private final java.util.function.Supplier<JPanel> backDest;
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private final JPanel confirmCard = new JPanel(new GridBagLayout());

    private JTextField shipName;
    private JTextField shipAddress;
    private JTextField shipCity;
    private JTextField shipZip;
    private JComboBox<String> payMethod;

    private final List<JLabel> shipErr = new ArrayList<>();
    private Map<String, CartItem> confirmedOrder = new LinkedHashMap<>();
    private String orderNumber = "";
    private String shipDate = "";

    public CheckoutPage(JFrame frame, java.util.function.Supplier<JPanel> backDest) {
        this.frame = frame;
        this.backDest = backDest;
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        add(buildHeader(), BorderLayout.NORTH);
        
        JScrollPane scroll = new JScrollPane(buildCards());
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        header.setPreferredSize(new Dimension(0, 58));

        JButton back = UIUtils.navIconButton("images/back.png", 24);
        back.addActionListener(e -> UIUtils.navigate(frame, new CartPage(frame, backDest)));
        header.add(back, BorderLayout.WEST);

        JLabel title = new JLabel("Checkout", SwingConstants.CENTER);
        title.setFont(Theme.bold(18f));
        title.setForeground(Theme.TEXT_ON_DARK);
        header.add(title, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildCards() {
        cardPanel.setBackground(Theme.BG);
        cardPanel.add(buildDetailsStep(), STEP_DETAILS);
        cardPanel.add(buildConfirm(), STEP_CONFIRM);
        cards.show(cardPanel, STEP_DETAILS);
        return cardPanel;
    }

    private JPanel buildDetailsStep() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Theme.BG);

        JPanel shell = new JPanel();
        shell.setLayout(new BoxLayout(shell, BoxLayout.Y_AXIS));
        shell.setBackground(Theme.BG);

        shell.add(buildCheckoutForm());
        shell.add(Box.createRigidArea(new Dimension(0, 16)));
        shell.add(buildOrderSummaryCard());

        wrap.add(shell);
        return wrap;
    }

    private JPanel buildCheckoutForm() {
        JPanel form = sectionCard();
        form.add(sectionTitle("Shipping Details"));
        form.add(gap(16));

        shipName = field(form, "Full Name", "Jane Doe", shipErr);
        shipAddress = field(form, "Street Address", "123 Main Street", shipErr);
        shipCity = field(form, "City", "Springfield", shipErr);
        shipZip = field(form, "ZIP / Postal Code", "12345", shipErr);

        form.add(gap(8));

        JLabel payTitle = new JLabel("Payment Method");
        payTitle.setFont(Theme.bold(13f));
        payTitle.setForeground(Theme.TEXT_2);
        payTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(payTitle);
        form.add(gap(4));

        payMethod = new JComboBox<>(new String[] {
                "Credit Card (Simulated)",
                "Debit Card (Simulated)",
                "Cash on Delivery"
        });
        payMethod.setFont(Theme.body(14f));
        payMethod.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        payMethod.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(payMethod);

        JLabel note = new JLabel("This checkout uses a simple fake payment simulator.");
        note.setFont(Theme.body(12f));
        note.setForeground(Theme.TEXT_2);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(gap(8));
        form.add(note);

        return form;
    }

    private JPanel buildOrderSummaryCard() {
        JPanel card = sectionCard();
        card.add(sectionTitle("Order Summary"));
        card.add(gap(16));

        double subtotal = AppState.get().getCartTotal();
        double tax = subtotal * 0.10;
        double total = subtotal + tax;

        JPanel rows = new JPanel(new GridBagLayout());
        rows.setOpaque(false);
        rows.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryRow(rows, "Items", String.valueOf(AppState.get().getCartItemCount()), 0);
        summaryRow(rows, "Subtotal", String.format("$%.2f", subtotal), 1);
        summaryRow(rows, "Tax", String.format("$%.2f", tax), 2);
        summaryRow(rows, "Total", String.format("$%.2f", total), 3);
        card.add(rows);

        card.add(gap(16));

        RoundedButton payBtn = UIUtils.successButton("Pay Now");
        payBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        payBtn.addActionListener(e -> handlePayNow());
        card.add(payBtn);

        return card;
    }

    private void handlePayNow() {
        try {
            if (AppState.get().getCart().isEmpty()) {
                ToastNotification.showError(frame, "Your cart is empty");
                UIUtils.navigate(frame, new CartPage(frame, backDest));
                return;
            }
            if (!validateShip()) {
                ToastNotification.showError(frame, "Please fix the errors in the shipping form");
                return;
            }

            confirmedOrder = new LinkedHashMap<>(AppState.get().getCart());
            orderNumber = String.format("ORD-%08d", new Random().nextInt(100_000_000));
            shipDate = LocalDate.now().plusDays(3 + new Random().nextInt(4))
                    .format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
            
            AppState.get().markMultiplePurchased(confirmedOrder.keySet());
            AppState.get().clearCart();
            
            rebuildConfirm();
            cards.show(cardPanel, STEP_CONFIRM);
            
            // Scroll to top to see the confirmation
            javax.swing.SwingUtilities.invokeLater(() -> {
                Component parent = getParent();
                while (parent != null && !(parent instanceof JScrollPane)) {
                    parent = parent.getParent();
                }
                if (parent != null) {
                    ((JScrollPane)parent).getVerticalScrollBar().setValue(0);
                }
            });
            
        } catch (Exception ex) {
            ex.printStackTrace();
            ToastNotification.showError(frame, "An error occurred during payment: " + ex.getMessage());
        }
    }

    private JPanel buildConfirm() {
        confirmCard.setBackground(Theme.BG);
        // Leave empty — only populated after a real payment via rebuildConfirm()
        return confirmCard;
    }

    private void rebuildConfirm() {
        confirmCard.removeAll();

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Theme.SURFACE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)));

        // ✅ Tick icon row
        JLabel tick = new JLabel("\u2713", SwingConstants.CENTER);
        tick.setFont(Theme.bold(40f));
        tick.setForeground(Theme.SUCCESS);
        tick.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(tick);
        box.add(gap(8));

        JLabel title = centered("Order Confirmed!", Theme.bold(26f), Theme.TEXT);
        JLabel order = centered("Order #" + orderNumber, Theme.body(14f), Theme.TEXT_2);
        JLabel payment = centered("Payment processed via " + selectedPaymentMethod(),
                Theme.body(13f), Theme.TEXT_2);
        JLabel shipping = centered("Estimated ship date: " + shipDate,
                Theme.body(13f), Theme.SUCCESS);

        box.add(title);
        box.add(gap(6));
        box.add(order);
        box.add(gap(4));
        box.add(payment);
        box.add(gap(4));
        box.add(shipping);
        box.add(gap(20));

        // Divider
        JPanel div = new JPanel();
        div.setBackground(Theme.BORDER);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(div);
        box.add(gap(12));

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        itemsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (CartItem item : confirmedOrder.values()) {
            String itemText = item.getQuantity() + "\u00D7 " + item.getItemName() + "  "
                    + String.format("$%.2f", item.getTotal());
            itemsPanel.add(centered(itemText, Theme.body(13f), Theme.TEXT_2));
            itemsPanel.add(gap(4));
        }

        box.add(itemsPanel);
        box.add(gap(22));

        RoundedButton rateBtn = UIUtils.primaryButton("Rate Purchase");
        rateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rateBtn.addActionListener(e -> RatingDialog.show(frame, confirmedOrder));

        RoundedButton homeBtn = UIUtils.darkButton("Continue Shopping");
        homeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        homeBtn.addActionListener(e -> UIUtils.navigate(frame, new HomePage(frame)));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actions.setOpaque(false);
        actions.add(rateBtn);
        actions.add(homeBtn);
        box.add(actions);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        confirmCard.add(box, gbc);
        confirmCard.revalidate();
        confirmCard.repaint();
    }

    private String selectedPaymentMethod() {
        Object selected = payMethod == null ? null : payMethod.getSelectedItem();
        return selected == null ? "simulator" : selected.toString();
    }

    private static JPanel sectionCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                BorderFactory.createEmptyBorder(24, 30, 24, 30)));
        panel.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));
        return panel;
    }

    private static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.bold(20f));
        label.setForeground(Theme.TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel centered(String text, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private static void summaryRow(JPanel parent, String label, String value, int row) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = row;
        g.insets = new Insets(3, 0, 3, 20);

        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel key = new JLabel(label);
        key.setFont(row == 3 ? Theme.bold(15f) : Theme.body(14f));
        key.setForeground(Theme.TEXT_2);
        parent.add(key, g);

        g.gridx = 1;
        g.weightx = 1.0;
        g.anchor = GridBagConstraints.EAST;
        JLabel val = new JLabel(value);
        val.setFont(row == 3 ? Theme.bold(17f) : Theme.bold(14f));
        val.setForeground(Theme.TEXT);
        parent.add(val, g);
    }

    private static Component gap(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }

    private JTextField field(JPanel parent, String label, String hint, List<JLabel> errs) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.bold(13f));
        lbl.setForeground(Theme.TEXT_2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tf = new JTextField();
        tf.setFont(Theme.body(14f));
        tf.setForeground(Color.decode("#9CA3AF"));
        tf.setText(hint);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_2, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        tf.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (tf.getText().equals(hint)) {
                    tf.setText("");
                    tf.setForeground(Theme.TEXT);
                }
            }

            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (tf.getText().isBlank()) {
                    tf.setText(hint);
                    tf.setForeground(Color.decode("#9CA3AF"));
                }
            }
        });

        JLabel err = new JLabel(" ");
        err.setFont(Theme.body(11f));
        err.setForeground(Theme.DANGER);
        err.setAlignmentX(Component.LEFT_ALIGNMENT);
        errs.add(err);

        parent.add(lbl);
        parent.add(gap(4));
        parent.add(tf);
        parent.add(err);
        parent.add(gap(8));
        return tf;
    }

    private boolean validateShip() {
        boolean ok = true;
        ok &= require(shipName, shipErr.get(0), "Name is required");
        ok &= require(shipAddress, shipErr.get(1), "Address is required");
        ok &= require(shipCity, shipErr.get(2), "City is required");
        ok &= requirePattern(shipZip, shipErr.get(3), "[A-Za-z0-9 -]{4,10}", "Enter a valid ZIP or postal code");
        return ok;
    }

    private boolean require(JTextField field, JLabel err, String msg) {
        boolean blank = field.getForeground().equals(Color.decode("#9CA3AF")) || field.getText().isBlank();
        if (blank) {
            err.setText(msg);
            return false;
        }
        err.setText(" ");
        return true;
    }

    private boolean requirePattern(JTextField field, JLabel err, String pattern, String msg) {
        if (field.getForeground().equals(Color.decode("#9CA3AF")) || field.getText().isBlank()) {
            err.setText("This field is required");
            return false;
        }
        if (!field.getText().trim().matches(pattern)) {
            err.setText(msg);
            return false;
        }
        err.setText(" ");
        return true;
    }
}
