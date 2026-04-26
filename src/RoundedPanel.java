import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class RoundedPanel extends JPanel {

    private final int   radius;
    private final Color fill;
    private final Color stroke;
    private final float strokeWidth;

    RoundedPanel(int radius, Color fill, Color stroke, float strokeWidth) {
        this.radius      = radius;
        this.fill        = fill;
        this.stroke      = stroke;
        this.strokeWidth = strokeWidth;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Border
        if (stroke != null && strokeWidth > 0) {
            g2.setColor(stroke);
            g2.setStroke(new java.awt.BasicStroke(strokeWidth));
            int half = (int) (strokeWidth / 2);
            g2.drawRoundRect(half, half, getWidth() - 2 * half - 1, getHeight() - 2 * half - 1,
                             radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
