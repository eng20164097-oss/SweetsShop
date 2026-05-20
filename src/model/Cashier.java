package model;

public class Cashier extends User {
    public Cashier(int id, String name, String password) {
        super(id, name, password);
    }

    @Override
    public void performDuty() {
        System.out.println("Cashier " + getName() + " is processing customer payments.");
    }
}
