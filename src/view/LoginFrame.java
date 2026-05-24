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
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            // استدعاء ميثود التحقق من قاعدة البيانات
            String role = db.DatabaseManager.authenticateUser(username, password);

            if (role != null) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + role);
                this.dispose(); // إغلاق نافذة تسجيل الدخول
                // هنا سنفتح واجهة لوحة التحكم لاحقاً
                new MainDashboard(role);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true); // لإظهار النافذة
    }
}

