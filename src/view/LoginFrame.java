package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import service.UserService; // استيراد طبقة الخدمة

/**
 * Layer 1: Presentation Layer (GUI).
 * Handles user authentication UI and communicates with the Service Layer.
 * 
 * @author Jamela Ahmed
 */
public class LoginFrame extends JFrame {
    
    // UI Components
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    
    // ربط طبقة الواجهة بطبقة الخدمة (Layer 1 -> Layer 2)
    private UserService userService = new UserService();

    // Design System: Theme Colors
    private final Color LIGHT_PINK = new Color(252, 228, 236);
    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color MODERN_GRAY = new Color(84, 110, 122);

    /**
     * Constructor to initialize the Arabic Login UI.
     */
    public LoginFrame() {
        // إعدادات النافذة باللغة العربية
        setTitle("نظام إدارة متجر الحلويات - تسجيل الدخول");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(LIGHT_PINK);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // 1. الشعار
        JLabel logoLabel = new JLabel("🧁"); 
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60)); 
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. العنوان معرب
        JLabel titleLabel = new JLabel("تسجيل دخول الموظفين");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_PINK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3. منطقة الحقول معربة
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setOpaque(false);
        formPanel.setMaximumSize(new Dimension(300, 200));

        JLabel userLabel = new JLabel("اسم المستخدم:");
        userLabel.setForeground(MODERN_GRAY);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        userField = new JTextField();
        userField.setBorder(new LineBorder(Color.WHITE, 2, true));
        userField.setHorizontalAlignment(JTextField.RIGHT); // الكتابة من اليمين

        JLabel passLabel = new JLabel("كلمة المرور:");
        passLabel.setForeground(MODERN_GRAY);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        passField = new JPasswordField();
        passField.setBorder(new LineBorder(Color.WHITE, 2, true));
        passField.setHorizontalAlignment(JPasswordField.RIGHT);

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        // 4. زر الدخول معرب
        loginButton = new JButton("دخول");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setBackground(ACCENT_PINK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addActionListener(e -> handleLogin());

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
     * Logic for handling login. 
     * Communicates with the Service layer instead of the Database manager directly.
     */
    private void handleLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        // نستخدم طبقة الخدمة هنا (Service Layer)
        String role = userService.login(username, password);

        if (role != null) {
            JOptionPane.showMessageDialog(this, "تم تسجيل الدخول بنجاح! مرحباً بك يا " + role);
            this.dispose(); 

            // توجيه المستخدم حسب الدور (الدور يأتي من قاعدة البيانات بالإنجليزية غالباً)
            if (role.equalsIgnoreCase("Manager")) {
                new MainDashboard("المدير");
            } 
            else if (role.equalsIgnoreCase("Cashier")) {
                new CashierDashboard(username); 
            } 
            else if (role.equalsIgnoreCase("Chef")) {
                new ChefDashboard(username); 
            }
        } 
        else {
            JOptionPane.showMessageDialog(this, "اسم المستخدم أو كلمة المرور غير صحيحة!", "فشل الدخول", JOptionPane.ERROR_MESSAGE);
        }
    }
}

