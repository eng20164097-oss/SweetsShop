package model;

/**
 * Represents a sweet product entity in the shop.
 * Stores details such as ID, name, price, and stock quantity.
 */

public class Product {
    private int id;
    private String name;
    private double price;
    private int stockQuantity;
    private String imageName;
    public Product(int id, String name, double price, int stockQuantity, String imageName) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageName = imageName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getImageName() { return imageName; }
    public void setImageName( String imageName) { this.imageName = imageName; }
}
