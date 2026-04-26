import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class RatingDialog {

    public static void show(JFrame owner, java.util.Map<String, CartItem> items) {
        if (items.isEmpty()) return;

        JDialog dlg = new JDialog(owner, "Rate Your Purchase", true);
        dlg.setSize(420, 320);
        dlg.setLocationRelativeTo(owner);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.SURFACE);

        JLabel title = new JLabel("How did you like these items?");
        title.setFont(Theme.bold(18f));
        title.setForeground(Theme.TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Theme.SURFACE);
        center.setBorder(BorderFactory.createEmptyBorder(8, 20, 12, 20));

        java.util.Map<String, Integer> ratings = new java.util.HashMap<>();

        for (CartItem item : items.values()) {
            Product p = item.getProduct();
            JPanel itemPanel = new JPanel(new BorderLayout(8, 0));
            itemPanel.setBackground(Theme.SURFACE);
            itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel name = new JLabel(p.name);
            name.setFont(Theme.bold(13f));
            name.setForeground(Theme.TEXT);

            JPanel stars = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            stars.setOpaque(false);

            for (int i = 1; i <= 5; i++) {
                final int rating = i;
                JButton star = UIUtils.ratingStarButton(false);
                star.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                star.addActionListener(e -> {
                    ratings.put(p.name, rating);
                    updateStars(stars, rating);
                });
                stars.add(star);
            }

            itemPanel.add(name, BorderLayout.WEST);
            itemPanel.add(stars, BorderLayout.EAST);
            center.add(itemPanel);
        }

        root.add(center, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(Theme.SURFACE);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));

        JButton skipBtn = UIUtils.ghostButton("Skip");
        skipBtn.addActionListener(e -> dlg.dispose());

        RoundedButton submitBtn = UIUtils.successButton("Submit Ratings");
        submitBtn.addActionListener(e -> {
            for (java.util.Map.Entry<String, Integer> entry : ratings.entrySet()) {
                AppState.get().rateProduct(entry.getKey(), entry.getValue());
            }
            ToastNotification.show(owner, "Thanks for rating!");
            dlg.dispose();
        });

        btns.add(skipBtn);
        btns.add(submitBtn);
        root.add(btns, BorderLayout.SOUTH);

        dlg.add(root);
        dlg.setVisible(true);
    }

    private static void updateStars(JPanel starsPanel, int rating) {
        java.awt.Component[] comps = starsPanel.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof JButton) {
                UIUtils.applyRatingStar((JButton) comps[i], i < rating);
            }
        }
        starsPanel.repaint();
    }
}
