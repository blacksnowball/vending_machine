package vending;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    private Product product;
    private ProductCategory type;


    @BeforeEach
    void setUp() {
        product = new Product(type,"Snack",1001,2.00);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getName() {
        assertEquals(product.getName(),"Snack");

    }

    @Test
    void getCode() {
        assertEquals(product.getCode(), 1001);
    }

    @Test
    void getPrice() {
        assertEquals(product.getPrice(),2.00);
    }

    @Test
    void setName() {
        product.setName("Candy");
        assertEquals(product.getName(),"Candy");
    }

    @Test
    void setCode() {
        product.setCode(1002);
        assertEquals(product.getCode(),1002);


    }

    @Test
    void setPrice() {
        product.setPrice(3.00);
        assertEquals(product.getPrice(),3.00);
    }
}