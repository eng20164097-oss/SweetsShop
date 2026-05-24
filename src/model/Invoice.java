/**
 * Handles the calculation and representation of a customer invoice.
 * It processes a list of order items to calculate the total amount.
 */

package model;
import java.util.List;

public class Invoice {
    private int invoiceId;
    private List<OrderItem> items;
    private double totalAmount;

    public Invoice(int invoiceId, List<OrderItem> items) {
        this.invoiceId = invoiceId;
        this.items = items;
        calculateTotal();
    }
    
    /**
     * Iterates through the list of items to calculate the final total price.
     */

    private void calculateTotal() {
        totalAmount = 0;
        for (OrderItem item : items) {
            totalAmount += item.getSubTotal();
        }
    }
    // Getters...
}
