package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import service.SalesService; // استيراد طبقة الخدمة (Layer 2)

/**
 * Layer 1: Presentation Layer (GUI).
 * واجهة الشيف المعربة - تدعم المعمارية الرباعية والتحكم في تحضير الطلبات.
 */
public class ChefDashboard extends JFrame {
    private JTable ordersTable, detailsTable;
    private DefaultTableModel ordersModel, detailsModel;
    
    // ربط الواجهة بطبقة الخدمة
    private SalesService salesService = new SalesService();

    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color LIGHT_PINK = new Color(252, 228, 236);

    public ChefDashboard(String chefName) {
        // إعدادات النافذة المعربة
        setTitle("نظام متجر الحلويات - مساحة المطبخ (الشيف: " + chefName + ")");
        // هذا السطر يجعل النافذة تفتح بكامل الشاشة تلقائياً حسب حجم شاشة الكمبيوتر
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 15, 15)); // تقسيم الشاشة لنصفين
        getContentPane().setBackground(LIGHT_PINK);
        
        // ضبط اتجاه النافذة من اليمين لليسار
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // --- 1. النصف العلوي: قائمة الطلبات الجديدة ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_PINK), " الطلبات الجديدة قيد الانتظار 📥 ", 
            2, 0, new Font(Font.DIALOG, Font.BOLD, 14), ACCENT_PINK));

        String[] orderColumns = {"رقم الطلب", "تاريخ الطلب", "الحالة"};
        ordersModel = new DefaultTableModel(orderColumns, 0);
        ordersTable = new JTable(ordersModel);
        ordersTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        // حدث عند اختيار طلب من الجدول
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row != -1) {
                int id = (int) ordersModel.getValueAt(row, 0);
                loadOrderDetails(id);
            }
        });
        
        topPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        
        // منطقة الأزرار
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setOpaque(false);

        JButton btnReady = createStyledButton("تجهيز الطلب ✅", ACCENT_PINK);
        JButton btnRefresh = createStyledButton("تحديث القائمة 🔄", new Color(100, 181, 246));
        JButton btnLogout = createStyledButton("خروج ↩️", new Color(189, 189, 189));

        btnReady.addActionListener(e -> markReady());
        btnRefresh.addActionListener(e -> loadPendingOrders());
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "هل تريد تسجيل الخروج؟", "تأكيد", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new CustomerHome();
            }
        });
        
        buttons.add(btnLogout);
        buttons.add(btnRefresh);
        buttons.add(btnReady);
        topPanel.add(buttons, BorderLayout.SOUTH);

        // --- 2. النصف السفلي: تفاصيل الطلب المختار ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_PINK), " الأصناف المطلوب تحضيرها 🍰 ", 
            2, 0, new Font(Font.DIALOG, Font.BOLD, 14), ACCENT_PINK));

        String[] detailColumns = {"اسم المنتج", "الكمية المطلوبة"};
        detailsModel = new DefaultTableModel(detailColumns, 0);
        detailsTable = new JTable(detailsModel);
        detailsTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        bottomPanel.add(new JScrollPane(detailsTable), BorderLayout.CENTER);

        add(topPanel);
        add(bottomPanel);

        loadPendingOrders();
        setVisible(true);
    }

    private void loadPendingOrders() {
        ordersModel.setRowCount(0);
        detailsModel.setRowCount(0);
        // استخدام الخدمة لجلب البيانات
        ResultSet rs = salesService.getOrdersForChef();
        try {
            while (rs != null && rs.next()) {
                ordersModel.addRow(new Object[]{
                    rs.getInt("id"), 
                    rs.getString("sale_date"), 
                    "قيد التحضير"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadOrderDetails(int saleId) {
        detailsModel.setRowCount(0);
        // استخدام الخدمة لجلب التفاصيل
        ResultSet rs = salesService.getItemsBySaleId(saleId);
        try {
            while (rs != null && rs.next()) {
                detailsModel.addRow(new Object[]{
                    rs.getString("product_name"), 
                    rs.getInt("quantity")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void markReady() {
        int row = ordersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار طلب أولاً!");
            return;
        }
        int id = (int) ordersModel.getValueAt(row, 0);
        try {
            // تنفيذ الأمر عبر طبقة الخدمة
            salesService.markOrderAsReady(id);
            JOptionPane.showMessageDialog(this, "تم تحديث الطلب رقم #" + id + " إلى جاهز بنجاح!");
            loadPendingOrders();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطأ: " + e.getMessage());
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(160, 35));
        btn.setFocusPainted(false);
        return btn;
    }
}

