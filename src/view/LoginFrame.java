package view;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login - Sweets Shop System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // لفتح النافذة في منتصف الشاشة

        // تصميم الواجهة (Layout)
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel(" Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel(" Password:"));
        passField = new JPasswordField();
        add(passField);

        loginButton = new JButton("Login");
        add(loginButton);

        setVisible(true); // لإظهار النافذة
    }
}

