package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import db.DatabaseManager;

/**
 * A professional and stylish Login Frame with a Sweets Shop theme.
 * This class handles user authentication using a pink and gray color palette
 * to provide a modern and appetizing user experience.
 * 
 * @author Jamela Ahmed
 */
public class LoginFrame extends JFrame {
    
    // UI Components
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    // Design System: Theme Colors
    private final Color LIGHT_PINK = new Color(252, 228, 236); // Background color
    private final Color ACCENT_PINK = new Color(240, 98, 146); // Buttons and headers
    private final Color MODERN_GRAY = new Color(84, 110, 122); // Text color
    private final Color LIGHT_GRAY = new Color(236, 239, 241); // Input fields background

    /**
     * Constructor to initialize the Login UI.
     * Sets up layouts, colors, fonts, and action listeners.
     */
    public LoginFrame() {
        // Window Configuration
        setTitle("Sweet Shop - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Container using BoxLayout for vertical stacking
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(LIGHT_PINK);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // 1. Logo Section (Emoji or Letter)
        JLabel logoLabel = new JLabel("🧁"); 
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60)); 
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Title Section
        JLabel titleLabel = new JLabel("Sweets System");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_PINK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. Form Section (Username & Password)
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(300, 200));

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(MODERN_GRAY);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        userField = new JTextField();
        userField.setBorder(new LineBorder(Color.WHITE, 2, true));
        userField.setBackground(Color.WHITE);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(MODERN_GRAY);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        passField = new JPasswordField();
        passField.setBorder(new LineBorder(Color.WHITE, 2, true));
        passField.setBackground(Color.WHITE);

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        // 4. Action Button Section
        loginButton = new JButton("Sign In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setBackground(ACCENT_PINK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder());

        // Attach Login Logic
        loginButton.addActionListener(e -> handleLogin());

        // Assembly
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(loginButton);
        
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Handles the login logic by verifying user inputs against the database.
     * If successful, it opens the Main Dashboard.
     */
       private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        // التحقق من قاعدة البيانات
        String role = DatabaseManager.authenticateUser(username, password);

        if (role != null) {
            // 1. رسالة ترحيب واحدة فقط وإغلاق نافذة الدخول
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + role);
            this.dispose(); 

            // 2. التوجيه الصحيح بناءً على نوع المستخدم
            if (role.equalsIgnoreCase("Manager")) {
                new MainDashboard(role);
            } 
            else if (role.equalsIgnoreCase("Cashier")) {
                new CashierDashboard(username); 
            } 
            else if (role.equalsIgnoreCase("Chef")) {
                // استدعاء واجهة الشيف
                new ChefDashboard(username); 
            } 
            else {
                // لأي دور غير مبرمج حالياً
                JOptionPane.showMessageDialog(this, "Dashboard for " + role + " is under development.");
            }
        } 
        else {
            // رسالة خطأ في حال كانت البيانات غلط
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

}


