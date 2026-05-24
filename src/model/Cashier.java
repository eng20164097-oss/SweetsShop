package model;
/**
 * Represents the cashier staff responsible for processing sales.
 * Inherits common attributes from the User class.
 */

public class Cashier extends User {
    public Cashier(int id, String name, String password) {
        super(id, name, password);
    }
    /**
     * Executes the cashier's duty, which involves processing 
     * and managing customer payments.
     */
    
    @Override
    public void performDuty() {
        System.out.println("Cashier " + getName() + " is processing customer payments.");
    }
}
