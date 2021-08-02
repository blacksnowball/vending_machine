package vending;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryTest {
    private ProductCategory drink;
    private LinkedHashMap<Product, Integer> products;
    private List<Product> list_of_product_name;


    @BeforeEach
    void setUp() {
        products = new LinkedHashMap<>();
        list_of_product_name = new ArrayList<>();

        Product milk = new Product(drink, "milk", 1003, 2.00);
        Product coke = new Product(drink, "coke", 1004, 3.00);
        Product water = new Product(drink, "water", 1005, 1.50);
        Product pepsi = new Product(drink, "pepsi", 1007, 2.50);
        Product tea = new Product(drink, "tea", 1006, 4.00);

        products.put(milk, 3);
        products.put(coke, 4);
        products.put(water, 5);
        products.put(pepsi, 6);
        products.put(tea, 7);
        list_of_product_name.add(milk);
        list_of_product_name.add(coke);
        list_of_product_name.add(water);
        list_of_product_name.add(pepsi);
        list_of_product_name.add(tea);
        // for (Product i : drink.getProducts().keySet())
        //   System.out.println(i.getName());

        drink = new ProductCategory(products, "drink");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetProduct() {

        assertNotNull(drink.getProduct("milk"));
        assertNotNull(drink.getProduct("coke"));
        assertNotNull(drink.getProduct("water"));
        assertNotNull(drink.getProduct("tea"));
        assertNotNull(drink.getProduct("pepsi"));


        Product water = new Product(drink, "water", 1006, 4.00);
        products.put(water, 4);
        //assertEquals(water, drink.getProduct("water"));
        assertEquals(4, drink.getProducts().get(water));

    }

    @Test
    void getProducts() {
        try {
            //assertEquals(drink.getProducts().size(), 5);
        }
        catch (Exception e ){
            System.out.println("Failed  setProducts");
        }
        int i = 0;
        for (Map.Entry Product_name : products.entrySet()) {
            Product key = (Product) Product_name.getKey();
            // assertEquals(key.getName(), list_of_product_name.get(i).getName());
            i += 1;


        }
    }

    @Test
    void getName () {
        assertEquals(drink.getName(), "drink");
    }

    @Test
    public void testEmptyProducts() {

        ProductCategory snacks = new ProductCategory("snacks");
        assertNull(snacks.getProduct("chips"));

    }

    @Test
    public void setProducts () {
        Product sevenup = new Product(drink, "sevenup", 1003, 2.00);
        LinkedHashMap<Product, Integer> newDrinks = new LinkedHashMap<>();
        newDrinks.put(sevenup, 3);

        drink.setProducts(newDrinks);
        try {
            assertEquals(drink.getProducts(), newDrinks);
        }
        catch (Exception e){
            System.out.println("This Test Failed");
        }

    }

    @Test
    public void setName(){
        drink.setName("new drink");
        assertEquals(drink.getName(), "new drink");
    }

    @Test
    public void testReportContents() {
        assertNotNull(drink.reportContents());
    }
}






