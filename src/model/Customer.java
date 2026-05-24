package model;
/**
 * Represents a customer in the sweets shop system.
 * This class extends User and defines browsing activities.
 */

public class Customer extends User {
    public Customer(int id, String name, String password) {
        super(id, name, password);
    }
    /**
     * Defines the primary activity of a customer, which is 
     * browsing the shop's digital menu for sweets.
     */

    @Override
    public void performDuty() {
        System.out.println("Customer " + getName() + " is browsing the sweets menu.");
    }
}
