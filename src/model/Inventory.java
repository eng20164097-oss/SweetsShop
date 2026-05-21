package model;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Product> products;

    public Inventory() {
        this.products = new ArrayList<>();
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void checkLowStock() {
        for (Product p : products) {
            if (p.getStockQuantity() < 5) {
                System.out.println("ALERT: " + p.getName() + " is low on stock!");
            }
        }
    }
}
