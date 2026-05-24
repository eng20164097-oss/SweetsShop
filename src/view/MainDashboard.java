package view;
import javax.swing.*;
import java.awt.*;
/**
 * The main application window displayed after a successful login.
 * It acts as the central hub for system operations based on the user's role.
 */

public class MainDashboard extends JFrame {
/**
 * Initializes the dashboard window and displays a personalized 
 * welcome message.
 * @param role The role of the logged-in user (e.g., Manager, Cashier).
 */

    public MainDashboard(String role) {
        setTitle("Sweets Shop Dashboard - " + role);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome to the System, Role: " + role, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel);

        setVisible(true);
    }
}
