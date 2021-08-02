package vending;

public class Cashier extends User implements CashingBehaviour {

    public Cashier(String username, String password) {
        super(username, password, Role.CASHIER);
    }
}
