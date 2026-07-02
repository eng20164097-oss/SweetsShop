package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 * The central manager for database operations.
 * Handles connectivity, schema initialization, and user security.
 */

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:sweets_shop.db";
    
/**
 * Establishes a connection to the SQLite database file.
 * Uses the JDBC driver to create a communication bridge.
 * @return A Connection object if successful, otherwise null.
 */

    public static Connection connect() {
    Connection conn = null;
    try {
        // سطر إضافي لضمان تحميل المكتبة 
        Class.forName("org.sqlite.JDBC"); 
        
        conn = DriverManager.getConnection(URL);
        System.out.println("Connection to SQLite has been established.");
    } catch (Exception e) { 
        System.out.println("Connection error: " + e.getMessage());
    }
    return conn;
}

/**
 * Sets up the database structure by creating tables for users and products.
 * It also inserts a default test administrator account.
 */

    public static void initializeDatabase() {
        String userTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "role TEXT NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        String productTable = "CREATE TABLE IF NOT EXISTS products ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "price REAL NOT NULL,"
                + "stock INTEGER NOT NULL"
                + ");";
                // 1. جدول المبيعات (رأس الفاتورة)
        String salesTable = "CREATE TABLE IF NOT EXISTS sales ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "cashier_name TEXT,"
                + "total_price REAL,"
                + "status TEXT DEFAULT 'Pending',"
                + "sale_date DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";

// 2. جدول تفاصيل المبيعات (كل صنف في الفاتورة)
        String saleItemsTable = "CREATE TABLE IF NOT EXISTS sale_items ("
                + "sale_id INTEGER,"
                + "product_name TEXT,"
                + "quantity INTEGER,"
                + "subtotal REAL,"
                + "FOREIGN KEY(sale_id) REFERENCES sales(id)"
                + ");";
       
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(productTable);
            // إضافة مستخدم تجريبي (مدير) للاختبار
            stmt.execute("INSERT OR IGNORE INTO users (id, name, role, password) VALUES (1, 'admin', 'Manager', '123')");
            stmt.execute(salesTable);
            stmt.execute(saleItemsTable);
            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Table creation error: " + e.getMessage());
        }
    }
    
    //  ميثود جديدة للتحقق من بيانات الدخول (شرط أساسي للمنطق)
