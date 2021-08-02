package vending;

import java.util.*;
import java.time.LocalDateTime;

public class PointOfSale implements java.io.Serializable {
    private LinkedHashMap<Integer, Integer> cash; // in  cents to avoid rounding errors :)
    private Integer eBalance;
    private List<Transaction> transactions;
    private List<CancelledTransaction> cancelledTransactions;

    public PointOfSale() {
        // largest to smallest notes
        // initially 5 as per the specs
        cash = new LinkedHashMap<>();
        cash.put(10000, 5);
        cash.put(5000, 5);
        cash.put(2000, 5);
        cash.put(1000, 5);
        cash.put(500, 5);
        cash.put(200, 5);
        cash.put(100, 5);
        cash.put(50, 5);
        cash.put(20, 5);
        cash.put(10, 5);
        cash.put(5, 5);

        eBalance = 0;
        transactions = new ArrayList<>();
        cancelledTransactions = new ArrayList<>();
    }

    // temporary function to visualise contents of object
    public void reportContents() {
    	List<String> validNotes = new ArrayList<>();
        validNotes.add("$100");
        validNotes.add("$50");
        validNotes.add("$20");
        validNotes.add("$10");
        validNotes.add("$5");
        validNotes.add("$2");
        validNotes.add("$1");
        validNotes.add("50c");
        validNotes.add("20c");
        validNotes.add("10c");
        validNotes.add("5c");
        
        int counter = 0;
        System.out.println("Current available change:");
        for (Integer note : cash.keySet()) {
            Integer qty = cash.get(note);
            System.out.println(validNotes.get(counter) + " x " + qty);
            counter++;
        }
    }

    public LinkedHashMap<Integer, Integer> getCash() {
        return cash;
    }

    // used by cashier and owner
    public boolean addCash(Integer note, Integer amount) {
        if(!cash.containsKey(note)) {
            System.out.println("Invalid coin/note.");
            return false;
        }
        if (amount < 0) {
            System.out.println("A positive amount must be entered.");
            return false;
        }
        cash.put(note, cash.get(note) + amount);
        return true;
    }

    // used by cashier and owner
    public boolean removeCash(Integer note, Integer amount) {
        if (!cash.containsKey(note)) {
            System.out.println("Invalid coin/note.");
            return false;
        }
        if (amount < 0) {
            System.out.println("A positive amount must be entered.");
            return false;
        }
        if (amount > cash.get(note)) {
            System.out.println("There is not this much in the machine to remove.");
            return false;
        }
        cash.put(note, cash.get(note) - amount);
        return true;
    }

    // takes a list of products and calculates the total cost
    public Integer calculateCost(HashMap<Product, Integer> products) {
        Integer totalCost = 0;
        for(Product p : products.keySet()) {
            totalCost += (int) (p.getPrice()*100) * products.get(p);
        }
        return totalCost;
    }

    // returns amount of change (returns null if not enough cash)
    public LinkedHashMap<Integer, Integer> makeCashPayment(Integer totalCost, Integer moneyIn) {
        // need to create a copy if the transaction fails
        LinkedHashMap<Integer, Integer> original = new LinkedHashMap<>();
        original.putAll(cash);

        Integer amountToReturn = moneyIn - totalCost;

        // check if they did not put enough cash
        if(amountToReturn < 0) {
            return null;
        }
        LinkedHashMap<Integer, Integer> change = new LinkedHashMap<>();

        int amount;
        int requiredNoteCount;

        for(Integer note: cash.keySet()) {
            if(note > amountToReturn) {
                continue;
            }

            amount = cash.get(note);
            if(amount > 0) {
                requiredNoteCount = amountToReturn/note;
                if(requiredNoteCount > amount) {
                    // not enough notes in the denomination
                    // take what you can
                    requiredNoteCount = amount;
                    cash.put(note, 0); // since all was used
                } else {
                    // remove required amount
                    cash.put(note, cash.get(note)-requiredNoteCount);
                }

                // adds change to customer's hashmap
                change.put(note, requiredNoteCount);
                amountToReturn = amountToReturn - (note*requiredNoteCount);
            }

        }
        if(amountToReturn > 0) {
            // not enough notes in the vending machine
            cash = original;
            return null;
        }

        return change;

    }

    // create a new transaction (for cash) - returns null if unsuccessful
    public Transaction createCashTransaction(Customer c, HashMap<Product, Integer> products, Integer moneyIn) {
        Integer totalCost = calculateCost(products);

        LocalDateTime today = LocalDateTime.now();
        LinkedHashMap<Integer, Integer> change = makeCashPayment(totalCost, moneyIn);

        if(change == null) {
            // System.out.println("Transaction unsuccessful.");
            return null;
        }

        Transaction trans = new Transaction(today, c.getUsername(), products, moneyIn, change, "cash");

        if (trans != null) {
        	transactions.add(trans);
        }
        return trans;

    }

    public boolean checkCard(String cardName, String cardNumber) {
        CardReader cardReader = new CardReader();

        List<CreditCard> cards = cardReader.getCardList();
        for(CreditCard c: cards) {
            if(c.name.equalsIgnoreCase(cardName)) {
                if(c.number.equals(cardNumber)) {
                    // card has been found
                    return true;
                }
            }
        }

        // card has not been found
        return false;
    }

    // create a new transaction (for card) - returns null if unsuccessful
    public Transaction createCardTransaction(Customer c, HashMap<Product, Integer> products, String cardName, String cardNumber) {
        if(!checkCard(cardName, cardNumber)) {
            return null;
        }

        // credit card is valid, create new transaction and add amount to eBalance
        Integer cost = calculateCost(products);
        eBalance += cost;

        LocalDateTime today = LocalDateTime.now();

        Transaction trans = new Transaction(today, c.getUsername(), products, cost, null, "card");
        if (trans != null) {
        	transactions.add(trans);
        }
        return trans;
    }
    
    public void cancelTransaction(Customer c, String cancelReason) {
    	LocalDateTime today = LocalDateTime.now();
		CancelledTransaction ct = new CancelledTransaction(
				today, c.getUsername(), cancelReason);
		getCancelledTransactions().add(ct);
    }

    public List<Transaction> getTransactions() {
    	return transactions;
    }
    
    public List<CancelledTransaction> getCancelledTransactions() {
    	return cancelledTransactions;
    }

}



