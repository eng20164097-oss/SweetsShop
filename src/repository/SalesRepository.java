package repository;

import db.DatabaseManager;
import model.OrderItem;
import java.sql.*;
import java.util.ArrayList;

/**
 * Repository for Sales and Order-tracking operations.
 */
public class SalesRepository {

    public void saveSale(String cashierName, double total, java.util.List<OrderItem> items) throws SQLException {
        String saleSql = "INSERT INTO sales (cashier_name, total_price, status) VALUES (?, ?, 'Pending')";
        String itemSql = "INSERT INTO sale_items (sale_id, product_name, quantity, subtotal) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";

        Connection conn = DatabaseManager.getConnection();
        conn.setAutoCommit(false); // بدء المعاملة لضمان سلامة البيانات

        try (PreparedStatement pstmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement itemStmt = conn.prepareStatement(itemSql);
             PreparedStatement stockStmt = conn.prepareStatement(updateStockSql)) {

            pstmt.setString(1, cashierName);
            pstmt.setDouble(2, total);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            int saleId = rs.next() ? rs.getInt(1) : 0;

            for (OrderItem item : items) {
                itemStmt.setInt(1, saleId);
                itemStmt.setString(2, item.getProductName());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getSubTotal());
                itemStmt.executeUpdate();

                stockStmt.setInt(1, item.getQuantity());
                stockStmt.setInt(2, item.getProductId());
                stockStmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public ResultSet getPendingSales() throws SQLException {
        return DatabaseManager.getConnection().createStatement().executeQuery("SELECT * FROM sales WHERE status = 'Pending'");
    }

    public ResultSet getSaleItems(int saleId) throws SQLException {
        String sql = "SELECT * FROM sale_items WHERE sale_id = ?";
        PreparedStatement pstmt = DatabaseManager.getConnection().prepareStatement(sql);
        pstmt.setInt(1, saleId);
        return pstmt.executeQuery();
    }

    public void updateSaleStatus(int saleId, String status) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE sales SET status = ? WHERE id = ?")) {
            pstmt.setString(1, status);
            pstmt.setInt(2, saleId);
            pstmt.executeUpdate();
        }
    }

    public ArrayList<Object[]> getReadyOrdersList() throws SQLException {
        ArrayList<Object[]> list = new ArrayList<>();
        String query = "SELECT id, cashier_name, sale_date FROM sales WHERE status = 'Ready'";
        try (Connection conn = DatabaseManager.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                list.add(new Object[]{rs.getInt("id"), rs.getString("sale_date")});
            }
        }
        return list;
    }
}

