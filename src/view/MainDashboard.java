package view;
import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
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
