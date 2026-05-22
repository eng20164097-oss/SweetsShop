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

    private void calculateTotal() {
        totalAmount = 0;
        for (OrderItem item : items) {
            totalAmount += item.getSubTotal();
        }
    }
    // Getters...
}
