package vending;

import java.util.ArrayList;
import java.util.List;

public class ProductInventory implements java.io.Serializable {
    private List<ProductCategory> inStock;

    public ProductInventory(List<ProductCategory> inStock) {
        this.inStock = inStock;
    }

    public ProductCategory getProductCategory(String name) {
        for (ProductCategory pc : inStock) {
            if (pc.getName().equalsIgnoreCase(name)) {
                return pc;
            }
        }
        return null;
    }

    public List<Product> catProduct(ProductCategory cat) {

        if (cat == null) {
            return null;
        }

        List<Product> productList = new ArrayList<>();
        for (Product i : cat.getProducts().keySet()) {
            productList.add(i);
        }

        return productList;
    }

    public Boolean checkexitProduct(Product product) {

        Boolean exitProduct = false;

        //find the product in the category <instock>
        for (ProductCategory i : inStock) {
            for (Product j : i.getProducts().keySet()) {
                if (j.getCode() == product.getCode()) ;
                {
                    exitProduct = true;
                }
            }
        }
        return exitProduct;

    }


    public Boolean addProduct(Product product, int quality) {
        if (product == null || quality <= 0) {
            return null;
        }
        Boolean successAddProduct = false;
        try {

            //if the Product isn't exits in the whole ProductInventory ( Never Exit Before )
            // To do: Create a new ProductCategory to ProductInventory and add the Product
            if (!checkexitProduct(product)) {
                System.out.println("No Found Product in any Category, Maybe Add a New Product");
                System.out.println("ProductInventory addProduct Method: Trying to add the Product into the store stock");
                inStock.add(product.getType());
                inStock.get(inStock.size() - 1).getProducts().put(product, quality);
                System.out.println("ProductInv addProduct Method: Please Check If the Product is being added correctly");


            }
            //Product should exit from here
            // 1. Get the index of the Category from the Inventory
            int index = inStock.indexOf(product.getType());
            //2. Add the existent amount of product in the Category Hashmap with the amount given
            inStock.get(index).getProducts().put(product, inStock.get(index).getProducts().get(product) + quality);


            //Checking for the amount of product in the


        } catch (Exception e) {
            System.out.println("Try to add but not success");


        }
        return successAddProduct;


    }

    public Boolean removeProduct(Product p, int qua) {

        if (p == null || qua <= 0) {
            return null;
        }


        Boolean successremove = false;

        if (!checkexitProduct(p)) {
            System.out.println("Can't find the product inside the category, maybe try to add it first");


        }
        try
        // If the product exits in the cat;
        {
            int index = inStock.indexOf(p.getType());
            int amountExit = inStock.get(index).getProducts().get(p);
            if (amountExit < qua) {
                System.out.println("Not enough amount to remove");

            }
            //If the amount is enough to remove

            inStock.get(index).getProducts().put(p, inStock.get(index).getProducts().get(p) - qua);
            return successremove = true;
        } catch (Exception e) {
            System.out.println("Try to remove product amount but unable to do so");

        }
        return successremove;

    }

    public List<ProductCategory> getInStock() {
        return this.inStock;
    }

    public Product getProductWithCode(int code) {
        for(ProductCategory pc : inStock) {
            for(Product p : pc.getProducts().keySet()) {
                if (p.getCode() == code) {
                    return p;
                }
            }
        }
        return null;
    }

    public Product getProductWithName(String name) {
        for(ProductCategory pc : inStock) {
            for(Product p : pc.getProducts().keySet()) {
                if (p.getName().equalsIgnoreCase(name)) {
                    return p;
                }
            }
        }
        return null;
    }
}


