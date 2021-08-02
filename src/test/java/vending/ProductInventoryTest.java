package vending;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductInventoryTest {

    private ProductInventory pi;
    private List<ProductCategory> productCategories;

    @BeforeEach
    public void setup() {
        productCategories = new ArrayList<>();
        productCategories.add(new ProductCategory(null, "Drinks"));
        pi = new ProductInventory(productCategories);
    }

    @Test
    public void testGetProductCategory() {
        assertNotNull(pi.getProductCategory("Drinks"));
        assertNull(pi.getProductCategory("Snacks"));
    }

}