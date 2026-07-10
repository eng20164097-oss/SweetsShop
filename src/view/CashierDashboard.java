package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import db.DatabaseManager;
import model.OrderItem;

public class CashierDashboard extends JFrame {
    private JTable menuTable, cartTable;
    private DefaultTableModel menuModel, cartModel;
    private JLabel totalLabel;
    private double grandTotal = 0;
    private ArrayList<OrderItem> cartItems = new ArrayList<>();
    private String cashierName;
    private JTable readyTable;
    private DefaultTableModel readyModel;


    private final Color ACCENT_PINK = new Color(240, 98, 146);
    private final Color LIGHT_PINK = new Color(252, 228, 236);

    public CashierDashboard(String name) {
        this.cashierName = name;
        setTitle("Sweets Shop POS - Cashier: " + name);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. الشريط العلوي
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT_PINK);
        JLabel title = new JLabel("  Point of Sale (POS System)", JLabel.LEFT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton btnLogout = new JButton("Logout ↩️");
        btnLogout.addActionListener(e -> {
    this.dispose();
    new CustomerHome(); // يعود للواجهة الرئيسية للزبائن
    });

        header.add(title, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. تقسيم الشاشة (المنيو جهة اليسار والسلة جهة اليمين)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));

        // --- قسم المنيو ---
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Sweet Menu"));
        menuModel = new DefaultTableModel(new String[] { "ID", "Name", "Price", "Stock" }, 0);
        menuTable = new JTable(menuModel);
        JButton btnAddToCart = new JButton("Add to Cart 🛒");
        btnAddToCart.setBackground(new Color(102, 187, 106));
        btnAddToCart.setForeground(Color.WHITE);
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        menuPanel.add(btnAddToCart, BorderLayout.SOUTH);

        // --- قسم السلة ---
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("Current Order (Cart)"));
        cartModel = new DefaultTableModel(new String[] { "Name", "Qty", "Subtotal" }, 0);
        cartTable = new JTable(cartModel);
        totalLabel = new JLabel("Total: 0.00 LYD", JLabel.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JButton btnCheckout = new JButton("Complete Sale & Print 🧾");
        btnCheckout.setBackground(ACCENT_PINK);
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 16));
                // --- قسم الطلبات الجاهزة للتسليم ---
        JPanel readyPanel = new JPanel(new BorderLayout());
        readyPanel.setBorder(BorderFactory.createTitledBorder("Orders Ready for Pickup ✅"));
        readyPanel.setPreferredSize(new Dimension(300, 150)); // حجم صغير بالأسفل
        
        readyModel = new DefaultTableModel(new String[]{"ID", "Time"}, 0);
        readyTable = new JTable(readyModel);
        
        JButton btnDelivered = new JButton("Hand Over to Customer 🧁");
        btnDelivered.setBackground(new Color(102, 187, 106)); // لون أخضر للتسليم
        btnDelivered.setForeground(Color.WHITE);
        
        readyPanel.add(new JScrollPane(readyTable), BorderLayout.CENTER);
        readyPanel.add(btnDelivered, BorderLayout.SOUTH);

        // إضافة هذا القسم أسفل لوحة السلة (اليمين)
        cartPanel.add(readyPanel, BorderLayout.NORTH); // سنضعه في الأعلى أو الأسفل حسب تنسيقك


                // إنشاء أزرار التحكم في السلة
        JButton btnRemove = new JButton("Remove Selected ❌");
        btnRemove.setBackground(new Color(229, 115, 115)); // لون أحمر هادئ
        btnRemove.setForeground(Color.WHITE);
        // وضع الأزرار في لوحة منظمة
        JPanel cartButtons = new JPanel(new GridLayout(2, 1, 5, 5));
        cartButtons.setOpaque(false);
        cartButtons.add(btnRemove);
        cartButtons.add(btnCheckout);
        JPanel cartBottom = new JPanel(new BorderLayout());
        cartBottom.add(totalLabel, BorderLayout.NORTH);
        cartBottom.add(cartButtons, BorderLayout.SOUTH);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartBottom, BorderLayout.SOUTH);
        mainPanel.add(menuPanel);
        mainPanel.add(cartPanel);
        add(mainPanel, BorderLayout.CENTER);
        // --- تفعيل الأزرار ---
        btnAddToCart.addActionListener(e -> addToCart());
        btnCheckout.addActionListener(e -> processCheckout());
