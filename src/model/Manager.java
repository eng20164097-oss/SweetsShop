package model;
/**
 * Represents the Shop Manager with administrative privileges.
 * Inherits from User and manages staff, inventory, and reports.
 */

public class Manager extends User implements ISortable {

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
       @Override
    public void sortData() {
        System.out.println("Manager is sorting the shop data...");
    }
 
}
