package vending;

import java.util.stream.Collectors;

public class Seller extends User implements SellingBehaviour {

    public Seller(String username, String password) {
        super(username, password, Role.SELLER);
    }

}
