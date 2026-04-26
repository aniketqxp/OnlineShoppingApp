import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;

public class ToastNotification {

    public static void show(JFrame parent, String message) {
        show(parent, message, Theme.HEADER, Theme.TEXT_ON_DARK);
    }

    public static void showSuccess(JFrame parent, String message) {
        show(parent, message, Theme.SUCCESS, Color.WHITE);
    }

    public static void showError(JFrame parent, String message) {
        show(parent, message, Theme.DANGER, Color.WHITE);
    }

    private static void show(JFrame parent, String message, Color bg, Color fg) {
        JDialog toast = new JDialog(parent, false);
        toast.setUndecorated(true);
        toast.setAlwaysOnTop(true);

        // Rounded panel
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JLabel label = new JLabel(message);
        label.setForeground(fg);
        label.setFont(Theme.bold(13f));
        panel.add(label, BorderLayout.CENTER);

        toast.setBackground(new Color(0, 0, 0, 0));
        toast.add(panel);
        toast.pack();

        Rectangle b = parent.getBounds();
        toast.setLocation(b.x + b.width - toast.getWidth() - 20, b.y + 76);

        // Fade out
        float[] alpha = { 1.0f };
        Timer fade = new Timer(40, null);
        fade.addActionListener(e -> {
            alpha[0] -= 0.07f;
            if (alpha[0] <= 0f) { fade.stop(); toast.dispose(); }
            else { try { toast.setOpacity(Math.max(0f, alpha[0])); } catch (Exception ignored) {} }
        });

        Timer show = new Timer(1900, e -> { ((Timer) e.getSource()).stop(); fade.start(); });
        show.setRepeats(false);

        toast.setVisible(true);
        show.start();
    }
}
