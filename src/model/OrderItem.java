package model;

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

    public double getSubTotal() {
        return quantity * unitPrice;
    }
    // Getters...
}
