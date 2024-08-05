import javax.swing.*;

public class OnlineShoppingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Shopping App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            // Create a Cart instance
            Cart cart = new Cart();

            // Initialize and set the HomePage as the initial content
            frame.setContentPane(new HomePage(frame, cart));
            frame.setVisible(true);
        });
    }
}
