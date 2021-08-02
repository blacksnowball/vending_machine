package vending;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

//A summary of transactions that includes transaction date and time, item sold, amount of money paid, returned change and payment method.

public class Transaction implements java.io.Serializable {
    // TO DO
    private LocalDateTime date;
    private String username;
    private HashMap<Product, Integer> products;
    private Integer moneyPaid;
    HashMap<Integer, Integer> change;
    String paymentMethod;

    public Transaction(LocalDateTime date, String username, HashMap<Product, Integer> products, Integer moneyPaid,
                       HashMap<Integer, Integer> change, String paymentMethod){
        this.date = date;
        this.username = username;
        this.products = products;
        this.moneyPaid = moneyPaid;
        this.change = change;
        this.paymentMethod = paymentMethod;
        
        for (Product p : products.keySet()) {
        	int amount = products.get(p);
        	p.setAmountSold(p.getAmountSold() + amount);
        }
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public LocalDateTime getDate() {
        return date;
    }
    
    public String getUsername() {
        return username;
    }

    public HashMap<Integer, Integer> getChange() {
        return change;
    }

    public Integer getTotalChange() {
        Integer totalChange = 0;
        for(Integer note : change.keySet()) {
            totalChange += note*change.get(note);
        }
        return totalChange;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Integer getMoneyPaid() {
        return moneyPaid;
    }
}

class CancelledTransaction implements java.io.Serializable {
    LocalDateTime date;
    String username;
    String cancelReason;
    
    public CancelledTransaction(LocalDateTime date, String username, String cancelReason){
    	this.date = date;
    	this.username = username;
    	this.cancelReason = cancelReason;
    }
}

