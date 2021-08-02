package vending;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;

public class CustomerTest {

    Customer customer;
    HashMap<Product, Integer> cart;
    ProductCategory snack = new ProductCategory("Snack");
    ProductCategory drink = new ProductCategory("Drink");
    Product coke = new Product(drink, "Coke", 12, 3.00);
    Product sprite = new Product(drink,"Sprite", 13, 2.00);
    Product cheetos = new Product(snack,"Cheetos",2,4.00);
    Product cadbury = new Product(snack,"Cadbury",4,3.00);

    @BeforeEach
    void setup() {
        customer = new Customer("tester", "1234");
        cart = new HashMap<>();
        cart.put(coke, 1);
        cart.put(sprite, 1);
        cart.put(cheetos, 1);
        cart.put(cadbury, 1);

        Transaction trans = new Transaction(LocalDateTime.now(), customer.getUsername(), cart, 20, null, "card");
        customer.addTransaction(trans);

    }

    @Test
    void testGetRecent() {
        // verify all items returned if less than 5 purchsaed
        Set<Product> products = customer.getRecent().get(0).getProducts().keySet();
        assertTrue(products.contains(coke));
        assertTrue(products.contains(sprite));
        assertTrue(products.contains(cheetos));
        assertTrue(products.contains(cadbury));


        Product freddo = new Product(snack,"Freddo",6,3.00);
        Product pepsi = new Product(drink,"Pepsi",7,3.00);
//
//        cart.put(freddo, 1);
//        cart.put(pepsi, 1);
//
//        assertEquals(6, customer.getRecent().size());

        // verify certain selection if more than 5 purchased


    }

    @Test
    public void testTransactionUpdates() {

        assertEquals(1, customer.getTransactions().size());
        assertEquals(1, customer.getRecent().size());


        Transaction trans1 = new Transaction(LocalDateTime.now(), customer.getUsername(), new HashMap<Product, Integer>(), 0, null, "card");
        Transaction trans2 = new Transaction(LocalDateTime.now(), customer.getUsername(), new HashMap<Product, Integer>(), 0, null, "card");
        Transaction trans3 = new Transaction(LocalDateTime.now(), customer.getUsername(), new HashMap<Product, Integer>(), 0, null, "card");
        Transaction trans4 = new Transaction(LocalDateTime.now(), customer.getUsername(), new HashMap<Product, Integer>(), 0, null, "card");
        Transaction trans5 = new Transaction(LocalDateTime.now(), customer.getUsername(), new HashMap<Product, Integer>(), 0, null, "card");

        customer.addTransaction(trans1);
        customer.addTransaction(trans2);
        customer.addTransaction(trans3);
        customer.addTransaction(trans4);
        customer.addTransaction(trans5);


        assertEquals(6, customer.getTransactions().size());
        assertEquals(5, customer.getRecent().size());


        List<Transaction> transactions = customer.getRecent();
        System.out.println("SIZE OF REFCENT: " + transactions.size());


//        assertEquals(transactions.get(0), trans1);
//        assertEquals(transactions.get(1), trans2);
//        assertEquals(transactions.get(2), trans3);
//        assertEquals(transactions.get(3), trans4);
//        assertEquals(transactions.get(4), trans5);

    }

    @Test
    public void purchaseProducts() {
        assertEquals(0, customer.getPurchasedProducts().size());
        customer.addProduct(cadbury);
        assertEquals(1, customer.getPurchasedProducts().size());
    }

    @Test
    void testUpdateCard() {
        assertNull(customer.getCard());
        CreditCard testCard = new CreditCard("tester", "0000");
        customer.setCard(testCard);
        assertEquals(testCard, customer.getCard());
    }


}
