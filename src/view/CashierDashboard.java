package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import model.OrderItem;
import model.Product;
import service.ProductService; // استيراد طبقة الخدمة
import service.SalesService;   // استيراد طبقة الخدمة

/**
 * Layer 1: Presentation Layer (GUI).
 * واجهة الكاشير (نقطة البيع) - تدعم المعمارية الرباعية والتعريب الكامل.
 */
public class CashierDashboard extends JFrame {
    private JTable menuTable, cartTable, readyTable;
    private DefaultTableModel menuModel, cartModel, readyModel;
    private JLabel totalLabel;
    private double grandTotal = 0;
    private ArrayList<OrderItem> cartItems = new ArrayList<>();
    private String cashierName;

    // تعريف الخدمات (Layers Integration)
    private ProductService productService = new ProductService();
    private SalesService salesService = new SalesService();

    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color LIGHT_PINK = new Color(252, 228, 236);

    public CashierDashboard(String name) {
        this.cashierName = name;
        setTitle("نظام مبيعات متجر الحلويات - [الكاشير: " + name + "]");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // ضبط اتجاه النافذة لليمين
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        // 1. الشريط العلوي (Header)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT_PINK);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel title = new JLabel("نقطة البيع الرقمية (POS System)", JLabel.RIGHT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        JButton btnLogout = new JButton("تسجيل الخروج ↩️");
        btnLogout.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            this.dispose();
            new CustomerHome();
        });

        header.add(title, BorderLayout.EAST);
        header.add(btnLogout, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // 2. اللوحة الرئيسية (تقسيم الشاشة)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- أ. قسم المنيو (جهة اليمين في العربي) ---
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_PINK), " قائمة الحلويات المتاحة 🍰 ", 
            TitledBorder.RIGHT, 0, new Font(Font.DIALOG, Font.BOLD, 14), ACCENT_PINK));
        
        menuModel = new DefaultTableModel(new String[]{"الرقم", "اسم الصنف", "السعر", "المخزون"}, 0);
        menuTable = new JTable(menuModel);
        menuTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JButton btnAddToCart = new JButton("إضافة للسلة 🛒");
        btnAddToCart.setBackground(new Color(102, 187, 106)); // أخضر
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        menuPanel.add(btnAddToCart, BorderLayout.SOUTH);

        // --- ب. قسم السلة والطلبات الجاهزة (جهة اليسار) ---
        JPanel rightSidePanel = new JPanel(new BorderLayout(0, 10));
        
        // جدول الطلبات الجاهزة للتسليم
