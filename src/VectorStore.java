import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class VectorStore {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("VectorStore");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setMinimumSize(new java.awt.Dimension(800, 600));
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new HomePage(frame));
            frame.setVisible(true);
        });
    }
}
