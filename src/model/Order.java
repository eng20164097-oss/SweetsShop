package model;

import java.util.Date;

/**
 * Represents a customer order in the system.
 */
public class Order {
    private int orderId;
    private String customerName;
    private double totalAmount;
    private String status; // Status can be: Pending, Preparing, Ready, Completed
    private Date orderDate;

    public Order(int orderId, String customerName, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = "Pending"; // Default status
        this.orderDate = new Date(); // Current time
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }

    @Override
    public String toString() {
        return "Order #" + orderId + " for " + customerName + " | Status: " + status;
    }
}
