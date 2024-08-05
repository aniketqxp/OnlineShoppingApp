import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class OnlineShoppingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Online Shopping App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new HomePage(frame));
            frame.setVisible(true);
        });
    }
}