package vending;

import java.util.ArrayList;
import java.util.List;


public class Customer extends User {
    private List<Transaction> transactions;
    private List<Product> purchasedProducts;
    private CreditCard linkedCard;

    public Customer(String username, String password) {
        super(username, password, Role.CUSTOMER);
        purchasedProducts = new ArrayList<>();
        transactions = new ArrayList<>();
        linkedCard = null;
    }

    public void addProduct(Product p) {
        purchasedProducts.add(p);
    }

    public List<Product> getPurchasedProducts() {
        return purchasedProducts;
    }

    public void addTransaction(Transaction t){
        // not sure if anything needs to be checked in the class itself
        transactions.add(t);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Transaction> getRecent() {
        // Assuming the list is in chronological order (since newest added recently)

        // if there are less than 5 purchased products, it will return all of them
        if(transactions.size() <= 5){
            return transactions;
        }

        // otherwise, it slices the purchased products to only include the last 5 products
        return transactions.subList(transactions.size()-6, transactions.size()-1);
    }
    
    public CreditCard getCard() {
    	return linkedCard;
    }
    
    public void setCard(CreditCard cc) {
    	this.linkedCard = cc;
    }
}

