package model;

import java.util.Date;

/**
 * Represents a customer's order entity.
 * It stores details like order ID, customer name, total amount, and order status.
 */

public class Order {
    private int orderId;
    private String customerName;
    private double totalAmount;
    private String status; // Status can be: Pending, Preparing, Ready, Completed
    private Date orderDate;

/**
 * Constructor to initialize a new customer order.
 * Sets the default status to "Pending" and records the current date.
 * @param orderId Unique identifier for the order.
 * @param customerName Name of the customer who placed the order.
 * @param totalAmount The total price of the order.
 */

    public Order(int orderId, String customerName, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = "Pending"; // Default status
        this.orderDate = new Date(); // Current time
    }

/**
 * @return The unique ID of the order.
 */

    public int getOrderId() { return orderId; }

/**
 * @param orderId The new ID to set for this order.
 */

    public void setOrderId(int orderId) { this.orderId = orderId; }

/**
 * @return The current status of the order (e.g., Pending, Ready).
 */

    public String getStatus() { return status; }

/**
 * Updates the status of the order.
 * @param status The new status string.
 */

    public void setStatus(String status) { this.status = status; }

/**
 * @return The total monetary value of the order.
 */

    public double getTotalAmount() { return totalAmount; }

/**
 * Returns a string representation of the order for display purposes.
 * @return A formatted string with order ID, customer name, and status.
 */

    @Override
    public String toString() {
        return "Order #" + orderId + " for " + customerName + " | Status: " + status;
    }
}
