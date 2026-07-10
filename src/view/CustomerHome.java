package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import db.DatabaseManager;
import model.Product;
import model.OrderItem;
import java.awt.Font;

/**
 * Professional Customer Home with Sidebar and Product Cards.
 */
public class CustomerHome extends JFrame {
    private JPanel productsGrid;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private double grandTotal = 0;
    private ArrayList<OrderItem> cartItems = new ArrayList<>();

    private final Color PRIMARY_PINK = new Color(240, 98, 146);
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Color TEXT_DARK = new Color(55, 71, 79);

    public CustomerHome() {
        setTitle("Sweet Delight - Premium Bakery");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // --- 1. القائمة الجانبية (Sidebar) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(280, 750));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(224, 224, 224)));

        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setOpaque(false);
        sidebarContent.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logo = new JLabel("متجر حلويات");
        logo.setFont(new Font("Serif", Font.BOLD, 32));
        logo.setForeground(PRIMARY_PINK);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        JScrollPane cartScroll = new JScrollPane(cartList);
        cartScroll.setBorder(BorderFactory.createTitledBorder(new LineBorder(PRIMARY_PINK), " سلتي  🛒 "));
        
        totalLabel = new JLabel("Total: 0.00 LYD");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebarContent.add(logo);
        sidebarContent.add(Box.createRigidArea(new Dimension(0, 40)));
        sidebarContent.add(cartScroll);
        sidebarContent.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarContent.add(totalLabel);

        // --- أزرار التحكم بالأسفل (الترتيب الصحيح هنا) ---
        JPanel sidebarBottom = new JPanel(new GridLayout(3, 1, 10, 10));
        sidebarBottom.setOpaque(false);
        sidebarBottom.setBorder(new EmptyBorder(0, 20, 30, 20));

        JButton btnConfirm = createStyledButton("Place Order \uD83D\uDE80", PRIMARY_PINK, Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        JButton btnRemove = createStyledButton("إزالة طلب \uD83D\uDDD1", new Color(229, 115, 115), Color.WHITE);
        btnRemove.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        JButton btnLogin = createStyledButton("Staff Login", new Color(120, 144, 156), Color.WHITE);

        // تفعيل الأكشن
        btnConfirm.addActionListener(e -> processOrder());
        btnRemove.addActionListener(e -> removeFromBasket());
        btnLogin.addActionListener(e -> {
            new LoginFrame();
            this.dispose();
        });

        sidebarBottom.add(btnRemove);
        sidebarBottom.add(btnConfirm);
        sidebarBottom.add(btnLogin);

        sidebar.add(sidebarContent, BorderLayout.CENTER);
        sidebar.add(sidebarBottom, BorderLayout.SOUTH);

        // --- 2. منطقة المنتجات ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        JLabel headerMsg = new JLabel(" أختر حلوك المفضل");
        headerMsg.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerMsg.setBorder(new EmptyBorder(30, 30, 10, 30));
        
        productsGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        productsGrid.setOpaque(false);
        JScrollPane gridScroll = new JScrollPane(productsGrid);
        gridScroll.setBorder(null);

        mainContent.add(headerMsg, BorderLayout.NORTH);
        mainContent.add(gridScroll, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);

        loadProducts();
        setVisible(true);
    }

    private void removeFromBasket() {
        int index = cartList.getSelectedIndex();
        if (index != -1) {
            OrderItem item = cartItems.get(index);
            grandTotal -= item.getSubTotal();
            totalLabel.setText("Total: " + String.format(Locale.US, "%.2f", grandTotal) + " LYD");
            cartListModel.remove(index);
            cartItems.remove(index);
        } else {
            JOptionPane.showMessageDialog(this, "Select an item to remove!");
        }
    }

    private void loadProducts() {
        productsGrid.removeAll();
        ArrayList<Product> products = DatabaseManager.getAllProductsList();
        for (Product p : products) {
            if (p.getStockQuantity() > 0) productsGrid.add(createProductCard(p));
        }
        productsGrid.revalidate();
        productsGrid.repaint();
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(230, 230, 230)), new EmptyBorder(15, 15, 15, 15)));

        JLabel imgLabel = new JLabel();
        try {
            String path = "resources/" + p.getImageName();
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(180, 130, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) { imgLabel.setText("SWEET"); }
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        info.add(new JLabel(p.getName().toUpperCase()));
        JLabel priceLabel = new JLabel(String.format(Locale.US, "%.2f LYD", p.getPrice()));
        priceLabel.setForeground(PRIMARY_PINK);
        info.add(priceLabel);

        JButton btnAdd = new JButton("Add +");
        btnAdd.addActionListener(e -> {
            String qtyS = JOptionPane.showInputDialog(this, "How many?", "1");
            if (qtyS != null) {
                int q = Integer.parseInt(qtyS);
                if (q <= p.getStockQuantity()) {
                    cartItems.add(new OrderItem(p.getId(), p.getName(), q, p.getPrice()));
                    cartListModel.addElement(p.getName() + " x" + q);
                    grandTotal += (p.getPrice() * q);
                    totalLabel.setText("Total: " + String.format(Locale.US, "%.2f", grandTotal) + " LYD");
                }
            }
        });

        card.add(imgLabel, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        card.add(btnAdd, BorderLayout.NORTH);
        return card;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void processOrder() {
        if (cartItems.isEmpty()) return;
        try {
            DatabaseManager.saveSale("Web Customer", grandTotal, cartItems);
            JOptionPane.showMessageDialog(this, "Order Sent!🧁 " );
cartItems.clear(); cartListModel.clear(); grandTotal = 0;
            totalLabel.setText("Total: 0.00 LYD");
            loadProducts();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }
}

