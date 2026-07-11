package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import model.Product;
import model.OrderItem;
import service.ProductService; // استيراد الطبقة الثانية (Business Logic)
import service.SalesService;   // استيراد الطبقة الثانية (Business Logic)

/**
 * Layer 1: Presentation Layer (GUI).
 * This class handles the Customer UI and interacts only with the Service Layer.
 */
public class CustomerHome extends JFrame {
    private JPanel productsGrid;
    private DefaultListModel<String> cartListModel;
    private JList<String> cartList;
    private JLabel totalLabel;
    private double grandTotal = 0;
    private ArrayList<OrderItem> cartItems = new ArrayList<>();

    // تعريف الخدمات (Layers Integration)
    private ProductService productService = new ProductService();
    private SalesService salesService = new SalesService();

    private final Color PRIMARY_PINK = new Color(240, 98, 146);
    private final Color BG_COLOR = new Color(248, 249, 250);
    private final Color TEXT_DARK = new Color(55, 71, 79);

    public CustomerHome() {
        setTitle("متجر الحلويات الملكي - القائمة الرقمية");
        // هذا السطر يجعل النافذة تفتح بكامل الشاشة تلقائياً حسب حجم شاشة الكمبيوتر
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        // --- 1. القائمة الجانبية (Sidebar) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(300, 750));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(224, 224, 224)));

        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setOpaque(false);
        sidebarContent.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel logoLabel = new JLabel("🧁"); 
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60)); 
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logo = new JLabel("متجر الحلويات");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        logo.setForeground(PRIMARY_PINK);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JScrollPane cartScroll = new JScrollPane(cartList);
        
        // تعريب إطار السلة
        TitledBorder cartBorder = BorderFactory.createTitledBorder(new LineBorder(PRIMARY_PINK), " سـلتي 🛒 ");
        cartBorder.setTitleJustification(TitledBorder.RIGHT); // محاذاة العنوان لليمين
        cartScroll.setBorder(cartBorder);
        
        totalLabel = new JLabel("الإجمالي: 0.00 دينار");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebarContent.add(logoLabel);
        sidebarContent.add(logo);
        sidebarContent.add(Box.createRigidArea(new Dimension(0, 40)));
        sidebarContent.add(cartScroll);
        sidebarContent.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarContent.add(totalLabel);

        // --- أزرار التحكم بالأسفل معربة ---
        JPanel sidebarBottom = new JPanel(new GridLayout(3, 1, 10, 10));
        sidebarBottom.setOpaque(false);
        sidebarBottom.setBorder(new EmptyBorder(0, 20, 30, 20));
        JButton btnConfirm = createStyledButton("إرسال الطلب 🚀", PRIMARY_PINK, Color.WHITE);
        Font modernFont = new Font(Font.DIALOG, Font.BOLD, 14);
        btnConfirm.setFont(modernFont);
        JButton btnRemove = createStyledButton("حذف من السلة 🗑", new Color(229, 115, 115), Color.WHITE);
        btnRemove.setFont(modernFont);
        JButton btnLogin = createStyledButton("دخول الموظفين 🔐", new Color(120, 144, 156), Color.WHITE);
        btnLogin.setFont(modernFont);

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
        JLabel headerMsg = new JLabel("اختر حلوياتك المفضلة ✨");
        headerMsg.setFont(modernFont);
        headerMsg.setHorizontalAlignment(SwingConstants.RIGHT); // محاذاة لليمين
        headerMsg.setBorder(new EmptyBorder(30, 30, 10, 30));
        
        productsGrid = new JPanel(new GridLayout(0, 3, 25, 25));
        productsGrid.setOpaque(false);
        JScrollPane gridScroll = new JScrollPane(productsGrid);
        gridScroll.setBorder(null);

        mainContent.add(headerMsg, BorderLayout.NORTH);
        mainContent.add(gridScroll, BorderLayout.CENTER);

        add(sidebar, BorderLayout.EAST); // نقل القائمة لجهة اليمين ليتناسب مع العربي
        add(mainContent, BorderLayout.CENTER);

        loadProducts();
        setVisible(true);
    }

    /**
     * Logic for loading products using the Service Layer.
     */
    private void loadProducts() {
        productsGrid.removeAll();
        // جلب البيانات من طبقة الخدمة (Layer 2)
        ArrayList<Product> products = productService.getAllProducts();
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
        
        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel priceLabel = new JLabel(String.format(Locale.US, "%.2f دينار", p.getPrice()));
        priceLabel.setForeground(PRIMARY_PINK);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        info.add(nameLabel);
        info.add(priceLabel);

        JButton btnAdd = new JButton("إضافة للسلة +");
        btnAdd.setBackground(BG_COLOR);
        btnAdd.setForeground(PRIMARY_PINK);
        btnAdd.setFocusPainted(false);
        
        btnAdd.addActionListener(e -> {
            String qtyS = JOptionPane.showInputDialog(this, "ما هي الكمية التي تريدها من " + p.getName() + "؟", "1");
            if (qtyS != null) {
                try {
                    int q = Integer.parseInt(qtyS);
                    if (q > 0 && q <= p.getStockQuantity()) {
                        cartItems.add(new OrderItem(p.getId(), p.getName(), q, p.getPrice()));
                        cartListModel.addElement(p.getName() + " (x" + q + ")");
                        grandTotal += (p.getPrice() * q);
                        totalLabel.setText("الإجمالي: " + String.format(Locale.US, "%.2f", grandTotal) + " دينار");
                    } else {
                        JOptionPane.showMessageDialog(this, "عذراً، الكمية غير متوفرة!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "يرجى إدخال رقم صحيح!");
                }
            }
        });

        card.add(imgLabel, BorderLayout.CENTER);
        card.add(info, BorderLayout.SOUTH);
        card.add(btnAdd, BorderLayout.NORTH);
        return card;
    }

    private void removeFromBasket() {
        int index = cartList.getSelectedIndex();
        if (index != -1) {
            OrderItem item = cartItems.get(index);
            grandTotal -= item.getSubTotal();
            totalLabel.setText("الإجمالي: " + String.format(Locale.US, "%.2f", grandTotal) + " دينار");
            cartListModel.remove(index);
            cartItems.remove(index);
        } else {
            JOptionPane.showMessageDialog(this, "يرجى اختيار صنف لحذفه!");
        }
    }

    private void processOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "السلة فارغة!");
            return;
        }
        try {
            // تنفيذ الطلب عبر طبقة الخدمة (Layer 2)
            salesService.completeSale("زبون الموقع", grandTotal, cartItems);
            JOptionPane.showMessageDialog(this, "تم إرسال طلبك بنجاح! 🍰\nنحن نقوم بتحضيره الآن.");
            cartItems.clear(); 
            cartListModel.clear(); 
            grandTotal = 0;
            totalLabel.setText("الإجمالي: 0.00 دينار");
            loadProducts();
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "خطأ: " + e.getMessage()); 
        }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); 
        btn.setForeground(fg);
        btn.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
        btn.setFocusPainted(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
