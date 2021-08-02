package vending;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardReaderTest {

    private CardReader cr;

    @BeforeEach
    public void setup() {
        cr = new CardReader();
    }

    @Test
    public void testCreditCardCreate() {
        CreditCard c = new CreditCard("Joe", "02020");
        assertNotNull(c);
        // assertTrue(c.toString().equals("name: Joe, number: 02020"));
    }

    @Test
    public void testCreditCardLoadList() {
        assertEquals(50, cr.getCardList().size());
    }

    @Test
    public void testCreditCardAddToList() {
        assertEquals(50, cr.getCardList().size());
        cr.addCardToList("Kamala", "02024");
        assertEquals(51, cr.getCardList().size());
        cr.removeCardFromList("02024");
    }

    @Test
    public void testCreditCardRemoveFromList() {
        assertEquals(50, cr.getCardList().size());
        cr.removeCardFromList("40691");
        assertEquals(49, cr.getCardList().size());
        cr.addCardToList("Charles", "40691");
    }

    @Test
    public void testCreditCardRemoveFromListFail() {
        assertFalse(cr.removeCardFromList("0#@$"));
    }

    @Test
    public void testCardListContain() {
        assertTrue(cr.cardListContains("40691"));
        assertTrue(cr.cardListContains("72238"));
    }

    @Test
    public void testListCards() {
        Assertions.assertDoesNotThrow(() -> cr.listCards());
    }

    @Test
    public void testEmptyCardList() {
        cr.getCardList().clear();
        assertEquals(0, cr.getCardList().size());
        assertFalse(cr.removeCardFromList("this should nto be found"));
    }

    @Test
    public void testCardListDoesNotContain() {
        assertFalse(cr.cardListContains("blah"));
        assertFalse(cr.cardListContains("-123"));
    }

}
