package vending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PointOfSaleTest {
    PointOfSale pos;
    HashMap<Product, Integer> cart;
    ProductCategory snack = new ProductCategory("Snack");
    ProductCategory drink = new ProductCategory("Drink");
    Product cheetos = new Product(snack, "Cheetos", 1, 1.20);
    Product coke = new Product(drink, "Coke", 5, 2.0);

    @BeforeEach
    void setUp() {
        pos = new PointOfSale();
        cart = new HashMap<>();
    }

    @Test
    public void testReportContents() {
        pos.reportContents();
    }

    @Test
    public void testAddCashSuccess() {
        Cashier cashier = new Cashier("Michael", "Kirby");
        assertEquals(pos.getCash().get(5), 5);
        assertTrue(cashier.addCash(pos, 5, 1));
        assertEquals(pos.getCash().get(5), 6);
        assertTrue(cashier.addCash(pos, 5, 0));
        assertEquals(pos.getCash().get(5), 6);
    }

    @Test
    public void testAddCashFail() {
        Cashier cashier = new Cashier("Michael", "Kirby");
        assertEquals(pos.getCash().get(5), 5);
        assertFalse(cashier.addCash(pos, 31254, 1));
        assertFalse(cashier.addCash(pos, 5, -11));
        assertEquals(pos.getCash().get(5), 5);
    }

    @Test
    public void testRemoveCashSuccess() {
        Owner owner = new Owner("William", "Gummow", null);
        assertEquals(pos.getCash().get(5), 5);
        assertTrue(owner.removeCash(pos, 5, 1));
        assertEquals(pos.getCash().get(5), 4);
        assertTrue(owner.removeCash(pos, 5, 0));
        assertEquals(pos.getCash().get(5), 4);
    }

    @Test
    public void testRemoveCashFail() {
        Owner owner = new Owner("William", "Gummow", null);
        assertFalse(owner.removeCash(pos, 4213412, 1));
        assertFalse(owner.removeCash(pos, 10, -1));
        assertFalse(owner.removeCash(pos, 5, 7));
    }




    @Test
    void testCalculateCost() {
        // check with no items in cart
        assertEquals(0, pos.calculateCost(cart));

        // add items and check
        cart.put(cheetos, 2);
        cart.put(coke, 1);
        double expected = 120*2 + 200 * 1; // 240 + 200 = 440 cents
        double actual = pos.calculateCost(cart);
        assertEquals(expected, actual);
    }

    @Test
    void testGetChange() {
        cart.put(cheetos, 2);
        cart.put(coke, 1);
        LinkedHashMap<Integer, Integer> change;

        // test if there is not enough change
        change = pos.makeCashPayment(pos.calculateCost(cart), 100000);
        assertNull(change);
        // check the cash in vending machine did not change
        for(Integer bill:pos.getCash().keySet()) {
            assertEquals(pos.getCash().get(bill), 5);
        }

        // test if customer puts enough money
        change = pos.makeCashPayment(pos.calculateCost(cart), 500);

        // since total = 4.4, should return 1 x 0.50 and 1 x 0.10
        System.out.println(change);
        assertEquals(change.size(), 2);
        assertEquals(change.get(50),1);
        assertEquals(change.get(10),1);

        // check that change in machine changed (1 less 50 cent and 1 less 10 cent)
        assertEquals(pos.getCash().get(50), 4);
        assertEquals(pos.getCash().get(10), 4);

    }


    @Test
    void testCreateCashTransaction() {
        Customer c = new Customer("test", "");
        cart.put(cheetos, 2);
        cart.put(coke, 1);
        Transaction t;
        t = pos.createCashTransaction(c, cart, 10000);
        assertEquals(t.getMoneyPaid(), 10000);
        assertEquals(t.getPaymentMethod(), "cash");
        assertEquals(t.getProducts(), cart);
    }

    @Test
    void testCreateCardTransaction() {
        Customer c = new Customer("test", "");
        cart.put(cheetos, 2);
        cart.put(coke, 1);
        Transaction t;
        // test with invalid card details
        t = pos.createCardTransaction(c, cart, "totallyValid", "123");
        assertNull(t);

        // test with valid card details
        t = pos.createCardTransaction(c, cart, "Charles", "40691");
        assertNotNull(t);
        assertEquals(t.getMoneyPaid(), 440);
        assertEquals(t.getPaymentMethod(), "card");
        assertEquals(t.getProducts(), cart);

    }

}