/**
 * Validates user credentials against the database records.
 * Uses PreparedStatement to prevent SQL injection attacks.
 * @param username The username entered in the login form.
 * @param password The password entered in the login form.
 * @return The role of the user (e.g., Manager, Chef) if valid, or null if invalid.
 */

    public static String authenticateUser(String username, String password) {
        String query = "SELECT role FROM users WHERE name = ? AND password = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role"); // يعيد دور المستخدم (Manager, Chef...)
            }
        } catch (SQLException e) {
            System.out.println("Auth Error: " + e.getMessage());
        }
        return null; // إذا لم يجد المستخدم
    }


           /**
     * Fetches all products and returns them as a List of objects.
     * This ensures the connection is CLOSED immediately after reading.
     */
    public static java.util.ArrayList<model.Product> getAllProductsList() {
        java.util.ArrayList<model.Product> list = new java.util.ArrayList<>();
        String query = "SELECT * FROM products";
        
        // استخدام try-with-resources يضمن إغلاق الاتصال تلقائياً (مهم جداً للدرجات)
        try (Connection conn = connect();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                list.add(new model.Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Fetch Error: " + e.getMessage());
        }
        return list; // نرسل القائمة الجاهزة للواجهة والاتصال مغلق الآن بسلام
    }

    /**
     * Inserts a new sweet product into the database.
     * Includes Exception Handling for data safety.
     */
    public static void addProduct(String name, double price, int stock) throws SQLException {
        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try (Connection conn = connect(); 
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.executeUpdate();
            System.out.println("Product added successfully!");
        }
    }
    /**
     * Fetches all users (Staff) from the database.
     */
    public static java.sql.ResultSet getAllUsers() {
        try {
            Connection conn = connect();
            return conn.createStatement().executeQuery("SELECT id, name, role FROM users");
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds a new staff member.
     */
    public static void addUser(String name, String role, String password) throws SQLException {
        String sql = "INSERT INTO users (name, role, password) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
        }
    }
    // 1. حذف منتج
    public static void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // 2. تعديل منتج (تغيير السعر والكمية)
    public static void updateProduct(int id, String name, double price, int stock) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }
        // 1. حذف موظف
    public static void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // 2. تعديل بيانات موظف
    public static void updateUser(int id, String name, String role) throws SQLException {
        String sql = "UPDATE users SET name = ?, role = ? WHERE id = ?";
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, role);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        }
    }
        /**
     * Reduces the stock quantity of a product after a successful sale.
     * This is a core part of the system logic to prevent selling items not in stock.
     * @param productId The ID of the sweet to be updated.
     * @param quantity The number of items sold.
     * @throws SQLException If stock is insufficient or database error occurs.
     */
        public static void reduceStock(int productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        // استخدام try-with-resources يضمن إغلاق الاتصال تلقائياً فور الانتهاء
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insufficient stock for product ID: " + productId);
            }
        }
    }

    /**
     * Saves a complete sale transaction to the database.
     * Demonstrates complex logical integration.
     */
    public static void saveSale(String cashierName, double total, java.util.List<model.OrderItem> items) throws SQLException {
    String saleSql = "INSERT INTO sales (cashier_name, total_price,status) VALUES (?, ?,'Pending')";
    String itemSql = "INSERT INTO sale_items (sale_id, product_name, quantity, subtotal) VALUES (?, ?, ?, ?)";
    String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";

    // اتصال واحد فقط لكل العملية
    try (Connection conn = connect()) {
        conn.setAutoCommit(false); // بدء المعاملة

        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(saleSql, java.sql.Statement.RETURN_GENERATED_KEYS);
             java.sql.PreparedStatement itemStmt = conn.prepareStatement(itemSql);
             java.sql.PreparedStatement stockStmt = conn.prepareStatement(updateStockSql)) {

            // 1. حفظ رأس الفاتورة
            pstmt.setString(1, cashierName);
            pstmt.setDouble(2, total);
            pstmt.executeUpdate();

            java.sql.ResultSet rs = pstmt.getGeneratedKeys();
            int saleId = rs.next() ? rs.getInt(1) : 0;

            // 2. حفظ الأصناف وتحديث المخزن (باستخدام نفس الاتصال)
            for (model.OrderItem item : items) {
                // حفظ تفاصيل الصنف
                itemStmt.setInt(1, saleId);
                itemStmt.setString(2, item.getProductName());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getSubTotal());
                itemStmt.executeUpdate();

                // تحديث المخزن فوراً بنفس الـ PreparedStatement
                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getProductId());
                stockStmt.executeUpdate();
            }

            conn.commit(); // هنا يتم الحفظ النهائي وفتح القفل بسلام
            System.out.println("Sale completed and stock updated!");

        } catch (SQLException e) {
            conn.rollback(); // تراجع إذا حدث خطأ
            throw e;
        }
    }
}

    // 1. جلب الطلبات التي لم تُجهز بعد
    public static ResultSet getPendingSales() {
        try {
            Connection conn = connect();
            return conn.createStatement().executeQuery("SELECT * FROM sales WHERE status = 'Pending'");
        } catch (SQLException e) {
            return null;
        }
    }

    // 2. جلب تفاصيل طلب معين (الأصناف المشتراة)
    public static ResultSet getSaleItems(int saleId) {
        try {
            Connection conn = connect();
            String sql = "SELECT * FROM sale_items WHERE sale_id = ?";
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, saleId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    // 3. تحديث حالة الطلب إلى "Ready"
    public static void updateSaleStatus(int saleId) throws SQLException {
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement("UPDATE sales SET status = 'Ready' WHERE id = ?")) {
            pstmt.setInt(1, saleId);
            pstmt.executeUpdate();
        }
    }

    /**
     * جلب الطلبات التي انتهى الشيف من تحضيرها (Ready)
     */
    public static java.sql.ResultSet getReadySales() {
        try {
            Connection conn = connect();
            return conn.createStatement().executeQuery("SELECT * FROM sales WHERE status = 'Ready'");
        } catch (SQLException e) {
            return null;
        }
    }
        /**
     * جلب الطلبات التي أصبحت جاهزة للتسليم (Ready)
     */
    public static java.util.ArrayList<Object[]> getReadyOrdersList() {
        java.util.ArrayList<Object[]> list = new java.util.ArrayList<>();
        String query = "SELECT id, cashier_name, sale_date FROM sales WHERE status = 'Ready'";
        try (Connection conn = connect();
             java.sql.ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id"), rs.getString("cashier_name"), rs.getString("sale_date")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * تحديث حالة الطلب إلى مكتمل (Completed) عند تسليمه للزبون
     */
    public static void markAsDelivered(int id) throws SQLException {
        try (Connection conn = connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement("UPDATE sales SET status = 'Completed' WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }


}

