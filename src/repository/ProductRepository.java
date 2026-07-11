package repository;

import db.DatabaseManager;
import model.Product;
import java.sql.*;
import java.util.ArrayList;

/**
 * Repository for Product-related database operations.
 */
public class ProductRepository {

    public ArrayList<Product> getAllProductsList() throws SQLException {
        ArrayList<Product> list = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getDouble("price"), rs.getInt("stock"),
                    rs.getString("image_name")
                ));
            }
        }
        return list;
    }

    public void addProduct(String name, double price, int stock, String imageName) throws SQLException {
        String sql = "INSERT INTO products (name, price, stock, image_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setString(4, imageName);
            pstmt.executeUpdate();
        }
    }

    public void deleteProduct(int id) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void updateProduct(int id, String name, double price, int stock) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE products SET name = ?, price = ?, stock = ? WHERE id = ?")) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }
}

