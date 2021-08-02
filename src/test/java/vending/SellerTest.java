package vending;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class SellerTest {
    Seller seller;
    private ProductCategory fruits = new ProductCategory("fruits");
    private ProductCategory snacks = new ProductCategory("snacks");
    private Product apple;
    private Product marsBar;
    private List<ProductCategory> categoriesList = new ArrayList<>();
    private ProductInventory inventory = new ProductInventory(categoriesList);


    @BeforeEach
    public void setup() {
        seller = new Seller("jason", "wow22");
        categoriesList.add(fruits);
        categoriesList.add(snacks);
        apple = new Product(fruits, "apple", 11112222, 2.3);
        marsBar = new Product(snacks, "marsBar", 11112223, 2.4);
        fruits.getProducts().put(apple, 3);
        snacks.getProducts().put(marsBar, 2);
    }

    @Test
    public void testInvalidProductCode() {

        // ensure existing items remain unmodifed

        assertEquals(3, fruits.getProducts().get(apple));
        assertEquals(2, snacks.getProducts().get(marsBar));


        seller.modifyItemPrice(inventory, "test", 1000);
        seller.addQuantity(inventory, "test", 1000);
        seller.modifyItemCategory(inventory, "test", "people");
        seller.modifyItemQty(inventory, "test", 1000);


        assertEquals(3, fruits.getProducts().get(apple));
        assertEquals(2, snacks.getProducts().get(marsBar));



    }

    @Test
    public void testConstruction() {
        assertNotNull(seller);
    }

    @Test
    public void testModifyItemName() {
        seller.modifyItemName(inventory, "apple", "yummy apple");
        assertEquals("yummy apple", apple.getName());
    }

    @Test
    public void testModifyItemNameDoesntExist() {
        // Shouldn't modify anything because product name is invalid
        seller.modifyItemName(inventory, "mars bar", "mars bar");
        assertEquals("marsBar", marsBar.getName());
    }

    @Test
    public void testModifyItemNameAlreadyExist() {
        // Shouldn't modify anything because product name already exists
        seller.modifyItemName(inventory, "marsBar", "apple");
        assertEquals("marsBar", marsBar.getName());
    }

    @Test
    public void testModifyItemQty() {
        seller.modifyItemQty(inventory, "apple", 5);
        assertEquals(5, fruits.getProducts().get(apple));
    }

    @Test
    public void testModifyItemQtyOutOfRange() {
        seller.modifyItemQty(inventory, "apple", 55);
        assertEquals(3, fruits.getProducts().get(apple));
        seller.modifyItemQty(inventory, "apple", -2);
        assertEquals(3, fruits.getProducts().get(apple));
    }

    @Test
    public void testAddQty() {
        seller.addQuantity(inventory, "apple", 5);
        assertEquals(8, fruits.getProducts().get(apple));
    }

    @Test
    public void testAddQtyOutOfRange() {
        seller.addQuantity(inventory, "apple", 235);
        assertEquals(3, fruits.getProducts().get(apple));
    }

    @Test
    public void testModifyItemPrice() {
        seller.modifyItemPrice(inventory, "apple", 2.67);
        assertEquals(2.67, apple.getPrice());
    }

    @Test
    public void testModifyItemPriceOutOfRange() {
        seller.modifyItemPrice(inventory, "apple", -2.67);
        assertEquals(2.3, apple.getPrice());
    }

    @Test
    public void testModifyItemCategory() {
        seller.modifyItemCategory(inventory, "apple", "snacks");
        assertEquals(inventory.getProductWithName("apple").getType(), snacks);
    }

    @Test
    public void testModifyItemNewCategory() {
        assertEquals(inventory.getInStock().size(), 2);
        assertNotNull(fruits.getProducts().get(apple));


        seller.modifyItemCategory(inventory, "apple", "snackySnack");


        // Checking to see that inventory has a new category
        assertEquals(inventory.getInStock().size(), 3);

        // Checking to see that apple product has an updated category name
        assertEquals(inventory.getProductWithName("apple").getType().getName(), "snackySnack");

        // Checking to see that apple has been added to the new category "snackySnack"
        assertEquals(inventory.getProductCategory("snackySnack").getProducts().get(apple), 3);

        // Checking to see that apple has been removed from the old category
        assertNull(fruits.getProducts().get(apple));
    }

    @Test
    public void testSellingReports() {

        try {
            seller.inventoryReport(inventory);
            seller.salesReport(inventory);
        } catch (Throwable t) {
            throw new Error("This should not happen");
        }
    }


}
