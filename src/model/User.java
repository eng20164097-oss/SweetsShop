package model;

/**
 * Abstract class representing a general user in the sweets shop system.
 * This class serves as the base for Manager, Chef, Cashier, and Customer.
 * This class cannot be instantiated directly and serves as a blueprint 
 * It demonstrates Abstraction and Encapsulation.
 * 
 * @author Jamela Ahmed
 */

public abstract class User {
    private int id;
    private String name;
    private String password;

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
    /**
     * Abstract method to be implemented by subclasses to perform specific duties.
     */

    public abstract void performDuty();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
