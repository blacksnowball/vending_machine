package vending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTest {

    Owner owner;
    List<User> users;

    @BeforeEach
    public void setup() {
        users = new ArrayList<>();
        owner = new Owner("foo", "bar", users);
        users.add(owner);
    }

    @Test
    public void testRetrieveUserSuccess() {
        Customer c = new Customer("temp", "test");
        users.add(c);
        assertNotNull(owner.retrieveUser("temp"));
        assertEquals(owner.retrieveUser("temp"), c);
    }

    @Test
    public void testRetrieveUserFail() {
        Customer c = new Customer("temp", "test");
        users.add(c);
        assertNull(owner.retrieveUser("TEMP"));
        assertNull(owner.retrieveUser("quadrilateral"));
    }


    @Test
    public void testAddUserSuccess() {
        assertEquals(users.size(), 1);
        assertTrue(owner.addUser("u2", "", "customer"));
        assertTrue(owner.addUser("u3", "", "seller"));
        assertTrue(owner.addUser("u4", "", "cashier"));
        assertTrue(owner.addUser("u5", "", "owner"));
        assertEquals(users.size(), 5);
        assertEquals(users.get(1).getRole(), Role.CUSTOMER);
        assertEquals(users.get(2).getRole(), Role.SELLER);
        assertEquals(users.get(3).getRole(), Role.CASHIER);
        assertEquals(users.get(4).getRole(), Role.OWNER);
    }

    @Test
    public void testAddUserFail() {
        assertFalse(owner.addUser("foo", "smith", "owner"));
    }

    @Test
    public void testRemoveUserFail() {
        Customer c = new Customer("sam", "sung");
        users.add(c);
        assertEquals(users.size(), 2);
        owner.removeUser("SAM");
        owner.removeUser("SAMMY");
        assertEquals(users.size(), 2);
    }

    @Test
    public void testRemoveUserSuccessful() {
        Customer c = new Customer("sam", "sung");
        users.add(c);
        assertEquals(users.size(), 2);
        owner.removeUser("sam");
        assertEquals(users.size(), 1);
    }

    @Test
    public void testPrintUsersSuccessful() {
        owner.printUsers();
    }

    @Test
    public void testGenerateUserReport() {


        try {
            owner.generateUserReport();
        } catch (Throwable t) {
            throw new Error("This should not happen");
        }
    }

    @Test void testTransactionReport() {
        // owner.transactionReport();
    }

}
