package model;

public class Chef extends User {
    public Chef(int id, String name, String password) {
        super(id, name, password);
    }

    @Override
    public void performDuty() {
        System.out.println("Chef " + getName() + " is preparing sweets orders.");
    }
}
