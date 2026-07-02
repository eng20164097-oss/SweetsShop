package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import db.DatabaseManager;

public class MainDashboard extends JFrame {
    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color LIGHT_PINK = new Color(252, 228, 236);
    private final Color MODERN_GRAY = new Color(84, 110, 122); 

    
    // جداول البيانات
    private JTable productTable, staffTable;
    private DefaultTableModel productModel, staffModel;

    public MainDashboard(String role) {
        setTitle("Sweets Shop Management System - [" + role + "]");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // إنشاء نظام التبويبات
        JTabbedPane tabbedPane = new JTabbedPane();
        // غيري هذا السطر في كود MainDashboard
        tabbedPane.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14)); 
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(ACCENT_PINK);

        // إضافة التبويبات
        tabbedPane.addTab("Inventory Management 🧁 🍰", createInventoryPanel());
        tabbedPane.addTab("Staff Management 👥", createStaffPanel());
        // --- إنشاء الشريط العلوي (Top Header Bar) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // نص يوضح من المستخدم الحالي
        JLabel userLabel = new JLabel("User: " + role + " | Sweets Shop Management");
        userLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        userLabel.setForeground(MODERN_GRAY);

        // إنشاء زر تسجيل الخروج
        JButton btnLogout = new JButton("Logout ↩️");
        btnLogout.setBackground(new Color(189, 189, 189)); // لون رمادي هادئ للLogout
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setFocusPainted(false);
        // استخدمي هذا الكود لضمان ظهور السهم
        btnLogout.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14)); // غيرنا الخط هنا
        btnLogout.setText("Logout \u21A9"); // استخدمنا الكود البرمجي للسهم لضمان ظهوره

        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // برمجة فعل زر الخروج
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // إغلاق لوحة التحكم
                new LoginFrame(); // فتح شاشة الدخول مرة أخرى
            }
        });

        headerPanel.add(userLabel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        // إضافة الشريط العلوي في أعلى النافذة
        add(headerPanel, BorderLayout.NORTH);
        // ------------------------------------------

        add(tabbedPane);
        loadProductData();
        loadStaffData();
        setVisible(true);
    }

      // 1. لوحة إدارة المخزون المحدثة
    
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_PINK);

        String[] columns = {"ID", "Name", "Price", "Stock"};
        productModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(productModel);
        
        // --- الخطوة المهمة: ننشئ اللوحة أولاً قبل استدعائها ---
        JPanel bottomButtons = new JPanel(new FlowLayout());
        bottomButtons.setBackground(LIGHT_PINK);

        // --- ثم ننشئ الأزرار ---
        JButton btnAdd = createStyledButton("Add New Sweet");
        JButton btnEdit = createStyledButton("Edit Selected");
        JButton btnDelete = createStyledButton("Delete Selected");
        JButton btnRefresh = createStyledButton("Refresh List");

        // --- ثم نربط الأزرار بالأوامر ---
        btnAdd.addActionListener(e -> addNewProduct());
        btnEdit.addActionListener(e -> editSelectedProduct());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
        btnRefresh.addActionListener(e -> loadProductData());

        // --- الآن نضيف الأزرار للوحة بدون أخطاء ---
        bottomButtons.add(btnAdd);
        bottomButtons.add(btnEdit);
        bottomButtons.add(btnDelete);
        bottomButtons.add(btnRefresh);

        // --- وأخيراً نجمع كل شيء في اللوحة الكبيرة ---
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);
        
        return panel;
    }


    // 2. لوحة إدارة الموظفين المحدثة
        private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_PINK);

        String[] columns = {"ID", "Name", "Role"};
        staffModel = new DefaultTableModel(columns, 0);
        staffTable = new JTable(staffModel);

        JPanel bottomButtons = new JPanel(new FlowLayout());
        bottomButtons.setBackground(LIGHT_PINK);

        JButton btnAddStaff = createStyledButton("Add Staff");
        JButton btnEditStaff = createStyledButton("Edit Staff");
        JButton btnDeleteStaff = createStyledButton("Delete Staff");
        JButton btnRefreshStaff = createStyledButton("Refresh Staff");

        // ربط الأزرار بالأوامر
        btnAddStaff.addActionListener(e -> addNewStaff());
        btnEditStaff.addActionListener(e -> editSelectedStaff());
        btnDeleteStaff.addActionListener(e -> deleteSelectedStaff());
        btnRefreshStaff.addActionListener(e -> loadStaffData());

        bottomButtons.add(btnAddStaff);
        bottomButtons.add(btnEditStaff);
        bottomButtons.add(btnDeleteStaff);
        bottomButtons.add(btnRefreshStaff);

        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);
        return panel;
    }



    // ميثود إضافة منتج (مع Exception Handling)
    private void addNewProduct() {
        try {
            String name = JOptionPane.showInputDialog(this, "Enter Sweet Name:");
            if (name == null || name.
            isEmpty()) return;

            String priceStr = JOptionPane.showInputDialog(this, "Enter Price:");
            double price = Double.parseDouble(priceStr);

            String stockStr = JOptionPane.showInputDialog(this, "Enter Stock:");
            int stock = Integer.parseInt(stockStr);

            DatabaseManager.addProduct(name, price, stock);
            loadProductData();
            JOptionPane.showMessageDialog(this, "Product added!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter numbers for price/stock.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    // ميثود إضافة موظف (مع Exception Handling)
    private void addNewStaff() {
        try {
            String name = JOptionPane.showInputDialog(this, "Enter Staff Name:");
            if (name == null || name.isEmpty()) return;

            String[] roles = {"Manager", "Cashier", "Chef"};
            String role = (String) JOptionPane.showInputDialog(this, "Select Role:", "Role", JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]);

            String pass = JOptionPane.showInputDialog(this, "Set Password:");

            DatabaseManager.addUser(name, role, pass);
            loadStaffData();
            JOptionPane.showMessageDialog(this, "Staff member added!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding staff: " + ex.getMessage());
        }
    }

        private void loadProductData() {
        productModel.setRowCount(0);
        // استدعاء الميثود الجديدة التي تعيد قائمة
        java.util.ArrayList<model.Product> products = db.DatabaseManager.getAllProductsList();
        
        for (model.Product p : products) {
            productModel.addRow(new Object[]{
                p.getId(), 
                p.getName(), 
                String.format(java.util.Locale.US, "%.2f", p.getPrice()),
                p.getStockQuantity()
            });
        }
    }

    private void loadStaffData() {
        staffModel.setRowCount(0);
        try (ResultSet rs = DatabaseManager.getAllUsers()) {
            while (rs != null && rs.next()) {
                staffModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("role")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_PINK);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(200, 40));
        return btn;
    }
        // 1. ميثود حذف الصنف المختار من الجدول
    private void deleteSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table first!");
            return;
        }

        int id = (int) productModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this sweet?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.DatabaseManager.deleteProduct(id); // تأكدي أنكِ أضفتِ هذه الميثود في DatabaseManager
                loadProductData(); // تحديث الجدول فوراً
                JOptionPane.showMessageDialog(this, "Deleted successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // 2. ميثود تعديل الصنف المختار
    private void editSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit!");
            return;
        }

        try {
            int id = (int) productModel.getValueAt(selectedRow, 0);
            String oldName = (String) productModel.getValueAt(selectedRow, 1);
            
            String newName = JOptionPane.showInputDialog(this, "Update Name:", oldName);
            String newPrice = JOptionPane.showInputDialog(this, "Update Price:", productModel.getValueAt(selectedRow, 2));
            String newStock = JOptionPane.showInputDialog(this, "Update Stock:", productModel.getValueAt(selectedRow, 3));

            if (newName != null && newPrice != null && newStock != null) {
                db.DatabaseManager.updateProduct(id, newName, Double.parseDouble(newPrice), Integer.parseInt(newStock));
                loadProductData();
                JOptionPane.showMessageDialog(this, "Updated successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid data. Please check your inputs.");
        }
    }
        private void deleteSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete!");
            return;
        }

        int id = (int) staffModel.getValueAt(selectedRow, 0);
        
        // منع حذف المدير الأساسي (أمان إضافي)
        if (id == 1) {
            JOptionPane.showMessageDialog(this, "Admin account cannot be deleted!", "Security", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.DatabaseManager.deleteUser(id);
                loadStaffData();
                JOptionPane.showMessageDialog(this, "Staff deleted!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void editSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to edit!");
            return;
        }

        try {
            int id = (int) staffModel.getValueAt(selectedRow, 0);
            String oldName = (String) staffModel.getValueAt(selectedRow, 1);
            
            String newName = JOptionPane.showInputDialog(this, "Update Staff Name:", oldName);
            String[] roles = {"Manager", "Cashier", "Chef"};
            String newRole = (String) JOptionPane.showInputDialog(this, "Update Role:", "Role", JOptionPane.QUESTION_MESSAGE, null, roles, staffModel.getValueAt(selectedRow, 2));

            if (newName != null && newRole != null) {
                db.DatabaseManager.updateUser(id, newName, newRole);
                loadStaffData();
                JOptionPane.showMessageDialog(this, "Staff updated successfully!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating staff.");
        }
    }

}
