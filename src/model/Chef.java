package model;
/**
 * Represents the Chef/Baker who manages the production of sweets.
 * Inherits from the User class and implements specific duties.
 */

public class Chef extends User {
    public Chef(int id, String name, String password) {
        super(id, name, password);
    }
    /**
     * Executes the specific duty of the chef, which is preparing orders.
     */

    @Override
    public void performDuty() {
        System.out.println("Chef " + getName() + " is preparing sweets orders.");
    }
}
