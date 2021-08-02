package vending;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductCategory implements java.io.Serializable {
    private LinkedHashMap<Product, Integer> products;
    private String name;

    public ProductCategory(String name) {
        this.name = name;
        this.products = new LinkedHashMap<>();
    }


    public ProductCategory(LinkedHashMap<Product, Integer> products, String name) {
        this.products = products;
        this.name = name;
    }

    public String reportContents() {

        StringBuilder contents = new StringBuilder();

        for (Product p : products.keySet()) {
            contents.append(p.getName() + "\n");
        }

        return contents.toString();

    }

    public Product getProduct(String name) {

        Product product = null;

        for (Product p : products.keySet()) {

            if (p.getName().equalsIgnoreCase(name)) {
                product = p;
                break;
            }

        }

        return product;

    }


    public LinkedHashMap<Product, Integer> getProducts() {
        return products;
    }


    public String getName() {
        return name;
    }

    public void setProducts(LinkedHashMap<Product, Integer> products) {
        this.products = products;
    }

    public void setName(String name) {
        this.name = name;
    }
}


