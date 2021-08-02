package vending;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public interface SellingBehaviour {

    default void modifyItemName(ProductInventory inv, String productName, String newName) {
        Product p = inv.getProductWithName(productName);
        // Checking to see if the code is valid or the product name is taken
        if(p == null) {
            System.out.println("Product code doesn't exist!");
            return;
        } else if (inv.getProductWithName(newName) != null) {
            System.out.println("Product name is already taken!");
            return;
        }

        p.setName(newName);
        System.out.println("Operation Successful");
    }

    default void modifyItemQty(ProductInventory inv, String productName, int qty) {
        Product p = inv.getProductWithName(productName);
        // Checking to see if the code is valid or the product name is taken
        if(p == null) {
            System.out.println("Product code doesn't exist!");
            return;
        }

        // Checking to see if quantity exceeds 15
        if(qty > 15) {
            System.out.println("Quantity cannot exceed 15");
            return;
        }

        if(qty < 0) {
            System.out.println("Quantity cannot be less than 0");
            return;
        }

        p.getType().getProducts().put(p, qty);
        System.out.println("Operation Successful");
    }

    default void modifyItemPrice(ProductInventory inv, String productName, double price) {
        Product p = inv.getProductWithName(productName);
        // Checking to see if the code is valid or the product name is taken
        if(p == null) {
            System.out.println("Product code doesn't exist!");
            return;
        }

        // Checking to see if price is less than 0
        if(price < 0) {
            System.out.println("Price cannot be negative");
            return;
        }

        p.setPrice(price);
        System.out.println("Operation Successful");
    }

    default void addQuantity(ProductInventory inv, String productName, int qty) {
        Product p = inv.getProductWithName(productName);
        // Checking to see if the code is valid or the product name is taken
        if(p == null) {
            System.out.println("Product code doesn't exist!");
            return;
        }

        ProductCategory pc = p.getType();
        int newQty = qty + pc.getProducts().get(p);
        // Checking to see if qty exceeds 15
        if(newQty > 15) {
            System.out.println("Quantity cannot exceed 15");
            return;
        }

        p.getType().getProducts().put(p, newQty);
        System.out.println("Operation Successful");
    }

    default void modifyItemCategory(ProductInventory inv, String productName, String categoryName) {
        Product p = inv.getProductWithName(productName);
        ProductCategory pc = inv.getProductCategory(categoryName);
        // Checking to see if the code is valid or the product name is taken
        if(p == null) {
            System.out.println("Product code doesn't exist!");
            return;
        }

        boolean newCategory = false;
        if(pc == null) {
            pc = new ProductCategory(categoryName);
            newCategory = true;
        }

        // Add the product into the new category
        pc.getProducts().put(p, p.getType().getProducts().get(p));

        // Remove the product from the old category
        p.getType().getProducts().remove(p);

        p.setType(pc);
        if (newCategory) {
        	inv.getInStock().add(pc);
        }
        // System.out.println("Operation Successful");
    }
    
    default void inventoryReport(ProductInventory inventory) {
        try {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "seller_reports");
            new File(folderPath.toString()).mkdirs();

            FileWriter writer = new FileWriter("seller_reports/inventory_report.csv");

            writer.append("Item Code,Category,Item Name,Price($),Quantity in Stock\n");
    		
            List<ProductCategory> stock = inventory.getInStock();
    		for (ProductCategory category : stock) {
    			String catString = category.getName();
    			LinkedHashMap<Product, Integer> productMap = category.getProducts();
    			Set<Product> productSet = productMap.keySet();
    			for (Product p : productSet) {
    				writer.append(p.getCode() + "," + catString + ","
    						+ p.getName() + "," + String.format("%.2f", p.getPrice())
    						+ "," + category.getProducts().get(p) + "\n");
    			}
    		}

            writer.flush();
            writer.close();

            System.out.println("Created inventory report.");

        } catch (IOException e) {
            System.out.println("Error writing report.");
            e.printStackTrace();
        }
    }
    
    default void salesReport(ProductInventory inventory) {
    	try {
            Path folderPath = Paths.get(System.getProperty("user.dir"), "seller_reports");
            new File(folderPath.toString()).mkdirs();

            FileWriter writer = new FileWriter("seller_reports/sales_report.csv");

            writer.append("Item Code,Item Name,Quantity Sold\n");
    		
            List<ProductCategory> stock = inventory.getInStock();
    		for (ProductCategory category : stock) {
    			LinkedHashMap<Product, Integer> productMap = category.getProducts();
    			Set<Product> productSet = productMap.keySet();
    			for (Product p : productSet) {
    				writer.append(p.getCode() + ","
    						+ p.getName() + "," + p.getAmountSold() + "\n");
    			}
    		}

            writer.flush();
            writer.close();

            System.out.println("Created sales report.");

        } catch (IOException e) {
            System.out.println("Error writing report.");
            e.printStackTrace();
        }
    }

}



