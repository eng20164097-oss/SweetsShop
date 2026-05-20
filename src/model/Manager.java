package model;

public class Manager extends User {
    public Manager(int id, String name, String password) {
        super(id, name, password);
    }

    @Override
    public void performDuty() {
        System.out.println("Manager " + getName() + " is reviewing reports and managing stock.");
    }
}
