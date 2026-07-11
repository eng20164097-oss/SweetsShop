package db;

import java.sql.*;

/**
 * Layer 4: Infrastructure Layer.
 * Implements the Singleton pattern for the database connection.
 */
public class DatabaseManager {
    private static Connection connection = null;
    private static final String URL = "jdbc:sqlite:sweets_shop.db";

    private DatabaseManager() {} // Private Constructor

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA journal_mode=WAL;"); // لمنع الـ Database Locked
                }
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver not found");
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        // نصوص إنشاء الجداول (كل الجداول التي كانت عندك)
        String userTable = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, role TEXT, password TEXT);";
        String productTable = "CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price REAL, stock INTEGER, image_name TEXT);";
        String salesTable = "CREATE TABLE IF NOT EXISTS sales (id INTEGER PRIMARY KEY AUTOINCREMENT, cashier_name TEXT, total_price REAL, status TEXT DEFAULT 'Pending', sale_date DATETIME DEFAULT CURRENT_TIMESTAMP);";
        String saleItemsTable = "CREATE TABLE IF NOT EXISTS sale_items (sale_id INTEGER, product_name TEXT, quantity INTEGER, subtotal REAL, FOREIGN KEY(sale_id) REFERENCES sales(id));";

        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(productTable);
            stmt.execute(salesTable);
            stmt.execute(saleItemsTable);
            stmt.execute("INSERT OR IGNORE INTO users (id, name, role, password) VALUES (1, 'admin', 'Manager', '123')");
            System.out.println("Database Initialized Successfully (Singleton).");
        } catch (SQLException e) {
            System.out.println("Init Error: " + e.getMessage());
        }
    }
}