JPanel readyPanel = new JPanel(new BorderLayout());
        readyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), " طلبات جاهزة للتسليم ✅ ", 
            TitledBorder.RIGHT, 0, new Font(Font.DIALOG, Font.BOLD, 14), Color.DARK_GRAY));
        readyPanel.setPreferredSize(new Dimension(0, 200));
        
        readyModel = new DefaultTableModel(new String[]{"رقم الطلب", "وقت الطلب"}, 0);
        readyTable = new JTable(readyModel);
        readyTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JButton btnHandOver = new JButton("تسليم الطلب للزبون 🍰");
        btnHandOver.setBackground(new Color(100, 181, 246)); // أزرق
        btnHandOver.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        
        readyPanel.add(new JScrollPane(readyTable), BorderLayout.CENTER);
        readyPanel.add(btnHandOver, BorderLayout.SOUTH);

        // جدول سلة التسوق الحالية
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_PINK), " سلة الطلب الحالي 🛒 ", 
            TitledBorder.RIGHT, 0, new Font(Font.DIALOG, Font.BOLD, 14), ACCENT_PINK));
        
        cartModel = new DefaultTableModel(new String[]{"الصنف", "الكمية", "المجموع"}, 0);
        cartTable = new JTable(cartModel);
        cartTable.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        totalLabel = new JLabel("الإجمالي: 0.00 دينار", JLabel.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnRemove = new JButton("حذف الصنف ❌");
        btnRemove.setBackground(new Color(229, 115, 115));
        btnRemove.setForeground(Color.WHITE);
        
        JButton btnCheckout = new JButton("إتمام البيع وطباعة الفاتورة 💰");
        btnCheckout.setBackground(ACCENT_PINK);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font(Font.DIALOG, Font.BOLD, 18));

        JPanel cartButtons = new JPanel(new GridLayout(2, 1, 5, 5));
        cartButtons.add(btnRemove);
        cartButtons.add(btnCheckout);

        JPanel cartBottom = new JPanel(new BorderLayout(5, 5));
        cartBottom.add(totalLabel, BorderLayout.NORTH);
        cartBottom.add(cartButtons, BorderLayout.SOUTH);

        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        rightSidePanel.add(readyPanel, BorderLayout.NORTH);
        rightSidePanel.add(cartPanel, BorderLayout.CENTER);

        mainPanel.add(menuPanel); // المنيو يمين
        mainPanel.add(rightSidePanel); // السلة والجاهز يسار
        add(mainPanel, BorderLayout.CENTER);

        // --- تفعيل الأزرار ---
        btnAddToCart.addActionListener(e -> addToCart());
        btnCheckout.addActionListener(e -> processCheckout());
        btnRemove.addActionListener(e -> removeFromCart());
        btnHandOver.addActionListener(e -> markAsDelivered());

        loadMenu();
        setVisible(true);
    }

    private void loadMenu() {
        menuModel.setRowCount(0);
        ArrayList<Product> products = productService.getAllProducts();
        for (Product p : products) {
            menuModel.addRow(new Object[]{
                p.getId(), p.getName(), 
                String.format(Locale.US, "%.2f", p.getPrice()), 
                p.getStockQuantity()
            });
        }
        loadReadyOrders();
    }

    private void loadReadyOrders() {
        readyModel.setRowCount(0);
        ArrayList<Object[]> orders = salesService.getReadyOrders();
        for (Object[] row : orders) {
            readyModel.addRow(row);
        }
    }

    private void addToCart() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "يرجى اختيار صنف من المنيو!");
            return;
        }
        try {
            int id = Integer.parseInt(menuModel.getValueAt(row, 0).toString());
            String name = menuModel.getValueAt(row, 1).toString();
            double price = Double.parseDouble(menuModel.getValueAt(row, 2).toString());
            int stock = Integer.parseInt(menuModel.getValueAt(row, 3).toString());

            String qtyStr = JOptionPane.showInputDialog(this, "أدخل الكمية لـ " + name + ":", "1");
            if (qtyStr == null || qtyStr.isEmpty()) return;
            int qty = Integer.parseInt(qtyStr);

            if (qty <= 0) throw new Exception("الكمية يجب أن تكون أكبر من صفر");
            if (qty > stock) throw new Exception("عذراً، المخزون لا يكفي!");

            double subtotal = price * qty;
            cartItems.add(new OrderItem(id, name, qty, price));
            cartModel.addRow(new Object[]{name, qty, String.format(Locale.US, "%.2f", subtotal)});
            grandTotal += subtotal;
            totalLabel.setText("الإجمالي: " + String.format(Locale.US, "%.2f", grandTotal) + " دينار");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطأ: " + e.getMessage(), "خطأ في الإدخال", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) return;
        OrderItem item = cartItems.get(row);
        grandTotal -= item.getSubTotal();
        totalLabel.setText("الإجمالي: " + String.format(Locale.US, "%.2f", grandTotal) + " دينار");
        cartItems.remove(row);
        cartModel.removeRow(row);
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "هل تريد إتمام عملية البيع؟", "تأكيد", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // استدعاء طبقة الخدمة
                salesService.completeSale(cashierName, grandTotal, cartItems);
                
                // طباعة الفاتورة (File I/O)
                new model.Invoice(cashierName, new ArrayList<>(cartItems), grandTotal).generateReceiptFile();
                
                JOptionPane.showMessageDialog(this, "تمت العملية بنجاح وتم طباعة الفاتورة");
                cartItems.clear();
                cartModel.setRowCount(0);
                grandTotal = 0;
                totalLabel.setText("الإجمالي: 0.00 دينار");
                loadMenu();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "فشلت العملية: " + e.getMessage());
            }
        }
    }

    private void markAsDelivered() {
        int row = readyTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) readyModel.getValueAt(row, 0);
        try {
            salesService.finishDelivery(id);
            JOptionPane.showMessageDialog(this, "تم تسليم الطلب رقم " + id + " وإغلاقه بنجاح.");
            loadReadyOrders();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطأ: " + e.getMessage());
        }
    }
}

