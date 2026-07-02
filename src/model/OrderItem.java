package model;
/**
 * Represents an individual item within a customer's order.
 * Links a specific product to the quantity ordered and calculates subtotals.
 */

public class OrderItem {
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;

    public OrderItem(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

/**
 * Calculates the subtotal for this specific item line 
 * based on quantity and unit price.
 * @return The calculated subtotal as a double.
 */
    
    public double getSubTotal() {
        return quantity * unitPrice;
    }
        public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

}
