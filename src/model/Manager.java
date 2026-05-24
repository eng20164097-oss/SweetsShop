package model;
/**
 * Represents the Shop Manager with administrative privileges.
 * Inherits from User and manages staff, inventory, and reports.
 */

public class Manager extends User {
    public Manager(int id, String name, String password) {
        super(id, name, password);
    }

/**
 * Implementation of the duty for a Manager, focusing on 
 * reviewing reports and stock management.
 */
    
    @Override
    public void performDuty() {
        System.out.println("Manager " + getName() + " is reviewing reports and managing stock.");
    }
}