// تفعيل زر الحذف
        btnRemove.addActionListener(e -> removeFromCart());
        // ابحثي عن زر btnDelivered وأضيفي له هذا السطر
        btnDelivered.addActionListener(e -> markAsDelivered());

        loadMenu();
        setVisible(true);
    }

    /**
     * Loads the sweets menu from the database and displays it in the table.
     * Uses the new List-based approach to avoid database locking.
     */
    private void loadMenu() {
        menuModel.setRowCount(0); // مسح الجدول القديم

        // استدعاء الميثود الجديدة التي تعيد ArrayList من الكائنات
        java.util.ArrayList<model.Product> products = db.DatabaseManager.getAllProductsList();

        if (products != null) {
            for (model.Product p : products) {
                // إضافة البيانات للجدول من كائن المنتج مباشرة
                menuModel.addRow(new Object[] {
                        p.getId(),
                        p.getName(),
                        String.format(java.util.Locale.US, "%.2f", p.getPrice()),
                        p.getStockQuantity()
                });
            }
        }
        loadReadyOrders(); // هذا السطر سيجبر الجدول العلوي على الانتعاش وظهور طلبات الشيف
    }

    private void addToCart() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the menu!");
            return;
        }

        try {
            // قراءة البيانات بطريقة آمنة تمنع خطأ "Invalid quantity"
            int id = Integer.parseInt(menuModel.getValueAt(row, 0).toString());
            String name = menuModel.getValueAt(row, 1).toString();
            double price = Double.parseDouble(menuModel.getValueAt(row, 2).toString());
            int stock = Integer.parseInt(menuModel.getValueAt(row, 3).toString());

            String qtyStr = JOptionPane.showInputDialog(this, "Enter Quantity for " + name + ":", "1");
            if (qtyStr == null || qtyStr.isEmpty())
                return;

            int qty = Integer.parseInt(qtyStr);

            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero!");
                return;
            }

            if (qty > stock) {
                JOptionPane.showMessageDialog(this, "Not enough stock! Available: " + stock, "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // حساب القيم وإضافتها للسلة
            double subtotal = price * qty;
            cartItems.add(new OrderItem(id, name, qty, price));
            cartModel.addRow(new Object[]{
                name, 
                qty, 
                String.format(java.util.Locale.US, "%.2f", subtotal) 
            });

            grandTotal += subtotal;
            totalLabel.setText("Total: " + String.format(java.util.Locale.US, "%.2f", grandTotal) + " LYD");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding to cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

        private void processCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Total: " + grandTotal + " LYD. Process payment?", "Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // استدعاء ميثود الحفظ التي برمجناها في DatabaseManager
                db.DatabaseManager.saveSale(cashierName, grandTotal, cartItems);
                
                JOptionPane.showMessageDialog(this, "Sale Completed! Invoice saved to database.");
                    // إنشاء كائن الفاتورة وطباعته في ملف
                model.Invoice receipt = new model.Invoice(cashierName, new java.util.ArrayList<>(cartItems), grandTotal);
                 receipt.generateReceiptFile();

                
                // تنظيف الواجهة لبدء زبون جديد
                cartItems.clear();
                cartModel.setRowCount(0);
                grandTotal = 0;
                totalLabel.setText("Total: 0.00 LYD");
                loadMenu(); // لتحديث المخزن أمام الكاشير

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Checkout Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

        /**
     * Removes the selected item from the shopping cart and updates the total price.
     */
        private void removeFromCart() {
            int selectedRow = cartTable.getSelectedRow();
        
        // 1. التحقق من الاختيار
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart to remove!");
            return;
        }

        try {
            // 2. الحل الذكي: الحصول على السعر من القائمة البرمجية مباشرة وليس من نص الجدول
            // هذا يتجنب مشكلة الأرقام العربية تماماً
            model.OrderItem itemToRemove = cartItems.get(selectedRow);
            double amountToSubtract = itemToRemove.getSubTotal();

            // 3. تحديث المجموع الكلي
            grandTotal -= amountToSubtract;
            if (grandTotal < 0.01) grandTotal = 0; // تأمين الحسابات
            
            // تحديث النص في الواجهة (نستخدم Locale.US لضمان ظهور الأرقام بالصيغة البرمجية)
            totalLabel.setText("Total: " + String.format(java.util.Locale.US, "%.2f", grandTotal) + " LYD");

            // 4. الحذف الفعلي من القائمة ومن الجدول
            cartItems.remove(selectedRow);
            cartModel.removeRow(selectedRow);

            System.out.println("Item removed successfully.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during removal: " + e.getMessage());
        }
    }

    private void loadReadyOrders() {
        readyModel.setRowCount(0);
        java.util.ArrayList<Object[]> orders = db.DatabaseManager.getReadyOrdersList();
        for (Object[] row : orders) {
            readyModel.addRow(row);
        }
    }

        private void markAsDelivered() {
        int row = readyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to deliver!");
            return;
        }

        int id = (int) readyModel.getValueAt(row, 0);
        try {
            db.DatabaseManager.markAsDelivered(id);
            
            // صياغة المسج الجديد (أكثر احترافية للكاشير)
            JOptionPane.showMessageDialog(this, 
                "Success! Order #" + id + " has been handed over and marked as COMPLETED.", 
                "Delivery Confirmed", 
                JOptionPane.INFORMATION_MESSAGE);
            
            loadReadyOrders(); // تحديث الجدول فوراً
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage());
        }
    }



}
