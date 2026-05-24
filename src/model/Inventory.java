package model;
import java.util.ArrayList;
import java.util.List;
/**
 * Manages the stock levels of sweets and products.
 * Includes methods for adding products and checking for low stock alerts.
 */
public class Inventory {
    private List<Product> products;

    public Inventory() {
        this.products = new ArrayList<>();
    }

/**
 * Adds a new product to the shop's inventory list.
 * @param p The product object to be added.
 */

    public void addProduct(Product p) {
        products.add(p);
    }

/**
 * Scans the inventory and prints an alert for products 
 * whose stock level is below the minimum threshold (5 units).
 */
    public void checkLowStock() {
        for (Product p : products) {
            if (p.getStockQuantity() < 5) {
                System.out.println("ALERT: " + p.getName() + " is low on stock!");
            }
        }
    }
}
