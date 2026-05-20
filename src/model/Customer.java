package model;

public class Customer extends User {
    public Customer(int id, String name, String password) {
        super(id, name, password);
    }

    @Override
    public void performDuty() {
        System.out.println("Customer " + getName() + " is browsing the sweets menu.");
    }
}
