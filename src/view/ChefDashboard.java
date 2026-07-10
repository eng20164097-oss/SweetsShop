package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import db.DatabaseManager;

public class ChefDashboard extends JFrame {
    private JTable ordersTable, detailsTable;
    private DefaultTableModel ordersModel, detailsModel;
    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color LIGHT_PINK = new Color(252, 228, 236);

    public ChefDashboard(String chefName) {
        setTitle("Sweets Shop - Kitchen Area (Chef: " + chefName + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 10, 10)); // تقسيم الشاشة لنصفين
        getContentPane().setBackground(LIGHT_PINK);

        // --- النصف العلوي: قائمة الطلبات الجديدة ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("New Pending Orders"));
        ordersModel = new DefaultTableModel(new String[]{"Order ID", "Date", "Status"}, 0);
        ordersTable = new JTable(ordersModel);
        
        // عند الضغط على طلب، تظهر تفاصيله في الجدول السفلي
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row != -1) loadOrderDetails((int) ordersModel.getValueAt(row, 0));
        });
        
        topPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        
        // أزرار التحكم
        JPanel buttons = new JPanel();
        JButton btnReady = new JButton("Mark as Ready ✅");
        JButton btnRefresh = new JButton("Refresh Orders 🔄");
        btnReady.setBackground(ACCENT_PINK);
        btnReady.setForeground(Color.WHITE);
        JButton btnLogout = new JButton("Logout ↩️");
        btnLogout.setBackground(new Color(189, 189, 189)); // لون رمادي مثل واجهة المدير
        btnLogout.setForeground(Color.BLACK);

        // برمجة فعل زر الخروج
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // إغلاق واجهة الشيف
                new CustomerHome(); // العودة لشاشة الدخول
            }
        });
        
        btnReady.addActionListener(e -> markReady());
        btnRefresh.addActionListener(e -> loadPendingOrders());
        
        buttons.add(btnRefresh);
        buttons.add(btnReady);
        buttons.add(btnLogout);
        topPanel.add(buttons, BorderLayout.SOUTH);

        // --- النصف السفلي: تفاصيل الطلب المختار ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Items to Prepare"));
        detailsModel = new DefaultTableModel(new String[]{"Product", "Quantity"}, 0);
        detailsTable = new JTable(detailsModel);
        bottomPanel.add(new JScrollPane(detailsTable), BorderLayout.CENTER);

        add(topPanel);
        add(bottomPanel);

        loadPendingOrders();
        setVisible(true);
    }

    private void loadPendingOrders() {
        ordersModel.setRowCount(0);
        detailsModel.setRowCount(0);
        try (ResultSet rs = DatabaseManager.getPendingSales()) {
            while (rs != null && rs.next()) {
                ordersModel.addRow(new Object[]{rs.getInt("id"), rs.getString("sale_date"), rs.getString("status")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadOrderDetails(int saleId) {
        detailsModel.setRowCount(0);
        try (ResultSet rs = DatabaseManager.getSaleItems(saleId)) {
            while (rs != null && rs.next()) {
                detailsModel.addRow(new Object[]{rs.getString("product_name"), rs.getInt("quantity")});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void markReady() {
        int row = ordersTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) ordersModel.getValueAt(row, 0);
        try {
            DatabaseManager.updateSaleStatus(id);
            JOptionPane.showMessageDialog(this, "Order #" + id + " is ready for delivery!");
            loadPendingOrders();
        } catch (Exception e) { e.printStackTrace(); }
    }
}

