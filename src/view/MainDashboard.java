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
    private final Color TEXT_DARK = new Color(55, 71, 79); // لون رمادي غامق جداً للنصوص

    // تعريف طبقات الخدمة (Business Logic Layer)
    private ProductService productService = new ProductService();
    private UserService userService = new UserService();
    
    private JTable productTable, staffTable;
    private DefaultTableModel productModel, staffModel;

    public MainDashboard(String role) {
        // إعدادات النافذة المعربة
        setTitle("نظام إدارة متجر الحلويات - [لوحة " + role + "]");
       // هذا السطر يجعل النافذة تفتح بكامل الشاشة تلقائياً حسب حجم شاشة الكمبيوتر
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 

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
        tabbedPane.addTab("الإحصائيات والتقارير 📊", createReportsPanel());

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
        private JPanel createReportsPanel() {
        // 1. اللوحة الأساسية
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(LIGHT_PINK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 2. جلب البيانات من الـ Service مع معالجة الأخطاء لضمان عدم توقف الواجهة
        service.ReportService reportService = new service.ReportService();
        java.util.Map<String, Object> stats = reportService.getDashboardStats();

        // التأكد من وجود قيم افتراضية في حال فشل جلب البيانات
        String revenue = stats.getOrDefault("revenue", 0.0).toString();
        String staffCount = stats.getOrDefault("staffCount", 0).toString();
        String lowStock = stats.getOrDefault("lowStock", 0).toString();

        // 3. إنشاء منطقة الكروت (العلوي)
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        statsContainer.setOpaque(false);
        // استبدلي الأسطر الحمراء بهذه (لاحظي أضفنا الإيموجي في النهاية)
        statsContainer.add(createStatCard("إجمالي المبيعات", revenue + " د.ل", "💰"));
        statsContainer.add(createStatCard("موظفين نشطين", staffCount, "👥"));
        statsContainer.add(createStatCard("نقص في المخزون", lowStock, "⚠️"));


        // 4. إنشاء منطقة أزرار التقارير (السفلي)
        JPanel buttonsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsContainer.setOpaque(false);
        buttonsContainer.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_PINK), " استخراج التقارير التفصيلية ", 
            2, 0, new Font("Segoe UI", Font.BOLD, 14), ACCENT_PINK));

        JButton btnSalesReport = createStyledButton("تقرير المبيعات");
        JButton btnInventoryReport = createStyledButton("تقرير المخزون");
        JButton btnStaffReport = createStyledButton("تقرير الموظفين");

        // برمجة الأزرار
        btnSalesReport.addActionListener(e -> {
            try {
                String[] columns = {"الرقم", "الكاشير", "المبلغ", "التاريخ"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                java.sql.ResultSet rs = reportService.getSalesReport();
                while (rs != null && rs.next()) {
                    model.addRow(new Object[]{rs.getInt("id"), rs.getString("cashier_name"), rs.getDouble("total_price"), rs.getString("sale_date")});
                }
                showReportDialog("المبيعات", model);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnInventoryReport.addActionListener(e -> {
            String[] columns = {"اسم الصنف", "الكمية", "السعر"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            ArrayList<model.Product> products = productService.getAllProducts();
            for (model.Product p : products) {
                model.addRow(new Object[]{p.getName(), p.getStockQuantity(), p.getPrice()});
            }
            showReportDialog("المخزون", model);
        });

        btnStaffReport.addActionListener(e -> {
            try {
                String[] columns = {"رقم الموظف", "الاسم", "الوظيفة"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);
                java.sql.ResultSet rs = userService.fetchAllUsers();
                while (rs != null && rs.next()) {
                    model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("role")});
                }
                showReportDialog("الموظفين", model);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        buttonsContainer.add(btnStaffReport);
        buttonsContainer.add(btnInventoryReport);
        buttonsContainer.add(btnSalesReport);

        // 5. تجميع كل المناطق في اللوحة الرئيسية 
            
        mainPanel.add(statsContainer, BorderLayout.NORTH);  // الإحصائيات فوق
        mainPanel.add(buttonsContainer, BorderLayout.CENTER); // الأزرار في الوسط
        mainPanel.add(createChartPanel(), BorderLayout.SOUTH); // الرسم البياني يملأ الفراغ بالأسفل

    
        return mainPanel; // إعادة اللوحة كاملة
    }
        
        private JPanel createStatCard(String title, String value, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        
        // إطار ناعم مع مساحة داخلية (Padding)
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // الجزء الأيسر: الأيقونة
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(ACCENT_PINK);
        
        // الجزء الأيمن: النصوص
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title, JLabel.RIGHT);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value, JLabel.RIGHT);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(TEXT_DARK);

        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
        private JPanel createChartPanel() {
        service.ReportService reportService = new service.ReportService();
        java.util.Map<String, Integer> data = reportService.getTopSweetsData();

        JPanel mainChartPanel = new JPanel(new BorderLayout());
        mainChartPanel.setBackground(Color.WHITE);
        mainChartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_PINK), "  المبيعات الأكثر طلباً 🍩 ", 
            2, 0, new Font(Font.DIALOG, Font.BOLD, 14), ACCENT_PINK));

        // لوحة الرسم المخصصة للدائرة
        JPanel donutPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int diameter = Math.min(width, height) - 60;
                int x = (width - diameter) / 2;
                int y = (height - diameter) / 2;

                if (data.isEmpty()) {
                    g2.drawString("لا توجد بيانات مبيعات حالياً", width/2 - 50, height/2);
                    return;
                }

                // حساب إجمالي الكميات لتحديد النسب
                double total = data.values().stream().mapToInt(Integer::intValue).sum();
                double startAngle = 90; // البدء من الأعلى

                // تعريف قائمة ألوان احترافية متناسقة مع الوردي
                Color[] colors = {
                    ACCENT_PINK, 
                    new Color(100, 181, 246), // أزرق سماوي
                    new Color(149, 117, 205), // بنفسجي هادئ
                    new Color(255, 183, 77),  // برتقالي فاتح
                    new Color(77, 182, 172)   // أخضر تيفاني
                };

                int i = 0;
                java.util.List<String> keys = new java.util.ArrayList<>(data.keySet());
                
                for (String key : keys) {
                    double extent = (data.get(key) / total) * 360.0;
                    g2.setColor(colors[i % colors.length]);
                    
                    // رسم جزء من الدائرة
                    g2.fillArc(x, y, diameter, diameter, (int) startAngle, (int) extent);
                    
                    startAngle += extent;
                    i++;
                }

                // --- سر شكل الـ Donut (رسم دائرة بيضاء في المنتصف) ---
                g2.setColor(Color.WHITE);
                int innerDiameter = diameter / 2;
                int ix = (width - innerDiameter) / 2;
                int iy = (height - innerDiameter) / 2;
                g2.fillOval(ix, iy, innerDiameter, innerDiameter);

                // كتابة كلمة "الأكثر طلباً" في منتصف الدائرة
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String centerText = "الأكثر طلباً";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(centerText, (width - fm.stringWidth(centerText)) / 2, (height / 2) + 5);
            }
        };

        // --- إضافة دليل الألوان (Legend) على اليمين ---
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
Color[] colors = {ACCENT_PINK, new Color(100, 181, 246), new Color(149, 117, 205), new Color(255, 183, 77), new Color(77, 182, 172)};
        int i = 0;
        for (String name : data.keySet()) {
            JPanel item = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            item.setOpaque(false);
            JLabel lblName = new JLabel(name + " (" + data.get(name) + ")");
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(15, 15));
            colorBox.setBackground(colors[i % colors.length]);
            
            item.add(lblName);
            item.add(colorBox);
            legendPanel.add(item);
            i++;
        }

        mainChartPanel.add(donutPanel, BorderLayout.CENTER);
        mainChartPanel.add(legendPanel, BorderLayout.EAST);
        mainChartPanel.setPreferredSize(new Dimension(0, 300));

        return mainChartPanel;
    }




       /**
     * ميثود احترافية لفتح نافذة منبثقة تعرض التقرير في جدول منظم
     * @param title عنوان التقرير (مثلاً: المبيعات)
     * @param model البيانات المراد عرضها في الجدول
     */
        private void showReportDialog(String title, javax.swing.table.DefaultTableModel model) {
        // 1. إنشاء النافذة المنبثقة
        JDialog reportDialog = new JDialog(this, "تقرير " + title, true);
        reportDialog.setSize(750, 550); // زدنا الحجم قليلاً ليتناسب مع الأزرار
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setLayout(new BorderLayout(15, 15));

        // 2. إعداد الجدول
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("البيانات التفصيلية"));

        // 3. إنشاء لوحة الأزرار في الأسفل (تجمع الحذف والطباعة والإغلاق)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton btnPrint = new JButton("طباعة التقرير 🖨️");
        btnPrint.setBackground(new Color(102, 187, 106)); // لون أخضر للطباعة
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

        JButton btnClose = new JButton("إغلاق");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // ربط زر الطباعة بالميثود المسؤولة عن صنع الملف
        btnPrint.addActionListener(e -> exportTableToFile(title, model));
        btnClose.addActionListener(e -> reportDialog.dispose());

        actionPanel.add(btnPrint);
        actionPanel.add(btnClose);

        // 4. العنوان العلوي
        JLabel lblHeader = new JLabel("تقرير " + title, JLabel.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(ACCENT_PINK);

        // 5. إضافة المكونات للنافذة
        reportDialog.add(lblHeader, BorderLayout.NORTH);
        reportDialog.add(scrollPane, BorderLayout.CENTER);
        reportDialog.add(actionPanel, BorderLayout.SOUTH);

        reportDialog.setVisible(true);
    }
    private void exportTableToFile(String title, javax.swing.table.DefaultTableModel model) {
        // إنشاء اسم ملف فريد باستخدام الوقت الحالي
        String fileName = "Report_" + title + "_" + System.currentTimeMillis() + ".txt";
        
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.File(fileName))) {
            writer.println("==========================================");
            writer.println("           نظام إدارة متجر الحلويات          ");
            writer.println("           تقرير: " + title);
            writer.println("==========================================");
            writer.println("تاريخ الاستخراج: " + java.time.LocalDateTime.now());
            writer.println("------------------------------------------");

            // طباعة أسماء الأعمدة من اليمين لليسار لتناسب العربي في النوت باد
            for (int i = model.getColumnCount() - 1; i >= 0; i--) {
                writer.print(model.getColumnName(i) + "\t\t");
            }
            writer.println("\n------------------------------------------");

            // طباعة صفوف البيانات
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = model.getColumnCount() - 1; j >= 0; j--) {
                    writer.print(model.getValueAt(i, j).toString() + "\t\t");
                }
                writer.println();
            }

            writer.println("==========================================");
            writer.flush();
            writer.close();

            // فتح الملف تلقائياً ليشاهده المدير (الدكتور)
            java.awt.Desktop.getDesktop().open(new java.io.File(fileName));
            JOptionPane.showMessageDialog(this, "تم استخراج التقرير بنجاح وهو جاهز للطباعة!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "خطأ أثناء محاولة الطباعة: " + e.getMessage());
        }
    }






}

