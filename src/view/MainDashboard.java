package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Locale;
import model.Product;
import service.ProductService; // استيراد طبقة الخدمة
import service.UserService;    // استيراد طبقة الخدمة

/**
 * Layer 1: Presentation Layer (GUI).
 * لوحة التحكم الرئيسية للمدير - تدعم المعمارية الرباعية واللغة العربية.
 */
public class MainDashboard extends JFrame {
    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color LIGHT_PINK = new Color(252, 228, 236);
    private final Color MODERN_GRAY = new Color(84, 110, 122); 

    // تعريف طبقات الخدمة (Business Logic Layer)
    private ProductService productService = new ProductService();
    private UserService userService = new UserService();
    
    private JTable productTable, staffTable;
    private DefaultTableModel productModel, staffModel;

    public MainDashboard(String role) {
        // إعدادات النافذة المعربة
        setTitle("نظام إدارة متجر الحلويات - [لوحة " + role + "]");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // 1. الشريط العلوي (Header)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel userLabel = new JLabel("المستخدم الحالي: " + role);
        userLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        userLabel.setForeground(MODERN_GRAY);

        JButton btnLogout = new JButton("تسجيل الخروج ↩️");
        btnLogout.setBackground(new Color(189, 189, 189));
        // 1. السطر الذي سيخفي المستطيل الداخلي تماماً
        btnLogout.setFocusPainted(false);

        Font modernFont = new Font(Font.DIALOG, Font.BOLD, 14);
        btnLogout.setFont(modernFont);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "هل أنت متأكد من تسجيل الخروج؟", "تأكيد", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new CustomerHome(); // العودة للواجهة الرئيسية
            }
        });

        headerPanel.add(userLabel, BorderLayout.EAST); // لليمين
        headerPanel.add(btnLogout, BorderLayout.WEST); // لليسار
        add(headerPanel, BorderLayout.NORTH);

        // 2. نظام التبويبات المعرب
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(modernFont);
        tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        tabbedPane.addTab("إدارة المخزون 🍰", createInventoryPanel());
        tabbedPane.addTab("إدارة الموظفين 👥", createStaffPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadProductData();
        loadStaffData();
        setVisible(true);
    }

    // لوحة إدارة المخزون
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_PINK);

        // رؤوس الجداول بالعربي
        String[] columns = {"الرقم", "اسم الصنف", "السعر (دينار)", "الكمية المتوفرة"};
        productModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(productModel);
        productTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomButtons.setOpaque(false);

        JButton btnAdd = createStyledButton("إضافة صنف");
        JButton btnEdit = createStyledButton("تعديل");
        JButton btnDelete = createStyledButton("حذف");
        JButton btnRefresh = createStyledButton("تحديث القائمة");

        btnAdd.addActionListener(e -> addNewProduct());
        btnEdit.addActionListener(e -> editSelectedProduct());
        btnDelete.addActionListener(e -> deleteSelectedProduct());
        btnRefresh.addActionListener(e -> loadProductData());

        bottomButtons.add(btnRefresh);
        bottomButtons.add(btnDelete);
        bottomButtons.add(btnEdit);
        bottomButtons.add(btnAdd);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);
        return panel;
    }

    // لوحة إدارة الموظفين
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_PINK);

        String[] columns = {"رقم الموظف", "الاسم", "الوظيفة"};
        staffModel = new DefaultTableModel(columns, 0);
        staffTable = new JTable(staffModel);
        staffTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomButtons.setOpaque(false);

        JButton btnAddStaff = createStyledButton("إضافة موظف");
        JButton btnEditStaff = createStyledButton("تعديل بيانات");
        JButton btnDeleteStaff = createStyledButton("حذف موظف");
        JButton btnRefreshStaff = createStyledButton("تحديث القائمة");

        btnAddStaff.addActionListener(e -> addNewStaff());
        btnEditStaff.addActionListener(e -> editSelectedStaff());
        btnDeleteStaff.addActionListener(e -> deleteSelectedStaff());
        btnRefreshStaff.addActionListener(e -> loadStaffData());

        bottomButtons.add(btnRefreshStaff);
        bottomButtons.add(btnDeleteStaff);
        bottomButtons.add(btnEditStaff);
        bottomButtons.add(btnAddStaff);

        panel.add(new JScrollPane(staffTable), BorderLayout.CENTER);
        panel.add(bottomButtons, BorderLayout.SOUTH);
        return panel;
    }

    private void addNewProduct() {
        try {
            String name = JOptionPane.showInputDialog(this, "اسم الصنف الجديد:");
            if (name == null || name.isEmpty()) return;

            String priceStr = JOptionPane.showInputDialog(this, "السعر:");
            double price = Double.parseDouble(priceStr);

            String stockStr = JOptionPane.showInputDialog(this, "الكمية:");
            int stock = Integer.parseInt(stockStr);

            String imageName = JOptionPane.showInputDialog(this, "اسم ملف الصورة (مثال: cake.jpg):", "cake.jpg");
            
            // استدعاء طبقة الخدمة (Layer 2)
            productService.saveProduct(name, price, stock, imageName);
            
            loadProductData();
            JOptionPane.showMessageDialog(this, "تم إضافة الصنف بنجاح!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "خطأ في البيانات: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductData() {
        productModel.setRowCount(0);
        // نطلب البيانات من الخدمة وليس القاعدة مباشرة
        ArrayList<Product> products = productService.getAllProducts();
        for (Product p : products) {
            productModel.addRow(new Object[]{
                p.getId(), 
                p.getName(), 
                String.format(Locale.US, "%.2f", p.getPrice()),
                p.getStockQuantity()
            });
        }
    }

    private void loadStaffData() {
        staffModel.setRowCount(0);
        try {
            // نستخدم الخدمة لجلب الموظفين
            ResultSet rs = userService.fetchAllUsers();
            while (rs != null && rs.next()) {
                staffModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("role")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار صنف من الجدول أولاً!");
            return;
        }
        int id = (int) productModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "هل أنت متأكد من حذف هذا الصنف؟", "تأكيد الحذف", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(id);
                loadProductData();
                JOptionPane.showMessageDialog(this, "تم الحذف بنجاح");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "خطأ: " + ex.getMessage()); }
        }
    }

    private void addNewStaff() {
        try {
            String name = JOptionPane.showInputDialog(this, "اسم الموظف:");
            if (name == null || name.isEmpty()) return;

            String[] roles = {"Manager", "Cashier", "Chef"};
            String role = (String) JOptionPane.showInputDialog(this, "اختر الوظيفة:", "الوظيفة", JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]);

            String pass = JOptionPane.showInputDialog(this, "كلمة المرور:");

            userService.createStaff(name, role, pass);
            loadStaffData();
            JOptionPane.showMessageDialog(this, "تم إضافة الموظف بنجاح");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "خطأ: " + ex.getMessage()); }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_PINK);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(140, 35));
        btn.setFocusPainted(false);
        return btn;
    }
    
       // 1. ميثود تعديل بيانات منتج (باستخدام الخدمة)
    private void editSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار صنف لتعديله!");
            return;
        }

        try {
            int id = (int) productModel.getValueAt(row, 0);
            String oldName = (String) productModel.getValueAt(row, 1);
            
            String newName = JOptionPane.showInputDialog(this, "تعديل الاسم:", oldName);
            String newPrice = JOptionPane.showInputDialog(this, "تعديل السعر:", productModel.getValueAt(row, 2));
            String newStock = JOptionPane.showInputDialog(this, "تعديل الكمية:", productModel.getValueAt(row, 3));

            if (newName != null && newPrice != null && newStock != null) {
                // نطلب من طبقة الخدمة تنفيذ التعديل
                productService.updateProduct(id, newName, Double.parseDouble(newPrice), Integer.parseInt(newStock));
                loadProductData();
                JOptionPane.showMessageDialog(this, "تم تحديث البيانات بنجاح");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "خطأ في إدخال البيانات!");
        }
    }

    // 2. ميثود حذف موظف (باستخدام الخدمة)
    private void deleteSelectedStaff() {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار موظف لحذفه!");
            return;
        }

        int id = (int) staffModel.getValueAt(row, 0);
        if (id == 1) { // حماية حساب المدير الأساسي
            JOptionPane.showMessageDialog(this, "لا يمكن حذف حساب المدير الأساسي!", "أمن النظام", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "هل أنت متأكد من حذف هذا الموظف؟", "تأكيد", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // نطلب من طبقة الخدمة الحذف
                userService.removeStaff(id);
                loadStaffData();
                JOptionPane.showMessageDialog(this, "تم حذف الموظف");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "خطأ: " + ex.getMessage());
            }
        }
    }

    // 3. ميثود تعديل بيانات موظف (باستخدام الخدمة)
    private void editSelectedStaff() {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار موظف لتعديل بياناته!");
            return;
        }

        try {
            int id = (int) staffModel.getValueAt(row, 0);
            String oldName = (String) staffModel.getValueAt(row, 1);
            
            String newName = JOptionPane.showInputDialog(this, "تعديل اسم الموظف:", oldName);
            String[] roles = {"Manager", "Cashier", "Chef"};
            String newRole = (String) JOptionPane.showInputDialog(this, "تعديل الوظيفة:", "الوظيفة", JOptionPane.QUESTION_MESSAGE, null, roles, staffModel.getValueAt(row, 2));

            if (newName != null && newRole != null) {
                // ملاحظة: يجب التأكد من وجود ميثود updateStaff في كلاس UserService
                userService.updateStaff(id, newName, newRole);
                loadStaffData();
                JOptionPane.showMessageDialog(this, "تم التحديث بنجاح");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "خطأ أثناء التحديث.");
        }
    }

}

